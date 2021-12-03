package moe.gc_uwu;

import android.util.Log;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class globalRankThread extends Thread implements Runnable {
    String Pos;
    NodeList names;
    NodeList scores;
    NodeList titles;
    NodeList sites;
    NodeList loc;
    NodeList locId;

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder;

    String url[]={"https://groovecoaster.jp/xml/fmj2100/rank/all/rank_",".xml"};

    private String tmp;

    globalRankThread(String Pos){
        this.Pos = Pos;
    }

    public NodeList getNames(){
        return this.names;
    }

    public NodeList getSites(){
        return this.sites;
    }

    public NodeList getScores(){
        return this.scores;
    }

    public NodeList getTitles(){
        return this.titles;
    }

    public NodeList getLoc(){ return this.loc; }

    public NodeList getLocId(){ return this.locId; }

    private String readStream(InputStream is) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = is.read();
            while(i != -1) {
                bo.write(i);
                i = is.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }

    @Override
    public void run() {
        try {
            URL Url = null;
            try {
                Url = new URL(url[0] + Pos + url[1]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) Url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                tmp = readStream(in);
                try {
                    builder = factory.newDocumentBuilder();
                    StringReader sr = new StringReader(tmp);
                    Document data = builder.parse(new InputSource(sr));
                    names = data.getElementsByTagName("player_name");
                    scores = data.getElementsByTagName("score_bi1");
                    titles = data.getElementsByTagName("title");
                    loc = data.getElementsByTagName("pref");
                    locId = data.getElementsByTagName("pref_id");
                    sites = data.getElementsByTagName("tenpo_name");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
            } finally {
                urlConnection.disconnect();
            }
        }finally {
            Log.d("GCdata", "http thread finished. (" + String.valueOf(Pos) + ")");
        }
    }
}
