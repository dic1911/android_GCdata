package moe.gc_uwu;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MonthlyRankActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener {

    private static rankingAdapter adapter;
    ArrayList<dataTemplate> data;
    ListView listView;
    Spinner spinner;
    ArrayList<String> pages;
    ArrayAdapter aa;

    mypageThread thread;
    AsyncGrabData grabber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_rank);

        listView = (ListView) findViewById (R.id.list);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        pages = new ArrayList<String>();

        try {
            new AsyncGrabData().execute(0);
            /*grabber = new AsyncGrabData();
            grabber.execute(0);*/
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        new AsyncGrabData().execute(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class AsyncGrabData extends AsyncTask<Integer, Void, String> {

        JSONObject res;
        JSONArray rank;
        String tmp, tmp2, tmp3, tmp4;
        int index;

        @Override
        protected String doInBackground(Integer... params) {
            index = params[0];
            thread = new mypageThread("https://mypage.groovecoaster.jp/sp/json/monthly_ranking.php?id=0&page=" + index);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            res = thread.getStat();
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            int pageCount = 0;
            try {
                pageCount = Integer.parseInt(res.getString("count"))/100;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(pages.size() == 0)
                for(int i=1; i<=pageCount; i++){
                    pages.add(String.valueOf(i));
                }

            if(spinner.getAdapter() == null) {
                aa = new ArrayAdapter(MonthlyRankActivity.this, R.layout.global_rank_spinner, pages);
                aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(aa);
            }

            data = new ArrayList<>(100);
            try {
                rank = thread.getStat().getJSONArray("rank");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                int rankNum;
                for (int i = 0; i < 100; i++) {
                    rankNum = ((i+1) + (index*100));
                    tmp = String.valueOf(rankNum) + ". " + rank.getJSONObject(i).getString("player_name"); // name
                    tmp2 = rank.getJSONObject(i).getString("score"); //score
                    tmp3 = rank.getJSONObject(i).getString("title"); //title
                    tmp4 = rank.getJSONObject(i).getString("pref"); //location
                    data.add(new dataTemplate(tmp, tmp2, tmp3, tmp4, false));
                }
            } catch (Exception e) {
                data.add(new dataTemplate(getString(R.string.monthly_not_avail), "", getString(R.string.try_again_later), "", false));
                e.printStackTrace();
            }
            adapter = new rankingAdapter(data, getApplicationContext());
            listView.setAdapter(adapter);
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MonthlyRankActivity.this, "Loading...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

}
