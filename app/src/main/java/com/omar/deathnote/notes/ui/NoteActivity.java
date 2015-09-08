package com.omar.deathnote.notes.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.support.v7.widget.Toolbar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.omar.deathnote.App;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.omar.deathnote.dialogs.AddAudioDialog;
import com.omar.deathnote.dialogs.AddPicDialog;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.utility.SharingModule;

import java.util.List;

public class NoteActivity extends AppCompatActivity implements INoteView {

    @InjectView(R.id.noteList)
    LinearLayout ll_main;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private INoteEventHandler presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.inject(this);
        presenter = App.getNotePresenter();
        presenter.SetView(this);

        if (savedInstanceState == null) presenter.GetContentId(getIntent());
        else presenter.DisplayView();

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.SaveContent();
    }

    @Override
    public void DisplayFragment(Content item, Fragment fragment) {
        FrameLayout container = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.content_container, null, false);
        container.setId(item.getUID());
        ll_main.addView(container);
        getSupportFragmentManager().beginTransaction().add(item.getUID(), fragment).commit();
    }


    @Override
    public void RemoveFragment(Content item) {
        Fragment fragment = getSupportFragmentManager().findFragmentById(item.getUID());
        View view = ll_main.findViewById(item.getUID());

        if (fragment != null) getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        if (view != null) ll_main.removeView(view);
    }

    @Override
    public void ClearList(List<Content> contentList) {
        for (Content item : contentList) {
            RemoveFragment(item);
        }
    }

    @Override
    public void InitToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        presenter.SaveContent();
        switch (item.getItemId()) {

            case R.id.action_share:
                break;

            case R.id.save:
                onBackPressed();
                break;

            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
