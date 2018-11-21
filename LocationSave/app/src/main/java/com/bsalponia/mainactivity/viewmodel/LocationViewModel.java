package com.bsalponia.mainactivity.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.bsalponia.mainactivity.repository.Location;
import com.bsalponia.mainactivity.repository.LocationRepository;

import java.util.List;

public class LocationViewModel extends AndroidViewModel {

    private LocationRepository repository;
    private LiveData<List<Location>> allLocation;

    public LocationViewModel(@NonNull Application application){
        super(application);
        repository= new LocationRepository(application);
        allLocation= repository.getGetAllLocation();
    }

    public LiveData<List<Location>> getAllLocation() {
        return allLocation;
    }

    public void insert(Location location){
        repository.insert(location);
    }

    public void delete(String timeStamp){
        repository.delete(timeStamp);
    }

}
