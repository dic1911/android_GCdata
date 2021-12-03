package moe.gc_uwu;

import android.os.AsyncTask;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatActivity;
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
    Map<Integer, ArrayList<dataTemplate>> dataMap;
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
        grabber = (AsyncGrabData) new AsyncGrabData().execute(mode);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (ready) {
            if (mode == 0)
                data = dataMap.get(position);
            else if (mode == 1 && ready)
                data = dataMap.get(parseInt(pages[position].split(" ")[0]));

            adapter = new rankingAdapter(data, getApplicationContext());
            listView.setAdapter(adapter);
        }
    }

    private class AsyncGrabData extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            threadArr = new globalRankThread[10];
            for (int i = 0; i < 10; i++) {
                threadArr[i] = new globalRankThread(String.valueOf(i + 1));
                threadArr[i].start();
            }

            for(int i=0; i<10; i++){
                try {
                    threadArr[i].join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            dataMap = new HashMap<>();
            data = new ArrayList<>(100);

            if(mode == 0){
                for(int batch=0; batch<10; batch++) {
                    dataMap.put(batch, new ArrayList<dataTemplate>(100));
                    for(int i=0; i<100; i++){
                        tmp = String.valueOf((batch*100) + (i+1)) + ". " + threadArr[batch].getNames().item(i).getTextContent();
                        tmp2 = threadArr[batch].getScores().item(i).getTextContent(); //score
                        tmp3 = threadArr[batch].getTitles().item(i).getTextContent(); //title
                        tmp4 = threadArr[batch].getSites().item(i).getTextContent(); //site
                        tmp5 = threadArr[batch].getLoc().item(i).getTextContent(); //location
                        dataMap.get(batch).add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));
                    }
                }
            }else if(mode == 1){
                int locId;
                idToLoc = new HashMap<>();

                for(int batch=0; batch<10; batch++)
                for(int i=0; i<100; i++){
                    tmp = String.valueOf((i+1)+(batch*100)) + ". " + threadArr[batch].getNames().item(i).getTextContent(); //name
                    tmp2 = threadArr[batch].getScores().item(i).getTextContent(); //score
                    tmp3 = threadArr[batch].getTitles().item(i).getTextContent(); //title
                    tmp4 = threadArr[batch].getSites().item(i).getTextContent(); //site
                    tmp5 = threadArr[batch].getLoc().item(i).getTextContent(); //location
                    String locIdStr = threadArr[batch].getLocId().item(i).getTextContent();
                    locId = parseInt(locIdStr.equals("") ? "-1": locIdStr); //location id
                    if(!dataMap.containsKey(locId)){
                        dataMap.put(locId, new ArrayList<dataTemplate>(20));
                    }
                    dataMap.get(locId).add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));

                    if(!idToLoc.containsKey(locId)){
                        if (locId == -1) {
                            Log.d("GCdata-rankloc", tmp5);
                            idToLoc.put(locId, "Unknown");
                        }
                        else idToLoc.put(locId, tmp5);
                    }
                }
                pages = new String[dataMap.keySet().size()];
                for(int i=0; i<dataMap.keySet().size(); i++){
                    pages[i] = String.valueOf(dataMap.keySet().toArray()[i]) +
                            " (" + idToLoc.get(dataMap.keySet().toArray()[i]) + ")";
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            aa = new ArrayAdapter(GlobalRankActivity.this,R.layout.global_rank_spinner, pages);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(aa);

            Log.d("GCdata-rank", String.valueOf(dataMap.keySet().toArray()[0]));

            adapter = new rankingAdapter(dataMap.get(dataMap.keySet().toArray()[0]), getApplicationContext());
            listView.setAdapter(adapter);
            ready = true;
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
