package moe.gc_uwu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.CookieManager;
import java.util.ArrayList;

public class MonthlyStatActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences login;
    SharedPreferences mypage_pref;
    String name;
    String cardID;
    String passwd;
    mypageThread thread;
    boolean alreadyLoggedIn;
    boolean ready;

    TextView textView;
    ListView listView;
    songListAdapter adapter;
    static ArrayList<musicTemplate> musicList;

    String period;
    int rank;
    int monthly_score_total;
    int score_count;
    int monthly_score_avg;
    //ArrayList<scoreTemplate> scoreData;

    static CookieManager cookieManager;

    AsyncGrabData grabber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_stat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init
        setTitle(getString(R.string.my_page) + " - " + getString(R.string.monthly));
        ready = false;
        alreadyLoggedIn = false;

        ready = false;
        listView = (ListView) findViewById(R.id.score_list);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mypage_pref = this.getSharedPreferences("mypage", Context.MODE_PRIVATE);
        login = this.getSharedPreferences("login", Context.MODE_PRIVATE);
        cardID = login.getString("cardID", "");
        passwd = login.getString("passwd", "");
        name = mypage_pref.getString("name", "");

        textView = (TextView) findViewById(R.id.monthlyStats);
        listView = (ListView) findViewById(R.id.monthlyList);

        if (cardID == "") {
            textView.setText(getString(R.string.please_login_first));
        } else if (name == "") {
            textView.setText(getString(R.string.mypage_monthly_unknown_name));
        } else {
            ready = true;
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("GCdata-mypage", "list item " + i + " selected.");
                String ID = musicList.get(i).getId();
                Log.d("GCdata-score", ID);
                int music_id = Integer.parseInt(ID);
                String url;
                url = "https://mypage.groovecoaster.jp/sp/json/music_detail.php?music_id=" + music_id;

                scoreTemplate song;
                Boolean hasEx;
                JSONObject fullData;

                try {
                    if (cookieManager != null) {
                        thread = new mypageThread(url, cookieManager);
                        thread.start();
                        thread.join();
                    } else {
                        Toast.makeText(MonthlyStatActivity.this, getString(R.string.mypage_cookie_error), Toast.LENGTH_LONG).show();
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

                    Intent scoreIntent = new Intent(MonthlyStatActivity.this, MyScoreActivity.class);
                    song.dataToIntent(scoreIntent);
                    Log.d("GCdata-score", "Starting score display activity");
                    startActivity(scoreIntent);
                } catch (Exception e) {
                    Snackbar.make(view, getString(R.string.mypage_song_not_played), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    e.printStackTrace();
                }
            }
        });

        if (ready) {
            grabber = new AsyncGrabData();
            grabber.execute(0);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ready)
                    new AsyncGrabData().execute(0);
                Snackbar.make(view, "Refreshing...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
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

    private class AsyncGrabData extends AsyncTask<Integer, Void, String> {

        int mode;
        int firstScore;

        @Override
        protected String doInBackground(Integer... params) {
            mode = params[0];
            if (mode == 0) {
                // grab music list
                String list_url = "https://mypage.groovecoaster.jp/sp/json/monthly_ranking_list.php?this_flag=true";
                if (!alreadyLoggedIn) {
                    thread = new mypageThread(list_url, cardID, passwd);
                } else {
                    thread = new mypageThread(list_url, cookieManager);
                }

                thread.start();
                try {
                    thread.join();
                    cookieManager = thread.getCookieManager();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    musicList = new ArrayList<>();
                    JSONArray songs = thread.getStat().getJSONArray("period_list").getJSONObject(0).getJSONArray("music_list");
                    for (int i = 0; i < songs.length(); i++) {
                        musicList.add(new musicTemplate(songs.getJSONObject(i).getString("music_id"),
                                songs.getJSONObject(i).getString("title")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MonthlyStatActivity.this, getString(R.string.mypage_exception), Toast.LENGTH_LONG).show();
                }
            } else if (mode == 1) {
                // process current stats

                // get total score and average score
                scoreBackupThread thread0 = new scoreBackupThread(getApplicationContext(), "", cookieManager, musicList, false);
                thread0.start();
                try {
                    thread0.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                monthly_score_total = thread0.total_score;
                score_count = thread0.count;
                monthly_score_avg = monthly_score_total / score_count;

                // find myself in the ranking list
                String monthly_rank_url = "https://mypage.groovecoaster.jp/sp/json/monthly_ranking.php?id=0&page=";
                int index = 0;
                thread = new mypageThread(monthly_rank_url + index);
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                rank = -1;
                try {
                    period = thread.getStat().getString("title");
                    firstScore = thread.getStat().getJSONArray("rank").getJSONObject(0).getInt("score");
                    for (int i = 0; i < 100; i++) {
                        if (name.equals(thread.getStat().getJSONArray("rank").getJSONObject(i).getString("player_name"))) {
                            rank = (index*100) + i;
                            break;
                        }
                    }

                    // try to search player from the ranking storage in a kinda better way
                    if (rank == -1){
                        float p = (float) monthly_score_total / firstScore;
                        float pages = thread.getStat().getInt("count") / 100;
                        index = Math.round(p * pages) / 3;
                    }
                    while (rank == -1) {
                        thread = new mypageThread(monthly_rank_url + index);
                        thread.start();
                        try {
                            thread.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (monthly_score_total > thread.getStat().getJSONArray("rank").getJSONObject(0).getInt("score")) {
                            index -= 1;
                        } else if (monthly_score_total < thread.getStat().getJSONArray("rank")
                                        .getJSONObject(thread.getStat().getJSONArray("rank").length()-1).getInt("score")) {
                            index += 1;
                        } else {
                            for (int i = 0; i < thread.getStat().getJSONArray("rank").length(); i++) {
                                if (name.equals(thread.getStat().getJSONArray("rank").getJSONObject(i).getString("player_name"))) {
                                    rank = (index*100) + i;
                                    break;
                                }
                            }
                        }

                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (mode == 0) {
                adapter = new songListAdapter(musicList, getApplicationContext());
                listView.setAdapter(adapter);
                new AsyncGrabData().execute(1);
            } else if (mode == 1) {
                StringBuilder res = new StringBuilder();
                res.append(name + "\n");
                res.append(period + "\n\n");
                res.append(getString(R.string.score) + ": " + monthly_score_total + "\n");
                res.append(getString(R.string.avg_score) + ": " + monthly_score_avg + "\n");
                res.append(getString(R.string.total_avg_score) + ": " + String.valueOf((monthly_score_total / (firstScore/1000000))) + "\n");
                res.append(getString(R.string.rank) + ": " + rank);

                textView.setText(res.toString());
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
