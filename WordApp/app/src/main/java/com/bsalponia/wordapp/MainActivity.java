package com.bsalponia.wordapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int NEWWORDACTIVITY_REQUESTCODE= 122;

    private WordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recycler_= findViewById(R.id.recycler_);
        recycler_.setLayoutManager(new LinearLayoutManager(this));

        final WordListAdapter adapter= new WordListAdapter();
        recycler_.setAdapter(adapter);

        viewModel= ViewModelProviders.of(this).get(WordViewModel.class);
        viewModel.getAllWords().observe(this, new Observer<List<Word>>() {
            @Override
            public void onChanged(@Nullable List<Word> words) {
                adapter.setList(words);
            }
        });




        FloatingActionButton fab_= findViewById(R.id.fab_);
        fab_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent= new Intent(v.getContext(), NewWordActivity.class);
                startActivityForResult(intent, NEWWORDACTIVITY_REQUESTCODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== NEWWORDACTIVITY_REQUESTCODE){
            if(resultCode== RESULT_OK){

                Word word= new Word(data.getStringExtra(NewWordActivity.NEW_WORD));
                viewModel.insert(word);

            }else{      //result cancelled
                Toast.makeText(this, "Empty field", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
