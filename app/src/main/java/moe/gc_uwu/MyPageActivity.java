package moe.gc_uwu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
    static Boolean dbHasData;
    int mode;

    Boolean friendScore;
    String friendName;
    String friendHash;

    mypageThread thread;
    int total_score;
    int avg_score;
    int rank;
    int last_total_score;
    int last_avg_score;
    int last_rank;

    static ArrayList<musicTemplate> musicList;
    static ArrayList<friendTemplate> friendList;
    ArrayList<scoreTemplate> scoreData;

    songListAdapter adapter;
    friendListAdapter fAdapter;
    ListView listView;

    SQLiteDatabase db;
    SharedPreferences login;
    SharedPreferences mypage_pref;
    SharedPreferences.Editor pref_edit;
    static CookieManager cookieManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        // init page
        friendScore = false;
        mode = getIntent().getExtras().getInt("mode");
        switch (mode) {
            case (0):{
                setTitle(getString(R.string.my_page)); break;
            }
            case (1):{
                if(getIntent().getExtras().containsKey("friendHash")){
                    friendName = getIntent().getExtras().getString("friendName");
                    friendName = friendName.split("\\[")[0];
                    setTitle(getString(R.string.my_page) + " - " + getString(R.string.scores) + " - " + friendName);
                    friendScore = true;
                    friendHash = getIntent().getExtras().getString("friendHash");
                }else{
                    setTitle(getString(R.string.my_page) + " - " + getString(R.string.scores));
                }
                break;
            }
            case (2):{
                setTitle(getString(R.string.my_page) + " - " + getString(R.string.event)); break;
            }
            case (3):{
                setTitle(getString(R.string.my_page) + " - " + getString(R.string.friends)); break;
            }
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

        mypage_pref = this.getSharedPreferences("mypage", Context.MODE_PRIVATE);
        pref_edit = mypage_pref.edit();
        dbHasData = false;
        if(mypage_pref.getBoolean("dbHasData", false)){
            dbHasData = true;
        }

        db = openOrCreateDatabase("mypage", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS stats(id integer primary key,time timestamp default (strftime('%s', 'now'))," +
                    "total_score integer, avg_score integer, rank integer);");


        if(cardID != "" && passwd != ""){
            fetch.setVisibility(View.VISIBLE);
            top.setText("\n\n" + cardID.substring(0,12) + "****");
            ready = true;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("GCdata-mypage", "list item " + i + " selected.");
                if (mode == 1) {
                    String ID = musicList.get(i).getId();
                    Log.d("GCdata-score", ID);
                    int music_id = Integer.parseInt(ID);
                    String url;
                    if(!friendScore)
                        url = "https://mypage.groovecoaster.jp/sp/json/music_detail.php?music_id=" + music_id;
                    else{
                        url = "https://mypage.groovecoaster.jp/sp/json/friend_music_detail.php?music_id=" + music_id + "&hash=" + friendHash;
                    }

                    scoreTemplate song;
                    Boolean hasEx;
                    JSONObject fullData;

                    try {
                        if (cookieManager != null) {
                            thread = new mypageThread(url, cookieManager);
                            thread.start();
                            thread.join();
                        } else {
                            Toast.makeText(MyPageActivity.this, getString(R.string.mypage_cookie_error), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        fullData = thread.getStat();
                        if (fullData.getJSONObject("music_detail").getInt("ex_flag") != 0) {
                            hasEx = true;
                        } else {
                            hasEx = false;
                        }


                        JSONObject detail = fullData.getJSONObject("music_detail");

                        JSONObject s;
                        JSONObject n;
                        JSONObject h;
                        JSONObject e;
                        JSONObject blk = new JSONObject();
                        blk.put("blank", true);

                        if (detail.isNull("simple_result_data")) {
                            s = blk;
                        } else {
                            s = detail.getJSONObject("simple_result_data");
                        }
                        if (detail.isNull("normal_result_data")) {
                            n = blk;
                        } else {
                            n = detail.getJSONObject("normal_result_data");
                        }
                        if (detail.isNull("hard_result_data")) {
                            h = blk;
                        } else {
                            h = detail.getJSONObject("hard_result_data");
                        }
                        if (!hasEx || detail.isNull("extra_result_data")) {
                            e = blk;
                        } else {
                            e = detail.getJSONObject("extra_result_data");
                        }

                        if (hasEx) {
                            song = new scoreTemplate(ID, musicList.get(i).getTitle(), detail.getString("artist"),
                                    hasEx, s, n, h, e, detail.getJSONArray("user_rank"), detail);
                        } else {
                            song = new scoreTemplate(ID, musicList.get(i).getTitle(), detail.getString("artist"),
                                    hasEx, s, n, h, detail.getJSONArray("user_rank"), detail);
                        }

                        Intent scoreIntent = new Intent(MyPageActivity.this, MyScoreActivity.class);
                        song.dataToIntent(scoreIntent);
                        Log.d("GCdata-score", "Starting score display activity");
                        startActivity(scoreIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (mode == 3) {
                    // load stat for the selected friend

                    try {
                        if (thread.getStat().getInt("status") != 0) {
                            Toast.makeText(MyPageActivity.this, getString(R.string.mypage_response_error), Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = new Intent(MyPageActivity.this, MyFriendActivity.class);
                            intent.putExtra("login", cardID);
                            intent.putExtra("passwd", passwd);
                            intent.putExtra("name", friendList.get(i).title);
                            intent.putExtra("hash", friendList.get(i).hash);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (ready) {
            top.setText("\n\nLoading...");
            new AsyncGrabData().execute(mode);
        }

        fetch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN && ready){
                    top.setText("\n\nLoading...");
                    new AsyncGrabData().execute(mode);

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
    public void onDestroy(){
        super.onDestroy();
        db.close();
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
        if(mode == 1)
            getMenuInflater().inflate(R.menu.my_page_score, menu);
        else
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
            // todo: show the progress somewhere on screen
            if(!dataFetched || mode != 1){
                Toast.makeText(MyPageActivity.this, getString(R.string.mypage_score_backup_not_ready),Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MyPageActivity.this, getString(R.string.mypage_score_backup_starting), Toast.LENGTH_LONG).show();
                new AsyncGrabData().execute(8);
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
            intent.putExtra("mode", 0);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(this, MonthlyRankActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_area) {
            Intent intent = new Intent(this, GlobalRankActivity.class);
            intent.putExtra("mode", 1);
            startActivity(intent);
        } else if(id == R.id.nav_stat) {
            if(mode != 0){
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode",0);
                startActivity(intent);
            }
        } else if (id == R.id.nav_score) {
            if(mode != 1){
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode", 1);
                startActivity(intent);
            }
        } else if (id == R.id.nav_monthly_stat) {
            Intent intent = new Intent(this, MonthlyStatActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            if(mode != 2) {
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode", 2);
                startActivity(intent);
            }
        } else if (id == R.id.nav_friend) {
            if(mode != 3) {
                Intent intent = new Intent(this, MyPageActivity.class);
                intent.putExtra("mode", 3);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class AsyncGrabData extends AsyncTask<Integer, Void, String> {

        StringBuilder tmp;
        boolean backupF;
        String reqURL;

        @Override
        protected String doInBackground(Integer... params) {
            backupF = false;
            if(params[0] == 0) {
                tmp = new StringBuilder();

                // grab basic stat
                thread = new mypageThread(cardID, passwd);
                thread.start();
            } else if(params[0] == 1) {
                // grab music list
                if(!friendScore)
                    reqURL = "https://mypage.groovecoaster.jp/sp/json/music_list.php";
                else
                    reqURL = "https://mypage.groovecoaster.jp/sp/json/friend_music_list.php?hash=" + friendHash;
                scoreData = new ArrayList<>();
                musicList = new ArrayList<>();
            } else if(params[0] == 2) {
                reqURL = "https://mypage.groovecoaster.jp/sp/json/event_data.php";
            } else if(params[0] == 3) {
                friendList = new ArrayList<>();
                reqURL = "https://mypage.groovecoaster.jp/sp/json/friend_list.php";
            } else if(params[0] == 8) {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD_HHmmss");
                String filename;

                if(!friendScore) {
                    filename = "gcdata_score-" + sdf.format(cal.getTime()) + ".csv";
                } else {
                    filename = "gcdata_score-" + friendName + "-" + sdf.format(cal.getTime()) + ".csv";
                }

                File file = new File(getExternalFilesDir(null), filename);
                scoreBackupThread bkpThread;
                if(!friendScore) {
                    bkpThread = new scoreBackupThread(MyPageActivity.this, file.getAbsolutePath(), cookieManager, musicList);
                } else {
                    bkpThread = new scoreBackupThread(MyPageActivity.this, file.getAbsolutePath(), cookieManager, musicList, friendHash);
                }
                bkpThread.start();
                try {
                    bkpThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                backupF = true;
            }

            if(params[0] != 0 && params[0] != 8){
                if (!alreadyLoggedIn) {
                    thread = new mypageThread(reqURL, cardID, passwd);
                } else {
                    thread = new mypageThread(reqURL, cookieManager);
                }

                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // save cookie for everyone uwu
                cookieManager = thread.getCookieManager();

                if(params[0] == 1){
                    try {
                        JSONArray songs = thread.getStat().getJSONArray("music_list");
                        int len = songs.length();
                        for (int i = 0; i < len; i++) {
                            musicList.add(new musicTemplate(songs.getJSONObject(i).getString("music_id"),
                                    songs.getJSONObject(i).getString("music_title")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MyPageActivity.this, getString(R.string.mypage_exception), Toast.LENGTH_LONG).show();
                    }
                }else if(params[0] == 3){
                    try {
                        JSONArray friends = thread.getStat().getJSONArray("friendList");
                        int len = friends.length();
                        for (int i = 0; i < len; i++) {
                            friendList.add(new friendTemplate(String.valueOf(i+1),
                                    friends.getJSONObject(i).getString("name") +
                                         "   [" + friends.getJSONObject(i).getString("title") + "]",
                                         friends.getJSONObject(i).getString("card_id")));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MyPageActivity.this, getString(R.string.mypage_exception), Toast.LENGTH_LONG).show();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                if(mode == 0){
                    try {
                        thread.join();

                        total_score = Integer.parseInt(thread.getStat().getJSONObject("player_data").getString("total_score"));
                        avg_score = thread.getStat().getJSONObject("player_data").getInt("average_score");
                        rank = thread.getStat().getJSONObject("player_data").getInt("rank");

                        if(dbHasData) {
                            Cursor c = db.rawQuery("SELECT * FROM stats;", null);
                            Log.d("GCdata-db","Stat.db.size="+c.getCount() + ", Stat.db.colCount=" + c.getColumnCount());
                            if(c.moveToLast()) {
                                last_total_score = c.getInt(2);
                                last_avg_score = c.getInt(3);
                                last_rank = c.getInt(4);
                            }
                            c.close();
                        }

                        pref_edit.putString("name", thread.getStat().getJSONObject("player_data").getString("player_name"));
                        pref_edit.commit();

                        String total_stage = thread.getStat().getJSONObject("stage").getString("all");
                        tmp.append("\n\n" + thread.getStat().getJSONObject("player_data").getString("player_name") + "\n\n");
                        tmp.append(getString(R.string.score) + ": ");
                        if(total_score == last_total_score) {
                            tmp.append(total_score + "\n");
                        } else {
                            if(total_score > last_total_score) {
                                tmp.append(total_score + "(+" + (total_score-last_total_score) + ")\n");
                            } else {
                                tmp.append(total_score + "(" + (total_score-last_total_score) + ")\n");
                            }
                        }

                        tmp.append(getString(R.string.avg_score) + ": ");
                        if(avg_score == last_avg_score) {
                            tmp.append(avg_score + "\n");
                        } else {
                            if(avg_score > last_avg_score) {
                                tmp.append(avg_score + "(+" + (avg_score-last_avg_score) + ")\n");
                            } else {
                                tmp.append(avg_score + "(" + (avg_score-last_avg_score) + ")\n");
                            }
                        }

                        tmp.append(getString(R.string.played_songs) + ": ");
                        tmp.append(thread.getStat().getJSONObject("player_data").getString("total_play_music") + " / ");
                        tmp.append(thread.getStat().getJSONObject("player_data").getString("total_music") + "\n");

                        tmp.append(getString(R.string.rank) + ": ");
                        if(last_rank == rank) {
                            tmp.append(rank + "\n");
                        } else {
                            if(rank > last_rank) {
                                tmp.append(rank + "(+" + (rank-last_rank) + ")\n");
                            } else {
                                tmp.append(rank + "(" + (rank-last_rank) + ")\n");
                            }
                        }

                        tmp.append(getString(R.string.avatar) + ": " + thread.getStat().getJSONObject("player_data").getString("avatar") + "\n");
                        tmp.append(getString(R.string.title) + ": " + thread.getStat().getJSONObject("player_data").getString("title") + "\n");
                        tmp.append(getString(R.string.trophy) +": " + thread.getStat().getJSONObject("player_data").getString("total_trophy") + "\n");
                        tmp.append(getString(R.string.trophy_rank) + ": " + thread.getStat().getJSONObject("player_data").getString("trophy_rank") + "\n\n");
                        tmp.append(getString(R.string.cleared) + ": " + thread.getStat().getJSONObject("stage").getString("clear") + " / " + total_stage + "\n");
                        tmp.append(getString(R.string.nomiss) + ": " + thread.getStat().getJSONObject("stage").getString("nomiss") + " / " + total_stage + "\n");
                        tmp.append(getString(R.string.fullchain) + ": " + thread.getStat().getJSONObject("stage").getString("fullchain") + " / " + total_stage + "\n");
                        tmp.append(getString(R.string.perfect) + ": " + thread.getStat().getJSONObject("stage").getString("perfect") + " / " + total_stage + "\n\n");
                        tmp.append(getString(R.string.rankS) + "  : " + thread.getStat().getJSONObject("stage").getString("s") + " / " + total_stage + "\n");
                        tmp.append(getString(R.string.rankSS) + " : " + thread.getStat().getJSONObject("stage").getString("ss") + " / " + total_stage + "\n");
                        tmp.append(getString(R.string.rankSSS) + ": " + thread.getStat().getJSONObject("stage").getString("sss") + " / " + total_stage + "\n");

                        if(thread.getStat().getJSONObject("player_data").getBoolean("friendApplication")){
                            Toast.makeText(MyPageActivity.this, getString(R.string.friend_request_true), Toast.LENGTH_LONG).show();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(MyPageActivity.this, getString(R.string.mypage_exception),Toast.LENGTH_LONG).show();
                    }

                    // todo: insert card id along with data for users with multiple cards
                    long unixTime = System.currentTimeMillis() / 1000L;
                    if(total_score != last_total_score || avg_score != last_avg_score || rank != last_rank)
                        db.execSQL("INSERT INTO stats VALUES(null, " + unixTime + "," + total_score + "," + avg_score + "," + rank + ");");

                    if(!dbHasData){
                        dbHasData = true;
                        pref_edit.putBoolean("dbHasData", true);
                        pref_edit.commit();
                    }

                    cookieManager = thread.getCookieManager();
                    top.setText(tmp.toString());
                }else if(mode == 1 && !backupF){
                    adapter = new songListAdapter(musicList, getApplicationContext());
                    listView.setAdapter(adapter);

                    top.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }else if(mode == 1 && backupF){
                    Toast.makeText(MyPageActivity.this, getString(R.string.mypage_scores_saved), Toast.LENGTH_LONG).show();
                }else if(mode == 2){
                    JSONObject res = thread.getStat();
                    tmp = new StringBuilder();
                    tmp.append("\n\n");
                    if(res.getInt("status") != 0){
                        Toast.makeText(MyPageActivity.this,
                                getString(R.string.mypage_event_exception), Toast.LENGTH_LONG).show();
                    }else{
                        JSONObject data = res.getJSONObject("event_data");
                        tmp.append(data.getString("title_name") + "\n");
                        tmp.append(data.getString("open_date") + "~" + data.getString("close_date") + "\n\n");

                        if (data.isNull("user_event_data")) {
                            tmp.append(getString(R.string.mypage_event_not_participated));
                        } else {
                            tmp.append(getString(R.string.rank) + ": " + data.getJSONObject("user_event_data").getString("rank") + "\n");
                            tmp.append(getString(R.string.mypage_event_points) + ": " + data.getJSONObject("user_event_data").getString("event_point") + "\n\n");

                            // todo: complete the award section, fuck taito for all the stupid data format
                            JSONObject award_data = data.getJSONObject("user_event_data").getJSONObject("award_data");
                            tmp.append(getString(R.string.mypage_event_awards) + ":\n\n");

                            if (!award_data.isNull("title_award")) {
                                tmp.append(getString(R.string.title) + ":\n");
                                for (int i = 0; i < award_data.getJSONArray("title_award").length(); i++) {
                                    tmp.append(award_data.getJSONArray("title_award").get(i) + "\n");
                                }
                            }
                            tmp.append("\n");

                            if (!award_data.isNull("item_award")) {
                                String item, count;
                                tmp.append(getString(R.string.mypage_event_item) + ":\n");
                                for (int i = 11; award_data.getJSONObject("item_award").has(String.valueOf(i)); i++) {
                                    item = award_data.getJSONObject("item_award").getJSONObject(String.valueOf(i)).getString("item_name");
                                    count = award_data.getJSONObject("item_award").getJSONObject(String.valueOf(i)).getString("item_num");
                                    tmp.append(item + " x " + count + "\n");
                                }
                            }

                            tmp.append("\n" + getString(R.string.mypage_event_trophies));
                            tmp.append(": " + award_data.getString("trophy_num") + "\n\n");

                            if (!res.isNull("vicinity_ranking_data")) {
                                tmp.append(getString(R.string.mypage_event_near_rank) + ":\n");
                                for (int i = 0; i < res.getJSONArray("vicinity_ranking_data").length(); i++) {
                                    tmp.append(res.getJSONArray("vicinity_ranking_data").getJSONObject(i).getString("rank") + ". ");
                                    tmp.append(res.getJSONArray("vicinity_ranking_data").getJSONObject(i).getString("player_name") + " / BP: ");
                                    tmp.append(res.getJSONArray("vicinity_ranking_data").getJSONObject(i).getString("event_point") + "\n");

                                }
                            }
                        }
                        top.setText(tmp);
                    }

                }else if(mode == 3){
                    if(thread.getStat().getInt("status") != 0){
                        top.setText("\n\n" + getString(R.string.mypage_connection_error));
                    }else{
                        fAdapter = new friendListAdapter(friendList, getApplicationContext());
                        listView.setAdapter(fAdapter);

                        top.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }

                }

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }
}
