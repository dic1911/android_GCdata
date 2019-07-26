package moe.gc_uwu;

public class musicTemplate {
    String id;
    String title;
    String last_play;

    public musicTemplate(){}

    public musicTemplate(String id, String title){
        this.id = id;
        this.title = title;
    }

    public musicTemplate(String id, String title, String date){
        this.id = id;
        this.title = title;
        this.last_play = date;
    }

    public String getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDate(){
        return this.last_play;
    }
}
