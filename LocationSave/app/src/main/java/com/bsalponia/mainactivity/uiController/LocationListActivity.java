package com.bsalponia.mainactivity.uiController;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bsalponia.mainactivity.R;
import com.bsalponia.mainactivity.adapter.LocationListAdapter;
import com.bsalponia.mainactivity.repository.Location;
import com.bsalponia.mainactivity.viewmodel.LocationViewModel;

import java.util.List;

public class LocationListActivity extends AppCompatActivity {

    private static final String TAG= LocationListActivity.class.getSimpleName();

    private LocationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        viewModel= ViewModelProviders.of(this).get(LocationViewModel.class);

        RecyclerView recycler_= findViewById(R.id.recycler_);
        recycler_.setLayoutManager(new LinearLayoutManager(this));

        final LocationListAdapter adapter= new LocationListAdapter(new LocationListAdapter.DeleteListener() {
            @Override
            public void onClick(String timeStamp) {
                viewModel.delete(timeStamp);
            }
        }, new LocationListAdapter.ShareListener() {
            @Override
            public void onCLick(Location location) {
                sendEmail(location);
            }
        }, new LocationListAdapter.RowListener() {
            @Override
            public void onClick(Location location) {
                Intent intent= new Intent(LocationListActivity.this, SavedMapActivity.class);
                intent.putExtra(SavedMapActivity.ADDRESS, location.getName());
                intent.putExtra(SavedMapActivity.LATITUDE, location.getLatitude());
                intent.putExtra(SavedMapActivity.LONGITUDE, location.getLongitude());
                startActivity(intent);
            }
        });
        recycler_.setAdapter(adapter);

        viewModel.getAllLocation().observe(this, new Observer<List<Location>>() {
            @Override
            public void onChanged(@Nullable List<Location> locations) {
                adapter.setList(locations);
            }
        });
    }

    private void sendEmail(Location location){

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email will handle this
        String subject= "My Saved Location";
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        String extraText= location.getName();
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
