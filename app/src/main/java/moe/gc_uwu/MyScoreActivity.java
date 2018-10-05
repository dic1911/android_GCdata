package moe.gc_uwu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class MyScoreActivity extends AppCompatActivity {

    TextView simple;
    TextView normal;
    TextView hard;
    TextView extra;
    TextView simple_t;
    TextView normal_t;
    TextView hard_t;
    TextView extra_t;
    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score);

        data = getIntent().getExtras();

        // init page
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(getIntent().getExtras().getString("id") + ". " + getIntent().getExtras().getString("title"));
        simple = findViewById(R.id.data_simple);
        normal = findViewById(R.id.data_normal);
        hard = findViewById(R.id.data_hard);
        extra = findViewById(R.id.data_extra);

        simple_t = findViewById(R.id.text_simple);
        normal_t = findViewById(R.id.text_normal);
        hard_t = findViewById(R.id.text_hard);
        extra_t = findViewById(R.id.text_extra);


        String tmp = "Score: ";
        tmp += (data.getString("s_score") + "    Rating: " + data.getString("s_rate") + "\n");
        tmp += ("Play Count: " + data.getString("s_pc") + "    Max Chain: " + data.getString("s_chain") + "\n");
        tmp += ("Rank: " + data.getString("s_rank") + "\n\n");
        simple_t.append("    [" + data.getString("s_stat") + "]");
        simple.setText(tmp);

        tmp = "Score: ";
        tmp += (data.getString("n_score") + "    Rating: " + data.getString("n_rate") + "\n");
        tmp += ("Play Count: " + data.getString("n_pc") + "    Max Chain: " + data.getString("n_chain") + "\n");
        tmp += ("Rank: " + data.getString("n_rank") + "\n\n");
        normal_t.append("    [" + data.getString("n_stat") + "]");
        normal.setText(tmp);

        tmp = "Score: ";
        tmp += (data.getString("h_score") + "    Rating: " + data.getString("h_rate") + "\n");
        tmp += ("Play Count: " + data.getString("h_pc") + "    Max Chain: " + data.getString("h_chain") + "\n");
        tmp += ("Rank: " + data.getString("h_rank") + "\n\n");
        hard_t.append("    [" + data.getString("h_stat") + "]");
        hard.setText(tmp);

        if(data.getBoolean("hasEx")) {
            tmp = "Score: ";
            tmp += (data.getString("e_score") + "    Rating: " + data.getString("e_rate") + "\n");
            tmp += ("Play Count: " + data.getString("e_pc") + "    Max Chain: " + data.getString("e_chain") + "\n");
            tmp += ("Rank: " + data.getString("e_rank") + "\n\n");
            extra_t.append("    [" + data.getString("e_stat") + "]");
            extra.setText(tmp);
        }else{
            extra_t.setVisibility(View.GONE);
            extra.setVisibility(View.GONE);
        }
    }

}
