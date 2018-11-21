package com.bsalponia.mainactivity.repository;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LocationDao {

    @Insert
    void insert(Location value);

    @Query("DELETE FROM location_table WHERE timeStamp= :timeStamp")
    void delete(String timeStamp);

    @Query("SELECT * FROM location_table ORDER BY timeStamp DESC")
    LiveData<List<Location>> getAllLocation();
}
