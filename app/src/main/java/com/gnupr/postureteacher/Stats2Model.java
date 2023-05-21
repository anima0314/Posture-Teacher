package com.gnupr.postureteacher;

public class Stats2Model {
    private int id;
    private String cycletime;
    private String percent;
    private String laps;

    public String getId() {
        String stid = Integer.toString(id);
        return stid;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCycletime() {
        return cycletime;
    }

    public void setCycletime(String cycletime) {
        this.cycletime = cycletime;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }

    public String getLaps() {
        return laps;
    }

    public void setLaps(String laps) {
        this.laps = laps;
    }


    public Stats2Model(){
    }
    public Stats2Model(int id, String cycletime, String percent, String unstable) {
        this.id = id;
        this.cycletime = cycletime;
        this.percent = percent;
        this.laps = laps;
    }
}
