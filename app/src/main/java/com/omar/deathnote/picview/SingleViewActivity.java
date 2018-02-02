package com.omar.deathnote.picview;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.omar.deathnote.AppComponent;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;

public class SingleViewActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewpager);
        findViewById(R.id.spinner).setVisibility(View.GONE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int position = getIntent().getIntExtra(Constants.ID, 0);
        ArrayList<String> list = getIntent().getStringArrayListExtra(Constants.LIST);
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new ImagesPagerAdapter(getSupportFragmentManager(), list));
        pager.setCurrentItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
