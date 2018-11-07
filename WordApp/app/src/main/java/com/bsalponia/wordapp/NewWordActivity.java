package com.bsalponia.wordapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class NewWordActivity extends AppCompatActivity {

    public static final String NEW_WORD= NewWordActivity.class.getSimpleName()+".word";

    private EditText etxt_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_word);

        etxt_= findViewById(R.id.etxt_);

        Button btn_= findViewById(R.id.btn_);
        btn_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent();
                if(etxt_.getText().length()==0){
                    setResult(RESULT_CANCELED, intent);
                }else{
                    intent.putExtra(NEW_WORD, etxt_.getText().toString());
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        });
    }
}
