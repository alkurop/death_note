package com.omar.deathnote.notes.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.omar.deathnote.App;
import com.omar.deathnote.R;
import com.omar.deathnote.models.ContentItem;
import com.omar.deathnote.notes.bll.INoteEventHandler;
import com.omar.deathnote.notes.item.bll.IContentEventHandler;

import java.util.List;

public class NoteActivity extends AppCompatActivity implements INoteView {

	@InjectView(R.id.noteList) LinearLayout ll_main;
	private INoteEventHandler presenter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);
		ButterKnife.inject(this);
		presenter = App.getNotePresenter();
		presenter.SetView(this);
	}


	@Override
	public void DisplayFragment(int UID, Fragment fragment) {
		FrameLayout container =(FrameLayout) LayoutInflater.from(this).inflate(R.layout.content_container, null,false);
		container.setId(UID);
		ll_main.addView(container);
		getSupportFragmentManager().beginTransaction().add(UID, fragment).commit();
	}

	@Override
	public void RemoveFragment(int UID) {
		Fragment fragment = getSupportFragmentManager().findFragmentById(UID);
		getSupportFragmentManager().beginTransaction().remove(fragment).commit();
		ll_main.removeView(ll_main.findViewById(UID));
	}
}
