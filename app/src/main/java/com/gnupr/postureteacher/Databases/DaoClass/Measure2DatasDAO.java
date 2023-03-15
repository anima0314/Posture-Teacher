package com.gnupr.postureteacher.Databases.DaoClass;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.gnupr.postureteacher.Databases.EntityClass.Measure2DatasEntity;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface Measure2DatasDAO {
    @Insert
    void insert(Measure2DatasEntity measurementTableEntity);

    @Update
    void update(Measure2DatasEntity measurementTableEntity);

    @Delete
    void delete(Measure2DatasEntity measurementTableEntity);

    //Select All Data
    @Query("SELECT * FROM Measure2Datas")
    List<Measure2DatasEntity> getAllData();

    @Query("DELETE FROM Measure2Datas")
    void deleteAll();

    //Select Date Data
    @Query("SELECT * FROM Measure2Datas WHERE Measure2RoundStartTimeFK = :Measure2RoundStartTime")
    List<Measure2DatasEntity> getTimeData(LocalDateTime Measure2RoundStartTime);
}