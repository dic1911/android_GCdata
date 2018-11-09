package moe.gc_uwu;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.CookieManager;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int lang_id = 1000;
    boolean updateAvail = false;
    String updateUrl = "";

    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set locale stuff here

        SharedPreferences lang_pref = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = this.getResources().getConfiguration();
        String lang = lang_pref.getString("locale", "");

        if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
            Intent refresh = new Intent(this, MainActivity.class);
            startActivity(refresh);
        }

        // then proceed to get things ready

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        text = (TextView) findViewById(R.id.text_in_main);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String repo = "https://030.cdpa.nsysu.edu.tw:1030/dic1911/android_GCdata";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(repo));
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AsyncCheckUpdate updateChecker = new AsyncCheckUpdate();
        updateChecker.execute();
    }

    @Override
    public void onResume(){
        super.onResume();
        text.setText(getString(R.string.app_desc));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SubMenu subm = menu.getItem(0).getSubMenu(); // get my MenuItem with placeholder submenu;
        subm.clear(); // delete place holder
        subm.add(0, lang_id + 0, 0, R.string.lang_en);
        subm.add(0, lang_id + 1, 1, R.string.lang_zh);
        subm.add(0, lang_id + 2, 2, R.string.lang_jp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_update) {
            if(updateAvail){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(updateUrl));
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this, getString(R.string.already_on_latest_ver),Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == lang_id) {
            setLocale(this, Locale.ENGLISH);
            updateLanguage(this, Locale.ENGLISH.getLanguage());
            restartApp(this, MainActivity.class);
            //updateViews("en");
        } else if (id == lang_id + 1) {
            setLocale(this, Locale.TAIWAN);
            updateLanguage(this, Locale.TAIWAN.getLanguage());
            restartApp(this, MainActivity.class);
        } else if (id == lang_id + 2) {
            setLocale(this, Locale.JAPAN);
            updateLanguage(this, Locale.JAPAN.getLanguage());
            restartApp(this, MainActivity.class);
            //updateViews("ja");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_global) {
            Intent intent = new Intent(this, GlobalRankActivity.class);
            intent.putExtra("mode", 0);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(this, MonthlyRankActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_area) {
            Intent intent = new Intent(this, GlobalRankActivity.class);
            intent.putExtra("mode", 1);
            startActivity(intent);
        } else if (id == R.id.nav_stat) {
            Intent intent = new Intent(this, MyPageActivity.class);
            intent.putExtra("mode",0);
            startActivity(intent);
        } else if (id == R.id.nav_score) {
            Intent intent = new Intent(this, MyPageActivity.class);
            intent.putExtra("mode",1);
            startActivity(intent);
        } else if (id == R.id.nav_monthly_stat) {
            Intent intent = new Intent(this, MonthlyStatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            Intent intent = new Intent(this, MyPageActivity.class);
            intent.putExtra("mode",2);
            startActivity(intent);
        } else if (id == R.id.nav_friend) {
            Intent intent = new Intent(this, MyPageActivity.class);
            intent.putExtra("mode",3);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AsyncCheckUpdate extends AsyncTask<Integer, Void, String> {

        JSONObject res;

        @Override
        protected String doInBackground(Integer... params) {
            OkHttpClient client = mypageThread.getUnsafeOkHttpClient(new CookieManager());
            Request request;
            String url = "https://030.cdpa.nsysu.edu.tw:1030/dic1911/android_GCdata/raw/branch/master/update.json";

            request = new Request.Builder().url(url).build();
            try {
                res = new JSONObject(client.newCall(request).execute().body().string());
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                if(res.getInt("version") > pInfo.versionCode){
                    updateUrl = res.getString("url");
                    updateAvail = true;
                    Toast.makeText(MainActivity.this, "Update available!\nUse the menu to download the update.",Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    /*   Locale changing related function
          Credit: federicoiosue on GitHub    */

    @SuppressLint("ApplySharedPref")
    public static Context updateLanguage(Context ctx, String lang) {
        SharedPreferences prefs = ctx.getSharedPreferences("language", MODE_MULTI_PROCESS);
        String language = prefs.getString("language", "");

        Locale locale = null;
        if (TextUtils.isEmpty(language) && lang == null) {
            locale = Locale.getDefault();
            prefs.edit().putString("language", locale.toString()).commit();
        } else if (lang != null) {
            locale = getLocale(lang);
            prefs.edit().putString("language", lang).commit();
        } else if (!TextUtils.isEmpty(language)) {
            locale = getLocale(language);
        }

        return setLocale(ctx, locale);
    }

    private static Context setLocale(Context context, Locale locale) {
        SharedPreferences lang_pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = lang_pref.edit();
        Configuration configuration = context.getResources().getConfiguration();

        editor.putString("locale", locale.toString());
        editor.apply();
        editor.commit();

        configuration.setLocale(locale);
        context.createConfigurationContext(configuration);
        return context;
    }

    /**
     * Checks country AND region
     */
    public static Locale getLocale(String lang) {
        if (lang.contains("_")) {
            return new Locale(lang.split("_")[0], lang.split("_")[1]);
        } else {
            return new Locale(lang);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    public static String getLocalizedString(Context context, String desiredLocale, int resourceId) {
        Configuration conf = context.getResources().getConfiguration();
        conf = new Configuration(conf);
        conf.setLocale(getLocale(desiredLocale));
        Context localizedContext = context.createConfigurationContext(conf);
        return localizedContext.getResources().getString(resourceId);
    }

    public void restartApp(final Context mContext, Class activityClass) {
		Intent intent = new Intent(mContext, activityClass);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		int mPendingIntentId = Long.valueOf(Calendar.getInstance().getTimeInMillis()).intValue();
		PendingIntent mPendingIntent = PendingIntent.getActivity(mContext, mPendingIntentId, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 120, mPendingIntent);
        System.exit(0);
    }

}
