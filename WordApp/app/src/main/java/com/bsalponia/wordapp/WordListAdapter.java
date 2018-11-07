package com.bsalponia.wordapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    private List<Word> list;

    public WordListAdapter(){}

    public void setList(List<Word> list){
        this.list= list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout linear_= (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_row, parent, false);
        return new ViewHolder(linear_);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LinearLayout linear_= holder.linear_;
        TextView txt_= linear_.findViewById(R.id.txt_);
        txt_.setText(list.get(position).getWord());
    }

    @Override
    public int getItemCount() {
        if(list!=null)
            return list.size();
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private LinearLayout linear_;
        public ViewHolder(View itemView) {
            super(itemView);
            linear_= (LinearLayout)itemView;
        }
    }
}
