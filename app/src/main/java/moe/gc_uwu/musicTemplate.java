package moe.gc_uwu;

public class musicTemplate {
    String id;
    String title;

    public musicTemplate(String id, String title){
        this.id = id;
        this.title = title;
    }

    public String getId(){
        return this.id;
    }

    public String getTitle(){
        return this.title;
    }
}
