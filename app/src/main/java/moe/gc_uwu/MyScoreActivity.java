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
    TextView last_t;
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

        last_t = findViewById(R.id.text_lastplay);

        String tmp = getString(R.string.score) + ": ";
        tmp += (data.getString("s_score") + "    " + getString(R.string.mypage_score_rating) + ": " + data.getString("s_rate") + "\n");
        tmp += (getString(R.string.mypage_score_play_count) + ": " + data.getString("s_pc") + "    " +
                getString(R.string.mypage_score_max_chain) + ": " + data.getString("s_chain") + "\n");
        tmp += (getString(R.string.rank) + ": " + data.getString("s_rank") + "\n\n");
        simple_t.append("    [" + data.getString("s_stat") + "]");
        simple.setText(tmp);

        tmp = getString(R.string.score) + ": ";
        tmp += (data.getString("n_score") + "    " + getString(R.string.mypage_score_rating) + ": " + data.getString("n_rate") + "\n");
        tmp += (getString(R.string.mypage_score_play_count) + ": " + data.getString("n_pc") + "    " +
                getString(R.string.mypage_score_max_chain) + ": " + data.getString("n_chain") + "\n");
        tmp += (getString(R.string.rank) + ": " + data.getString("n_rank") + "\n\n");
        normal_t.append("    [" + data.getString("n_stat") + "]");
        normal.setText(tmp);

        tmp = getString(R.string.score) + ": ";
        tmp += (data.getString("h_score") + "    " + getString(R.string.mypage_score_rating) + ": " + data.getString("h_rate") + "\n");
        tmp += (getString(R.string.mypage_score_play_count) + ": " + data.getString("h_pc") + "    " +
                getString(R.string.mypage_score_max_chain) + ": " + data.getString("h_chain") + "\n");
        tmp += (getString(R.string.rank) + ": " + data.getString("h_rank") + "\n\n");
        hard_t.append("    [" + data.getString("h_stat") + "]");
        hard.setText(tmp);

        if(data.getBoolean("hasEx")) {
            tmp = getString(R.string.score) + ": ";
            tmp += (data.getString("e_score") + "    " + getString(R.string.mypage_score_rating) + ": " + data.getString("e_rate") + "\n");
            tmp += (getString(R.string.mypage_score_play_count) + ": " + data.getString("e_pc") + "    " +
                    getString(R.string.mypage_score_max_chain) + ": " + data.getString("e_chain") + "\n");
            tmp += (getString(R.string.rank) + ": " + data.getString("e_rank") + "\n\n");
            extra_t.append("    [" + data.getString("e_stat") + "]");
            extra.setText(tmp);
        }else{
            extra_t.setVisibility(View.GONE);
            extra.setVisibility(View.GONE);
        }

        if (getIntent().getExtras().getString("last_play") != null) {
            tmp = getString(R.string.mypage_score_last_play) + ":\n" + getIntent().getExtras().getString("last_play");
            last_t.setText(tmp);
        }
    }

}
