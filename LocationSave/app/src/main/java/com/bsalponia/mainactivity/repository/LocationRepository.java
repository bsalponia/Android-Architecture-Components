package com.bsalponia.mainactivity.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class LocationRepository {

    private LocationDao locationDao;
    private LiveData<List<Location>> getAllLocation;

    public LocationRepository(Application application){
        LocationRoomDatabase db= LocationRoomDatabase.getDatabase(application);
        locationDao= db.locationDao();
        getAllLocation= locationDao.getAllLocation();
    }

    /*live data stuff runs in background*/
    public LiveData<List<Location>> getGetAllLocation() {
        return getAllLocation;
    }

    /*no live data so no background, thats why Async*/
    public void insert(Location location){
        new insertAsyncTask(locationDao).execute(location);
    }

    private static class insertAsyncTask extends AsyncTask<Location, Void , Void> {

        private LocationDao locationDao;

        private insertAsyncTask(LocationDao locationDao){
            this.locationDao= locationDao;
        }

        @Override
        protected Void doInBackground(Location... locations) {
            locationDao.insert(locations[0]);
            return null;
        }
    }

    public void delete(String timeStamp){
        new deleteAsyncTask(locationDao).execute(timeStamp);
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void , Void> {

        private LocationDao locationDao;

        private deleteAsyncTask(LocationDao locationDao){
            this.locationDao= locationDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            locationDao.delete(strings[0]);
            return null;
        }
    }

}
