package moe.gc_uwu;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.util.ArrayList;

import static java.lang.Integer.parseInt;

public class scoreBackupThread extends Thread implements Runnable {

    Context context;
    String file;
    CookieManager cookieManager;
    String friendHash;
    String result;
    StringBuilder preres;

    Boolean saveFile;

    volatile int count;
    volatile int total_score;

    ArrayList<musicTemplate> songList;

    String[] stat = {"Not Played", "Failed", "Cleared", "No Miss", "Full Chain", "Perfect"};

    public scoreBackupThread(Context context, String file, CookieManager cookieManager, ArrayList<musicTemplate> songList){
        this.context = context;
        this.file = file;
        this.cookieManager = cookieManager;
        this.songList = songList;
        this.friendHash = "";
        this.saveFile = true;

        result = "id,song,simple_mk,simple_rt,simple_sc,sp_chain,simple_pc,sp_rank,normal_mk,normal_rt,normal_sc,nm_chain,normal_pc,nm_rank,hard_mk,hard_rt,hard_sc,hd_chain,hard_pc,hd_rank,extra_mk,extra_rt,extra_sc,ex_chain,extra_pc,ex_rank\n";
    }

    public scoreBackupThread(Context context, String file, CookieManager cookieManager, ArrayList<musicTemplate> songList, String friendHash){
        this.context = context;
        this.file = file;
        this.cookieManager = cookieManager;
        this.songList = songList;
        this.friendHash = friendHash;
        this.saveFile = true;

        result = "id,song,simple_mk,simple_rt,simple_sc,sp_chain,simple_pc,sp_rank,normal_mk,normal_rt,normal_sc,nm_chain,normal_pc,nm_rank,hard_mk,hard_rt,hard_sc,hd_chain,hard_pc,hd_rank,extra_mk,extra_rt,extra_sc,ex_chain,extra_pc,ex_rank\n";
    }

    public scoreBackupThread(Context context, String file, CookieManager cookieManager, ArrayList<musicTemplate> songList, boolean saveFile){
        this.context = context;
        this.file = file;
        this.cookieManager = cookieManager;
        this.songList = songList;
        this.friendHash = "";
        this.saveFile = saveFile;
        this.count = 0;
        this.total_score = 0;

        result = "";
    }

    private String getDiffStat(JSONObject data){
        String result = "";
        try {
            if (data.getInt("perfect") >= 1) {
                result = stat[5];
            } else if (data.getInt("full_chain") >= 1) {
                result = stat[4];
            } else if (data.getInt("no_miss") >= 1) {
                result = stat[3];
            } else if (data.getBoolean("is_clear_mark")) {
                result = stat[2];
            } else if (data.getBoolean("is_failed_mark")) {
                result = stat[1];
            }
        } catch (Exception e) {e.printStackTrace();}
        return result;
    }

    private void scoreDataHandler(StringBuilder target, mypageThread thread){
        try {
            if (thread.getStat().isNull("music_detail")){
                // no data to process here
                return;
            }
            JSONObject detail = thread.getStat().getJSONObject("music_detail");
            JSONArray rank = detail.getJSONArray("user_rank");
            JSONObject tmp;
            if (detail.getString("music_id").contains("321")) {
                target.append(detail.getString("music_id") + ",=\"2112410403927243233368\",");
            } else if (detail.getString("music_title").contains("\"")) {
                target.append(detail.getString("music_id") + "," + detail.getString("music_title") + ",");
            } else {
                target.append(detail.getString("music_id") + ",\"" + detail.getString("music_title") + "\",");
            }
            if (detail.isNull("simple_result_data")) {
                target.append(",,,,,,");
            } else {
                tmp = detail.getJSONObject("simple_result_data");
                target.append(getDiffStat(tmp) + "," + tmp.getString("rating") + "," + tmp.getString("score") + "," +
                        tmp.getString("max_chain") + "," + tmp.getString("play_count") + ",");
                count++;
                total_score += parseInt(tmp.getString("score"));
                if(!rank.isNull(0)){
                    target.append(rank.getJSONObject(0).getString("rank") + ",");
                }else{
                    target.append(",");
                }
            }
            if (detail.isNull("normal_result_data")) {
                target.append(",,,,,,");
            } else {
                tmp = detail.getJSONObject("normal_result_data");
                target.append(getDiffStat(tmp) + "," + tmp.getString("rating") + "," + tmp.getString("score") + "," +
                        tmp.getString("max_chain") + "," + tmp.getString("play_count") + ",");
                count++;
                total_score += parseInt(tmp.getString("score"));
                if(!rank.isNull(1)){
                    target.append(rank.getJSONObject(1).getString("rank") + ",");
                }else{
                    target.append(",");
                }
            }
            if (detail.isNull("hard_result_data")) {
                target.append(",,,,,,");
            } else {
                tmp = detail.getJSONObject("hard_result_data");
                target.append(getDiffStat(tmp) + "," + tmp.getString("rating") + "," + tmp.getString("score") + "," +
                        tmp.getString("max_chain") + "," + tmp.getString("play_count") + ",");
                count++;
                total_score += parseInt(tmp.getString("score"));
                if(!rank.isNull(2)){
                    target.append(rank.getJSONObject(2).getString("rank") + ",");
                }else{
                    target.append(",");
                }
            }
            if (detail.getInt("ex_flag") == 0 ||
                    detail.isNull("extra_result_data")) {
                target.append(",,,,,,");
            } else {
                tmp = detail.getJSONObject("extra_result_data");
                target.append(getDiffStat(tmp) + "," + tmp.getString("rating") + "," + tmp.getString("score") + "," +
                        tmp.getString("max_chain") + "," + tmp.getString("play_count") + ",");
                count++;
                total_score += parseInt(tmp.getString("score"));
                if(!rank.isNull(3)) {
                    target.append(rank.getJSONObject(3).getString("rank"));
                }
            }
            target.append("\n");
        } catch (Exception e) {e.printStackTrace();}
    }

    public String getResult(){
        return preres.toString();
    }

    @Override
    public void run() {
        preres = new StringBuilder("id,song,simple_mk,simple_rt,simple_sc,sp_chain,simple_pc,sp_rank,normal_mk,normal_rt,normal_sc,nm_chain,normal_pc,nm_rank,hard_mk,hard_rt,hard_sc,hd_chain,hard_pc,hd_rank,extra_mk,extra_rt,extra_sc,ex_chain,extra_pc,ex_rank\n");
        mypageThread threads[] = new mypageThread[4];
        FileOutputStream fos;

        String url;
        String music_id;
        for(int i=0; i<songList.size(); i++) {
            Log.d("GCdata-score_bkp", String.valueOf(i+1) + "/" + String.valueOf(songList.size()));

            music_id = songList.get(i).getId();
            if(friendHash.equals("")) {
                url = "https://mypage.groovecoaster.jp/sp/json/music_detail.php?music_id=" + music_id;
            } else {
                url = "https://mypage.groovecoaster.jp/sp/json/friend_music_detail.php?music_id=" + music_id + "&hash=" + friendHash;
            }

            // fetch -j4
            if(i > 3){
                //Toast.makeText(context,"Backup Progress: " + String.valueOf(i+1) + "/" + String.valueOf(songList.size()), Toast.LENGTH_SHORT).show();
                try {
                    threads[i%4].join();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                scoreDataHandler(preres, threads[i%4]);
            }
            threads[i%4] = new mypageThread(url, cookieManager);
            threads[i%4].start();

            // handle the last threads
            if(i == songList.size()-1){
                for(int j=0; j<4; j++) {
                    try {
                        threads[j].join();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    scoreDataHandler(preres, threads[j]);
                }
            }
        }
        try {
           // outputStreamWriter = new OutputStreamWriter(context.openFileOutput((file.getName()), Context.MODE_PRIVATE));
            if (saveFile) {
                fos = new FileOutputStream(new File(file), false);
                fos.write(preres.toString().getBytes());
                Log.d("GCdata-score_bkp", "Done!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
