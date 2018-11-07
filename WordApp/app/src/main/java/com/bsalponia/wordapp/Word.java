package com.bsalponia.wordapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "word_table")
public class Word {

    /*In case if I want to give the Word another name then have to use- @Entity(tableName = "table_name")
    * In case of column name annotate-  @ColumnInfo(name = "column_name") */

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String word;

    public Word(String word){
        this.word= word;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
