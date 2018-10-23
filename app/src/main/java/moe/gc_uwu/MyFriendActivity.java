package moe.gc_uwu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MyFriendActivity extends AppCompatActivity {

    Bundle data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView text = (TextView) findViewById(R.id.friend_stat);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        setSupportActionBar(toolbar);

        data = getIntent().getExtras();
        StringBuilder str = new StringBuilder("\n");

        String url = "https://mypage.groovecoaster.jp/sp/json/friend_player_data.php?hash=" + data.getString("hash");
        mypageThread thread = new mypageThread(url, MyPageActivity.cookieManager);
        thread.start();

        setTitle(data.getString("name"));
        text.setText("Loading...");

        try {
            thread.join();

            //str.append(thread.getStat().getJSONObject("player_data").getString("player_name") + "\n\n");

            str.append("Score: " + thread.getStat().getJSONObject("player_data").getString("total_score") + "\n");
            str.append("Avg. Score: " + thread.getStat().getJSONObject("player_data").getString("average_score") + "\n");
            str.append("Played Songs: " + thread.getStat().getJSONObject("player_data").getString("total_play_music") + " / ");
            str.append(thread.getStat().getJSONObject("player_data").getString("total_music") + "\n");
            str.append("Rank: " + thread.getStat().getJSONObject("player_data").getString("rank") + "\n");
            str.append("Title:" + thread.getStat().getJSONObject("player_data").getString("title") + "\n");
            str.append("Avatar: " + thread.getStat().getJSONObject("player_data").getString("avatar") + "\n");
            str.append("Trophy: " + thread.getStat().getJSONObject("player_data").getString("total_trophy") + "\n");
            str.append("Trophy Rank: " + thread.getStat().getJSONObject("player_data").getString("trophy_rank") + "\n\n");

            String stageCount = thread.getStat().getJSONObject("stage").getString("all");

            str.append("Cleared: " + thread.getStat().getJSONObject("stage").getString("clear") + " / " + stageCount + "\n");
            str.append("No Miss: " + thread.getStat().getJSONObject("stage").getString("nomiss") + " / " + stageCount + "\n");
            str.append("Full Chain: " + thread.getStat().getJSONObject("stage").getString("fullchain") + " / " + stageCount + "\n");
            str.append("Perfect: " + thread.getStat().getJSONObject("stage").getString("perfect") + " / " + stageCount + "\n\n");

            str.append("Rank S  : " + thread.getStat().getJSONObject("stage").getString("s") + " / " + stageCount + "\n");
            str.append("Rank S+ : " + thread.getStat().getJSONObject("stage").getString("ss") + " / " + stageCount + "\n");
            str.append("Rank S++: " + thread.getStat().getJSONObject("stage").getString("sss") + " / " + stageCount + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }

        text.setText(str.toString());

        fab.setVisibility(View.VISIBLE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyFriendActivity.this, MyPageActivity.class);
                intent.putExtra("mode",1);
                intent.putExtra("friendHash", data.getString("hash"));
                intent.putExtra("friendName", data.getString("name"));
                startActivity(intent);
            }
        });
    }
}
