package com.gnupr.postureteacher.Databases.EntityClass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.*;
import java.time.LocalDateTime;


@Entity(tableName = "Measure2Rounds")
public class Measure2RoundsEntity {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int Measure2RoundID;

    @ColumnInfo
    private LocalDateTime Measure2RoundStartTime;

    @ColumnInfo
    private LocalDateTime Measure2RoundEndTime;

    public Measure2RoundsEntity() {
    }

    public Measure2RoundsEntity(int DB_Measure2RoundID, LocalDateTime DB_Measure2RoundStartTime, LocalDateTime DB_Measure2RoundEndTime) {
        this.Measure2RoundID = DB_Measure2RoundID;
        this.Measure2RoundStartTime = DB_Measure2RoundStartTime;
        this.Measure2RoundEndTime = DB_Measure2RoundEndTime;
    }

    public int getMeasure2RoundID() {
        return Measure2RoundID;
    }
    public void setMeasure2RoundID(int Measure2RoundID) {
        this.Measure2RoundID = Measure2RoundID;
    }

    public LocalDateTime getMeasure2RoundStartTime() { return Measure2RoundStartTime; }
    public void setMeasure2RoundStartTime(LocalDateTime Measure2RoundStartTime) {
        this.Measure2RoundStartTime = Measure2RoundStartTime;
    }

    public LocalDateTime getMeasure2RoundEndTime() { return Measure2RoundEndTime; }
    public void setMeasure2RoundEndTime(LocalDateTime Measure2RoundEndTime) {
        this.Measure2RoundEndTime = Measure2RoundEndTime;
    }
}