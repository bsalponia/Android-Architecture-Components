package com.bsalponia.wordapp;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Word.class}, version = 1)
public abstract class WordRoomDatabase extends RoomDatabase {

    public abstract WordDao wordDao();

    private static volatile WordRoomDatabase database;

    public static WordRoomDatabase getDatabase(Context context){
        if(database== null){
            synchronized (WordRoomDatabase.class){
                if(database== null){
                    database= Room.databaseBuilder(context.getApplicationContext(),
                            WordRoomDatabase.class, "word_database")
//                            .addCallback(databaseCallback)
                            .build();
                }
            }
        }
        return database;
    }

    /*In case if i want to perform any operation when the db is open, then onOpen callback is provided.*/
//    private static RoomDatabase.Callback databaseCallback= new RoomDatabase.Callback(){
//        @Override
//        public void onOpen(@NonNull SupportSQLiteDatabase db) {
//            super.onOpen(db);
//            perform operation here in background thread
//        }
//    };


}
