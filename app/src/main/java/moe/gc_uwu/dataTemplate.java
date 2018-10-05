package moe.gc_uwu;

public class dataTemplate {
    String player;
    String score;
    String title;
    String site;
    String pref;

    public dataTemplate(){
        this.player = "NO NAME";
        this.score = "0";
        this.title = "GROOVER";
        this.site = "ROUND 2 @ localhost";
        this.pref = "030";
    }

    public dataTemplate(String player, String score, String title) {
        this.player = player;
        this.score = score;
        this.title = title;
        this.site = "ROUND 2 @ localhost";
        this.pref = "030";
    }

    public dataTemplate(String player, String score, String title, String site) {
        this.player = player;
        this.score = score;
        this.title = title;
        this.site = site;
        this.pref = "030";
    }

    public dataTemplate(String player, String score, String title, String site, String pref) {
        this.player = player;
        this.score = score;
        this.title = title;
        this.site = site;
        this.pref = pref;
    }



    public String getPlayer(){
        return this.player;
    }

    public String getScore(){
        return this.score;
    }

    public String getTitle(){
        return this.title;
    }

    public String getSite(){
        if(this.pref != "030"){
            return this.pref + ", " + this.site;
        }
        return this.site;
    }
}
