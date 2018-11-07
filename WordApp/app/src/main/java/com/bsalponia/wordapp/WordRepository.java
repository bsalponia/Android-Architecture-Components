package com.bsalponia.wordapp;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class WordRepository {

    private WordDao wordDao;
    private LiveData<List<Word>> allWords;

    public WordRepository(Application application){
        WordRoomDatabase db= WordRoomDatabase.getDatabase(application);
        wordDao= db.wordDao();
        allWords= wordDao.getAllWords();
    }

    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }

    public void insert(Word word){
        new insertAsyncTask(wordDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<Word, Void , Void>{

        private WordDao wordDao;

        private insertAsyncTask(WordDao wordDao){
            this.wordDao= wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insert(words[0]);
            return null;
        }
    }

}
