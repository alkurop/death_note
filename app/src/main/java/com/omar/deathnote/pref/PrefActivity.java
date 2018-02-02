package com.omar.deathnote.pref;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;
import com.omar.deathnote.R;

import io.reactivex.functions.Consumer;

public class PrefActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_about);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        RxView.clicks(findViewById(R.id.email)).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Intent intent  = new Intent(PrefActivity.this, Contact.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
