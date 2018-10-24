package moe.gc_uwu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    boolean updateAvail = false;
    String updateUrl = "";

    static int tapped = 0;
    TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
                Toast.makeText(MainActivity.this, "You're on latest version available!",Toast.LENGTH_SHORT).show();
            }
            return true;
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
            //Toast.makeText(MainActivity.this, "Not implemented",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MyPageActivity.class);
            intent.putExtra("mode",1);
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
}
