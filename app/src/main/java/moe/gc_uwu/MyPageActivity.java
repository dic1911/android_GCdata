package moe.gc_uwu;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;



/*import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;*/

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.CookieManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MyPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String cardID;
    String passwd;
    Button fetch;
    TextView top;
    Boolean ready;
    static Boolean alreadyLoggedIn;
    static Boolean dataFetched;
    int mode;

    mypageThread thread;

    static ArrayList<musicTemplate> musicList;
    ArrayList<scoreTemplate> scoreData;

    songListAdapter adapter;
    ListView listView;

    SharedPreferences login;
    static CookieManager cookieManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        // init page
        if(getIntent().getExtras() != null){
            mode = 1;
            setTitle("My Page (beta) - Scores");
        }else{
            mode = 0;
            setTitle("My Page (beta)");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        top = (TextView) findViewById(R.id.text_in_mypage);
        setSupportActionBar(toolbar);
        fetch = (Button) findViewById(R.id.btn_fetch);
        ready = false;
        dataFetched = false;
        if(alreadyLoggedIn == null)
            alreadyLoggedIn = false;
        listView = (ListView) findViewById(R.id.score_list);
        //fetch.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        login = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        cardID = login.getString("cardID", "");
        passwd = login.getString("passwd", "");

        if(cardID != "" && passwd != ""){
            fetch.setVisibility(View.VISIBLE);
            top.setText("\n\n" + cardID.substring(0,12) + "****");
            ready = true;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("GCdata-score", "music list item " + i + " selected.");
                String ID = musicList.get(i).getId();
                Log.d("GCdata-score", ID);
                int music_id = Integer.parseInt(ID);
                String url = "https://mypage.groovecoaster.jp/sp/json/music_detail.php?music_id=" + music_id;
                scoreTemplate song;
                Boolean hasEx;
                JSONObject fullData;

                try{
                    if(cookieManager != null) {
                        thread = new mypageThread(url, cookieManager);
                        thread.start();
                        thread.join();
                    }else{
                        Toast.makeText(MyPageActivity.this, "Cookie is invalid! Tried restart the app?",Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    fullData = thread.getStat();
                    if(fullData.getJSONObject("music_detail").getInt("ex_flag") != 0){
                        hasEx = true;
                    }else{ hasEx = false; }


                    JSONObject detail = fullData.getJSONObject("music_detail");

                    JSONObject s;
                    JSONObject n;
                    JSONObject h;
                    JSONObject e;
                    JSONObject blk = new JSONObject();
                    blk.put("blank", true);

                    if(detail.isNull("simple_result_data")){ s = blk; }else{ s = detail.getJSONObject("simple_result_data"); }
                    if(detail.isNull("normal_result_data")){ n = blk; }else{ n = detail.getJSONObject("normal_result_data"); }
                    if(detail.isNull("hard_result_data")){ h = blk; }else{ h = detail.getJSONObject("hard_result_data"); }
                    if(!hasEx || detail.isNull("extra_result_data")){ e = blk; }else{ e = detail.getJSONObject("extra_result_data"); }

                    if(hasEx){
                        song = new scoreTemplate(ID, musicList.get(i).getTitle(), detail.getString("artist"),
                                hasEx, s, n, h, e, detail.getJSONArray("user_rank"), detail);
                    }else{
                        song = new scoreTemplate(ID, musicList.get(i).getTitle(), detail.getString("artist"),
                                hasEx, s, n, h, detail.getJSONArray("user_rank"), detail);
                    }

                    Intent scoreIntent = new Intent(MyPageActivity.this, MyScoreActivity.class);
                    song.dataToIntent(scoreIntent);
                    Log.d("GCdata-score", "Starting score display activity");
                    startActivity(scoreIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        fetch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && ready){
                    top.setText("\n\nLoading...");
                    if(mode == 0) {
                        // grab basic stat
                        if(!alreadyLoggedIn) {
                            thread = new mypageThread(cardID, passwd);
                            thread.start();
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();

                            }
                            cookieManager = thread.getCookieManager();
                        }else{
                            thread = new mypageThread("https://mypage.groovecoaster.jp/sp/json/player_data.php", cookieManager);
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        //top.append("\n" + thread.getResult());
                        String tmp = "";

                        try {
                            String total_stage = thread.getStat().getJSONObject("stage").getString("all");
                            tmp += ("\n\n" + thread.getStat().getJSONObject("player_data").getString("player_name") + "\n\n");
                            tmp += ("Score: " + thread.getStat().getJSONObject("player_data").getString("total_score") + "\n");
                            tmp += ("Avg. Score: " + thread.getStat().getJSONObject("player_data").getString("average_score") + "\n");
                            tmp += ("Played Songs: " + thread.getStat().getJSONObject("player_data").getString("total_play_music") + " / ");
                            tmp += (thread.getStat().getJSONObject("player_data").getString("total_music") + "\n");
                            tmp += ("Rank: " + thread.getStat().getJSONObject("player_data").getString("rank") + "\n");
                            tmp += ("Avatar: " + thread.getStat().getJSONObject("player_data").getString("avatar") + "\n");
                            tmp += ("Title: " + thread.getStat().getJSONObject("player_data").getString("title") + "\n");
                            tmp += ("Trophy: " + thread.getStat().getJSONObject("player_data").getString("total_trophy") + "\n");
                            tmp += ("Trophy Rank: " + thread.getStat().getJSONObject("player_data").getString("trophy_rank") + "\n\n");
                            tmp += ("Cleared: " + thread.getStat().getJSONObject("stage").getString("clear") + " / " + total_stage + "\n");
                            tmp += ("No Miss: " + thread.getStat().getJSONObject("stage").getString("nomiss") + " / " + total_stage + "\n");
                            tmp += ("Full Chain: " + thread.getStat().getJSONObject("stage").getString("fullchain") + " / " + total_stage + "\n");
                            tmp += ("Perfect: " + thread.getStat().getJSONObject("stage").getString("perfect") + " / " + total_stage + "\n\n");
                            tmp += ("Rank S: " + thread.getStat().getJSONObject("stage").getString("s") + " / " + total_stage + "\n");
                            tmp += ("Rank S+: " + thread.getStat().getJSONObject("stage").getString("ss") + " / " + total_stage + "\n");
                            tmp += ("Rank S++: " + thread.getStat().getJSONObject("stage").getString("sss") + " / " + total_stage + "\n");
                            top.setText(tmp);
                        } catch (Exception e) {
                            Toast.makeText(MyPageActivity.this, "Error when making request, wrong password?",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }else if(mode == 1){
                        // grab music list
                        if(!alreadyLoggedIn) {
                            thread = new mypageThread("https://mypage.groovecoaster.jp/sp/json/music_list.php", cardID, passwd);
                            thread.start();
                            scoreData = new ArrayList<>();
                            musicList = new ArrayList<>();
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            cookieManager = thread.getCookieManager();
                        }else{
                            thread = new mypageThread("https://mypage.groovecoaster.jp/sp/json/music_list.php", cookieManager);
                            try {
                                thread.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        String tmp;
                        try{
                            JSONArray songs = thread.getStat().getJSONArray("music_list");
                            int len = songs.length();
                            for(int i=0; i<len; i++){
                                musicList.add(new musicTemplate(songs.getJSONObject(i).getString("music_id"),
                                        songs.getJSONObject(i).getString("music_title")));
                            }
                            adapter = new songListAdapter(musicList, getApplicationContext());
                            listView.setAdapter(adapter);

                            top.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }catch(Exception e){
                            Toast.makeText(MyPageActivity.this, "Error when making request, wrong password?",Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                    //alreadyLoggedIn = true;
                    dataFetched = true;
                    return true;
                }
                    return false;
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        cardID = login.getString("cardID", "");
        passwd = login.getString("passwd", "");
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
        getMenuInflater().inflate(R.menu.my_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_score_backup) {
            if(!dataFetched || mode == 0){
                Toast.makeText(MyPageActivity.this, "Fetch song list before back things up",Toast.LENGTH_LONG).show();
            }else {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD_HHmmss");
                File file = new File(getExternalFilesDir(null), "gcdata_score-" + sdf.format(cal.getTime()) + ".csv");
                scoreBackupThread bkpThread = new scoreBackupThread(this, file.getAbsolutePath(), cookieManager, musicList);
                bkpThread.start();
                Toast.makeText(MyPageActivity.this, "Backing up all the scores to local storage..", Toast.LENGTH_LONG).show();
                Toast.makeText(MyPageActivity.this, "Target: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
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
            startActivity(intent);
        } else if (id == R.id.nav_area) {
            Toast.makeText(this, "Not implemented",Toast.LENGTH_LONG).show();
        } else if(id == R.id.nav_stat) {
            if(mode == 1){
                Intent intent = new Intent(this, MyPageActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_score) {
            if(mode == 0){
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("score","true");
                startActivity(intent);
            }
        } else {
            //Toast.makeText(MyPageActivity.this, "Not implemented",Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
