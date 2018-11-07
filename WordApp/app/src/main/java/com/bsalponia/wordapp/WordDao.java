package com.bsalponia.wordapp;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface WordDao {

    /*By default when there's a conflict it's replace but I can change it- @Insert(onConflict = OnConflictStrategy.REPLACE)*/
    @Insert
    void insert(Word word);

    @Query("DELETE FROM word_table")
    void delete();

    /*LiveData is an observable library
    * LiveData runs the query asynchronously on a background thread, when needed. So I don't have to run a thread myself*/
    @Query("SELECT * FROM word_table ORDER BY id ASC")
    LiveData<List<Word>> getAllWords();


}
