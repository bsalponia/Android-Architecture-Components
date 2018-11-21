package com.bsalponia.mainactivity.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bsalponia.mainactivity.repository.Location;
import com.bsalponia.mainactivity.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder> {

    private List<Location> list;

    public LocationListAdapter( DeleteListener deleteListener, ShareListener shareListener, RowListener rowListener){
        this.deleteListener= deleteListener;
        this.shareListener= shareListener;
        this.rowListener= rowListener;
    }

    public void setList(List<Location> list){
        this.list= list;
        notifyDataSetChanged();
    }

    public interface DeleteListener{
        void onClick(String timeStamp);
    }
    private DeleteListener deleteListener;

    public interface ShareListener{
        void onCLick(Location location);
    }
    private ShareListener shareListener;

    public interface RowListener{
        void onClick(Location location);
    }
    private RowListener rowListener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RelativeLayout relative_= (RelativeLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(relative_);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int pos= position;
        RelativeLayout relative_= holder.relative_;

        TextView txtDate= relative_.findViewById(R.id.txtDate);
        txtDate.setText(formatDate(list.get(pos).getTimeStamp()));

        TextView txtAddress= relative_.findViewById(R.id.txtAddress);
        txtAddress.setText(list.get(pos).getName());

        ImageView imgDelete= relative_.findViewById(R.id.imgDelete);
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteListener!=null)
                    deleteListener.onClick(list.get(pos).getTimeStamp());
            }
        });

        ImageView imgShare= relative_.findViewById(R.id.imgShare);
        imgShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shareListener!=null)
                    shareListener.onCLick(list.get(pos));
            }
        });

        relative_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rowListener!=null)
                    rowListener.onClick(list.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list!=null &&
                list.size()>0)
            return list.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private RelativeLayout relative_;
        public ViewHolder(View itemView) {
            super(itemView);
            relative_= (RelativeLayout)itemView;
        }
    }

    private String formatDate(String timeStamp){
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(Long.valueOf(timeStamp));
        SimpleDateFormat dateFormat= new SimpleDateFormat("hh:mm  dd/MM/yy ");
        return dateFormat.format(calendar.getTime());
    }
}
