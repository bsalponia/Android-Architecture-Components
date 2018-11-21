package com.bsalponia.mainactivity.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Location.class}, version= 1)
public abstract class LocationRoomDatabase extends RoomDatabase{

    public abstract LocationDao locationDao();

    private static volatile LocationRoomDatabase database;

    public static LocationRoomDatabase getDatabase(Context context){
        if(database==null){
            synchronized (LocationRoomDatabase.class){
                database= Room.databaseBuilder(context.getApplicationContext(),
                        LocationRoomDatabase.class, "location_database").build();
            }
        }
        return database;
    }
}
