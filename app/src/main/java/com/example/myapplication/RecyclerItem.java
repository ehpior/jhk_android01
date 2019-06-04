package com.example.myapplication;

public class RecyclerItem {
    private String titleStr ;
    private String descStr ;

    public RecyclerItem(String title, String desc){
        this.titleStr = title;
        this.descStr = desc;
    }

    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
}
