package moe.gc_uwu;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GlobalRankActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
                                                                NavigationView.OnNavigationItemSelectedListener {

    TextView mTextView;

    private static rankingAdapter adapter;
    ArrayList<dataTemplate> data;
    ListView listView;
    Spinner spinner;

    //XmlPullParserFactory factory;
    //XmlPullParser xpp;
    String pages[] = {"1 ~ 100", "101 ~ 200", "201 ~ 300", "301 ~ 400", "401 ~ 500",
                        "501 ~ 600", "601 ~ 700", "701 ~ 800", "801 ~ 900", "901 ~ 1000"};
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

        setTitle("Global rank");
        listView = (ListView) findViewById (R.id.list);

        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, pages);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

        // init shit


        // fetch data from taito uwu
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
        for(int i=0; i<100; i++){
            tmp = String.valueOf(i+1) + ". " + threadArr[0].getNames().item(i).getTextContent();
            tmp2 = threadArr[0].getScores().item(i).getTextContent(); //score
            tmp3 = threadArr[0].getTitles().item(i).getTextContent(); //title
            tmp4 = threadArr[0].getSites().item(i).getTextContent(); //site
            tmp5 = threadArr[0].getLoc().item(i).getTextContent(); //location
            data.add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));
        }


        for(int i=1; i<10; i++){
            threadArr[i] = new globalRankThread(String.valueOf(i+1));
            threadArr[i].start();
        }


        adapter = new rankingAdapter(data, getApplicationContext());
        listView.setAdapter(adapter);
        Log.d("GCdata", "======== END OF onCreate() ========");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        for(int i=1; i<10; i++){
            try {
                threadArr[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        data = new ArrayList<>(100);
        for(int i=0; i<100; i++){
            tmp = String.valueOf((i+1)+(position*100)) + ". " + threadArr[position].getNames().item(i).getTextContent(); //name
            tmp2 = threadArr[position].getScores().item(i).getTextContent(); //score
            tmp3 = threadArr[position].getTitles().item(i).getTextContent(); //title
            tmp4 = threadArr[position].getSites().item(i).getTextContent(); //site
            tmp5 = threadArr[position].getLoc().item(i).getTextContent(); //location
            data.add(new dataTemplate(tmp, tmp2, tmp3, tmp4, tmp5));
        }
        adapter = new rankingAdapter(data, getApplicationContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            Intent intent = new Intent(this, MyPageActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_score) {
            Intent intent = new Intent(this, MyPageActivity.class);
            intent.putExtra("score","true");
            startActivity(intent);
        } else {
            //Toast.makeText(MyPageActivity.this, "Not implemented",Toast.LENGTH_LONG).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
