package com.gnupr.postureteacher;

public class StatsDetailModel {

    private int id;
    private String first_time;
    private String end_time;
    private String total_time;

    public String getId() {
        String stid = Integer.toString(id);
        return stid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_time() {
        return first_time;
    }

    public void setTime(String first_time) {
        this.first_time = first_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTotal_time() {
        return total_time;
    }

    public void setTotal_time(String total_time) {
        this.total_time = total_time;
    }


    public StatsDetailModel(){
    }
    public StatsDetailModel(int id, String first_time, String end_time, String total_time) {
        this.id = id;
        this.first_time = first_time;
        this.end_time = end_time;
        this.total_time = total_time;
    }

}
