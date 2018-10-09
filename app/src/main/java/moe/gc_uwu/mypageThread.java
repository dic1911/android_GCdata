package moe.gc_uwu;

import android.util.Log;

import org.json.JSONObject;
import org.w3c.dom.Document;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.FormBody;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class mypageThread extends Thread implements Runnable {

    String url;
    String loginUrl;
    String cardID;
    String passwd;
    String res;
    RequestBody formBody;
    Request request;
    Response response;
    URL Url;
    JSONObject stat;

    CookieManager cookieManager;
    OkHttpClient client;

    mypageThread(String cardID, String passwd){
        this.loginUrl = "https://mypage.groovecoaster.jp/sp/login/auth_con.php";
        this.url = "https://mypage.groovecoaster.jp/sp/json/player_data.php";
        this.cardID = cardID;
        this.passwd = passwd;
        try {
            Url = new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    mypageThread(String url, String cardID, String passwd){
        this.loginUrl = "https://mypage.groovecoaster.jp/sp/login/auth_con.php";
        this.url = url;
        this.cardID = cardID;
        this.passwd = passwd;
        try {
            Url = new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    mypageThread(String url, CookieManager cookieManager){
        this.loginUrl = "";
        this.url = url;
        this.cardID = "";
        this.passwd = "";
        this.cookieManager = cookieManager;
        try {
            Url = new URL(this.url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public CookieManager getCookieManager(){
        return this.cookieManager;
    }

    public JSONObject getStat(){ return stat;}

    @Override
    public void run() {
        Log.d("GCdata-mypage", "mypageThread.run()");
        try {
            if(loginUrl != "") {
                formBody = new FormBody.Builder()
                        .add("nesicaCardId", cardID)
                        .add("password", passwd)
                        .build();
                request = new Request.Builder()
                        .url(loginUrl)
                        .post(formBody)
                        .build();

                if(cookieManager == null)
                    cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                client = getUnsafeOkHttpClient(cookieManager);
                try {
                    response = client.newCall(request).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                client = getUnsafeOkHttpClient(cookieManager);
            }
            request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            res = response.body().string();
            stat = new JSONObject(res);

            Log.d("GCdata-mypage", "=====REPLY START=====\n"+res+"\n=====REPLY  END =====");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient(CookieManager cookieManager) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {}

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.cookieJar(new JavaNetCookieJar(cookieManager)).build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
