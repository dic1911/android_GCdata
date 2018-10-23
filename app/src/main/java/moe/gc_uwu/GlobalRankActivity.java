package moe.gc_uwu;

import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class GlobalRankActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
                                                                //NavigationView.OnNavigationItemSelectedListener {

    TextView mTextView;

    private static rankingAdapter adapter;
    ArrayList<dataTemplate> data;
    //ArrayList<ArrayList<dataTemplate>> data_area;
    Map<Integer, ArrayList<dataTemplate>> data_area;
    Map<Integer, String> idToLoc;
    ListView listView;
    ArrayAdapter aa;
    NavigationView navigationView;
    Spinner spinner;
    Boolean ready;

    int mode;
    String loading[] = {"Loading..."};
    String pages[] = {"1 ~ 100", "101 ~ 200", "201 ~ 300", "301 ~ 400", "401 ~ 500",
                        "501 ~ 600", "601 ~ 700", "701 ~ 800", "801 ~ 900", "901 ~ 1000"};

    AsyncGrabData grabber;

    String tmp = "";
    String tmp2 = "";
    String tmp3 = "";
    String tmp4 = "";
    String tmp5 = "";

    //globalRankThread thread0;
    globalRankThread threadArr[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_rank);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);*/

        // init page
        ready = false;
        mode = getIntent().getExtras().getInt("mode");
        switch (mode){
            case (0):{
                setTitle("Global rank");
                break;
            }
            case (1):{
                setTitle("Area rank");
                break;
            }
        }

        listView = (ListView) findViewById (R.id.list);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        aa = new ArrayAdapter(this,R.layout.global_rank_spinner, loading);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        // init shit


        // fetch data from taito uwu
        if(mode == 0) {
            threadArr = new globalRankThread[10];
            threadArr[0] = new globalRankThread(String.valueOf(1));

            threadArr[0].start();
            // todo: make it faster / don't delay the activity launch while waiting for response
            try {
                threadArr[0].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            data = new ArrayList<>(100);
            for (int i = 0; i < 100; i++) {
                tmp = String.valueOf(i + 1) + ". " + threadArr[0].getNames().item(i).getTextContent();
                tmp2 = threadArr[0].getScores().item(i).getTextContent(); //score
                tmp3 = threadArr[0].getTitles().item(i).getTextContent(); //title
                tmp4 = threadArr[0].getSites().item(i).getTextContent(); //site
                tmp5 = threadArr[0].getLoc().item(i).getTextContent(); //location
                data.add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));
            }


            for (int i = 1; i < 10; i++) {
                threadArr[i] = new globalRankThread(String.valueOf(i + 1));
                threadArr[i].start();
            }

            aa = new ArrayAdapter(this,R.layout.global_rank_spinner, pages);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(aa);
            adapter = new rankingAdapter(data, getApplicationContext());
            listView.setAdapter(adapter);
        } else if (mode == 1) {
            grabber = (AsyncGrabData) new AsyncGrabData().execute(mode);
        }
        Log.d("GCdata", "======== END OF onCreate() ========");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(mode == 0) {
            for (int i = 1; i < 10; i++) {
                try {
                    threadArr[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            data = new ArrayList<>(100);
            for (int i = 0; i < 100; i++) {
                tmp = String.valueOf((i + 1) + (position * 100)) + ". " + threadArr[position].getNames().item(i).getTextContent(); //name
                tmp2 = threadArr[position].getScores().item(i).getTextContent(); //score
                tmp3 = threadArr[position].getTitles().item(i).getTextContent(); //title
                tmp4 = threadArr[position].getSites().item(i).getTextContent(); //site
                tmp5 = threadArr[position].getLoc().item(i).getTextContent(); //location
                data.add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));
            }
            adapter = new rankingAdapter(data, getApplicationContext());
            listView.setAdapter(adapter);
        } else if(mode == 1 && ready) {
            data = data_area.get(parseInt(pages[position].split(" ")[0]));
            adapter = new rankingAdapter(data, getApplicationContext());
            listView.setAdapter(adapter);
        }
    }

    private class AsyncGrabData extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            if(mode == 1){
                threadArr = new globalRankThread[10];
                for(int i=0; i<10; i++) {
                    threadArr[i] = new globalRankThread(String.valueOf(i+1));
                    threadArr[i].start();
                }

                for(int i=0; i<10; i++){
                    try {
                        threadArr[i].join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                int locId;
                data_area = new HashMap<>();
                idToLoc = new HashMap<>();
                data = new ArrayList<>(100);
                for(int batch=0; batch<10; batch++)
                for(int i=0; i<100; i++){
                    tmp = String.valueOf((i+1)+(batch*100)) + ". " + threadArr[batch].getNames().item(i).getTextContent(); //name
                    tmp2 = threadArr[batch].getScores().item(i).getTextContent(); //score
                    tmp3 = threadArr[batch].getTitles().item(i).getTextContent(); //title
                    tmp4 = threadArr[batch].getSites().item(i).getTextContent(); //site
                    tmp5 = threadArr[batch].getLoc().item(i).getTextContent(); //location
                    locId = parseInt(threadArr[batch].getLocId().item(i).getTextContent()); //location id
                    if(!data_area.containsKey(locId)){
                        data_area.put(locId, new ArrayList<dataTemplate>(20));
                    }
                    data_area.get(locId).add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));

                    if(!idToLoc.containsKey(locId)){
                        idToLoc.put(locId, tmp5);
                    }
                }
                pages = new String[data_area.keySet().size()];
                for(int i=0; i<data_area.keySet().size(); i++){
                    pages[i] = String.valueOf(data_area.keySet().toArray()[i]) +
                            " (" + idToLoc.get(data_area.keySet().toArray()[i]) + ")";
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(mode == 1){
                aa = new ArrayAdapter(GlobalRankActivity.this,R.layout.global_rank_spinner, pages);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(aa);

                adapter = new rankingAdapter(data_area.get(158), getApplicationContext());
                listView.setAdapter(adapter);
                ready = true;
            }
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /*
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Log.d("GCdata-nav_menu", String.valueOf(id));
        if (id == R.id.nav_global) {
            if(mode != 0) {
                Intent intent = new Intent(this, GlobalRankActivity.class);
                intent.putExtra("mode", 0);
                startActivity(intent);
            }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    */
}
