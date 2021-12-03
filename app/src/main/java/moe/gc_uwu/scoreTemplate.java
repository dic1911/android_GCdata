package moe.gc_uwu;

import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;

public class scoreTemplate {
    String id;
    String title;
    String artist;
    Boolean hasEx;
    Boolean[] diffPlayed;

    String[] stat = {"Not Played", "Failed", "Cleared", "No Miss", "Full Chain", "Perfect"};

    String s_stat;
    String s_rate;
    String s_score;
    String s_chain;
    String s_rank;
    String s_pc;

    String n_stat;
    String n_rate;
    String n_score;
    String n_chain;
    String n_rank;
    String n_pc;

    String h_stat;
    String h_rate;
    String h_score;
    String h_chain;
    String h_rank;
    String h_pc;

    String e_stat;
    String e_rate;
    String e_score;
    String e_chain;
    String e_rank;
    String e_pc;

    public Boolean[] hasDiffData(){
        return diffPlayed;
    }

    public int dataToIntent(Intent intent){
        try{
            intent.putExtra("id", this.id);
            intent.putExtra("title", this.title);
            intent.putExtra("hasEx", this.hasEx);

            intent.putExtra("s_stat", this.s_stat);
            intent.putExtra("s_rate", this.s_rate);
            intent.putExtra("s_score", this.s_score);
            intent.putExtra("s_chain", this.s_chain);
            intent.putExtra("s_rank", this.s_rank);
            intent.putExtra("s_pc", this.s_pc);

            intent.putExtra("n_stat", this.n_stat);
            intent.putExtra("n_rate", this.n_rate);
            intent.putExtra("n_score", this.n_score);
            intent.putExtra("n_chain", this.n_chain);
            intent.putExtra("n_rank", this.n_rank);
            intent.putExtra("n_pc", this.n_pc);

            intent.putExtra("h_stat", this.h_stat);
            intent.putExtra("h_rate", this.h_rate);
            intent.putExtra("h_score", this.h_score);
            intent.putExtra("h_chain", this.h_chain);
            intent.putExtra("h_rank", this.h_rank);
            intent.putExtra("h_pc", this.h_pc);

            intent.putExtra("e_stat", this.e_stat);
            intent.putExtra("e_rate", this.e_rate);
            intent.putExtra("e_score", this.e_score);
            intent.putExtra("e_chain", this.e_chain);
            intent.putExtra("e_rank", this.e_rank);
            intent.putExtra("e_pc", this.e_pc);

            return 0;
        } catch (Exception e){
            e.printStackTrace();
            return 1;
        }
    }


    public scoreTemplate(String id, String title, String artist, Boolean hasEx, JSONObject simple, JSONObject normal, JSONObject hard, JSONObject extra, JSONArray rank, JSONObject full){
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.hasEx = hasEx;

        diffPlayed = new Boolean[]{true, true, true, true};
        //t = 10;

        try {
            if(simple.has("blank")){
                this.s_stat = stat[0];
                this.s_rate = "-";
                this.s_score = "-";
                this.s_chain = "-";
                this.s_rank = "-";
                this.s_pc = "-";

                diffPlayed[0] = false;
                //t -= 1;
            } else {
                if (simple.getInt("perfect") >= 1) {
                    this.s_stat = stat[5];
                } else if (simple.getInt("full_chain") >= 1) {
                    this.s_stat = stat[4];
                } else if (simple.getInt("no_miss") >= 1) {
                    this.s_stat = stat[3];
                } else if (simple.getBoolean("is_clear_mark")) {
                    this.s_stat = stat[2];
                } else if (simple.getBoolean("is_failed_mark")) {
                    this.s_stat = stat[1];
                }

                this.s_rate = simple.getString("rating");
                this.s_score = simple.getString("score");
                this.s_pc = simple.getString("play_count");
                this.s_chain = simple.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(0))
                    this.s_rank = "-";
                else
                    this.s_rank = rank.getJSONObject(0).getString("rank");
            }

            if(normal.has("blank")){
                this.n_stat = stat[0];
                this.n_rate = "-";
                this.n_score = "-";
                this.n_chain = "-";
                this.n_rank = "-";

                diffPlayed[1] = false;
                //t -= 2;
            } else {
                if (normal.getInt("perfect") >= 1) {
                    this.n_stat = stat[5];
                } else if (normal.getInt("full_chain") >= 1) {
                    this.n_stat = stat[4];
                } else if (normal.getInt("no_miss") >= 1) {
                    this.n_stat = stat[3];
                } else if (normal.getBoolean("is_clear_mark")) {
                    this.n_stat = stat[2];
                } else if (normal.getBoolean("is_failed_mark")) {
                    this.n_stat = stat[1];
                }

                this.n_rate = normal.getString("rating");
                this.n_score = normal.getString("score");
                this.n_pc = normal.getString("play_count");
                this.n_chain = normal.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(1))
                    this.n_rank = "-";
                else
                    this.n_rank = rank.getJSONObject(1).getString("rank");
            }

            if(hard.has("blank")){
                this.h_stat = stat[0];
                this.h_rate = "-";
                this.h_score = "-";
                this.h_chain = "-";
                this.h_rank = "-";

                diffPlayed[2] = false;
                //t -= 3;
            } else {
                if (hard.getInt("perfect") >= 1) {
                    this.h_stat = stat[5];
                } else if (hard.getInt("full_chain") >= 1) {
                    this.h_stat = stat[4];
                } else if (hard.getInt("no_miss") >= 1) {
                    this.h_stat = stat[3];
                } else if (hard.getBoolean("is_clear_mark")) {
                    this.h_stat = stat[2];
                } else if (hard.getBoolean("is_failed_mark")) {
                    this.h_stat = stat[1];
                }

                this.h_rate = hard.getString("rating");
                this.h_score = hard.getString("score");
                this.h_pc = hard.getString("play_count");
                this.h_chain = hard.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(2))
                    this.h_rank = "-";
                else
                    this.h_rank = rank.getJSONObject(2).getString("rank");
            }

            if(extra.has("blank")){
                this.e_stat = stat[0];
                this.e_rate = "-";
                this.e_score = "-";
                this.e_chain = "-";
                this.e_rank = "-";

                diffPlayed[3] = false;
                //t -= 4;
            } else {
                if (extra.getInt("perfect") >= 1) {
                    this.e_stat = stat[5];
                } else if (extra.getInt("full_chain") >= 1) {
                    this.e_stat = stat[4];
                } else if (extra.getInt("no_miss") >= 1) {
                    this.e_stat = stat[3];
                } else if (extra.getBoolean("is_clear_mark")) {
                    this.e_stat = stat[2];
                } else if (extra.getBoolean("is_failed_mark")) {
                    this.e_stat = stat[1];
                }

                this.e_rate = extra.getString("rating");
                this.e_score = extra.getString("score");
                this.e_pc = extra.getString("play_count");
                this.e_chain = extra.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(3))
                    this.e_rank = "-";
                else
                    this.e_rank = rank.getJSONObject(3).getString("rank");
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public scoreTemplate(String id, String title, String artist, Boolean hasEx, JSONObject simple, JSONObject normal, JSONObject hard, JSONArray rank, JSONObject full){
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.hasEx = hasEx;

        diffPlayed = new Boolean[]{true, true, true, true};
        //t = 10;

        try {
            if(simple.has("blank")){
                this.s_stat = stat[0];
                this.s_rate = "-";
                this.s_score = "-";
                this.s_pc = "-";
                this.s_chain = "-";
                this.s_rank = "-";

                diffPlayed[0] = false;
                //t -= 1;
            } else {
                if (simple.getInt("perfect") >= 1) {
                    this.s_stat = stat[5];
                } else if (simple.getInt("full_chain") >= 1) {
                    this.s_stat = stat[4];
                } else if (simple.getInt("no_miss") >= 1) {
                    this.s_stat = stat[3];
                } else if (simple.getBoolean("is_clear_mark")) {
                    this.s_stat = stat[2];
                } else if (simple.getBoolean("is_failed_mark")) {
                    this.s_stat = stat[1];
                }

                this.s_rate = simple.getString("rating");
                this.s_score = simple.getString("score");
                this.s_pc = simple.getString("play_count");
                this.s_chain = simple.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(0))
                    this.s_rank = "-";
                else
                    this.s_rank = rank.getJSONObject(0).getString("rank");
            }

            if(normal.has("blank")){
                this.n_stat = stat[0];
                this.n_rate = "-";
                this.n_score = "-";
                this.n_pc = "-";
                this.n_chain = "-";
                this.n_rank = "-";

                diffPlayed[1] = false;
                //t -= 2;
            } else {
                if (normal.getInt("perfect") >= 1) {
                    this.n_stat = stat[5];
                } else if (normal.getInt("full_chain") >= 1) {
                    this.n_stat = stat[4];
                } else if (normal.getInt("no_miss") >= 1) {
                    this.n_stat = stat[3];
                } else if (normal.getBoolean("is_clear_mark")) {
                    this.n_stat = stat[2];
                } else if (normal.getBoolean("is_failed_mark")) {
                    this.n_stat = stat[1];
                }

                this.n_rate = normal.getString("rating");
                this.n_score = normal.getString("score");
                this.n_pc = normal.getString("play_count");
                this.n_chain = normal.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(1))
                    this.n_rank = "-";
                else
                    this.n_rank = rank.getJSONObject(1).getString("rank");
            }

            if(hard.has("blank")){
                this.h_stat = stat[0];
                this.h_rate = "-";
                this.h_score = "-";
                this.h_pc = "-";
                this.h_chain = "-";
                this.h_rank = "-";

                diffPlayed[2] = false;
                //t -= 3;
            } else {
                if (hard.getInt("perfect") >= 1) {
                    this.h_stat = stat[5];
                } else if (hard.getInt("full_chain") >= 1) {
                    this.h_stat = stat[4];
                } else if (hard.getInt("no_miss") >= 1) {
                    this.h_stat = stat[3];
                } else if (hard.getBoolean("is_clear_mark")) {
                    this.h_stat = stat[2];
                } else if (hard.getBoolean("is_failed_mark")) {
                    this.h_stat = stat[1];
                }

                this.h_rate = hard.getString("rating");
                this.h_score = hard.getString("score");
                this.h_pc = hard.getString("play_count");
                this.h_chain = hard.getString("max_chain");
                if(rank == null || full.getJSONArray("user_rank").isNull(2))
                    this.h_rank = "-";
                else
                    this.h_rank = rank.getJSONObject(2).getString("rank");
            }


            this.e_stat = stat[0];
            this.e_rate = "-";
            this.e_score = "-";
            this.e_pc = "-";
            this.e_chain = "-";
            this.e_rank = "-";

            diffPlayed[3] = false;

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
