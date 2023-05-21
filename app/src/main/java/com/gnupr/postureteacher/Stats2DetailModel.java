package com.gnupr.postureteacher;

public class Stats2DetailModel {

    private int id;
    private String time;
    private String percent;

    public String getId() {
        String stid = Integer.toString(id);
        return stid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String first_time) {
        this.time = time;
    }
    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }


    public Stats2DetailModel(){
    }
    public Stats2DetailModel(int id, String time, String percent) {
        this.id = id;
        this.time = time;
        this.percent = percent;
    }

}
