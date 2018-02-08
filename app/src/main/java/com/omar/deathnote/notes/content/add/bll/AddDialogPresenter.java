package com.omar.deathnote.notes.content.add.bll;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.omar.deathnote.App;
import com.omar.deathnote.R;
import com.omar.deathnote.notes.content.ContentType;
import com.omar.deathnote.notes.content.add.item.AddDialogItemDataHolder;
import com.omar.deathnote.notes.content.add.item.IAddDialogItemDataHolder;
import com.omar.deathnote.notes.content.add.ui.IAddDialogView;

import java.util.ArrayList;
import java.util.List;

public class AddDialogPresenter implements IAddDialogPresenter {
    private IAddDialogCallback callback;
    private IAddDialogView view;
    private AddDialogAdaper adaper;
    private Context context;
    private List<IAddDialogItemDataHolder> items = new ArrayList<>();

    @Override
    public void setView(IAddDialogView view) {
        this.view = view;
    }

    @Override
    public void displayView() {
        adaper = new AddDialogAdaper(items);
        ((RecyclerView) view.getRecyclerView()).setAdapter(adaper);
        generateBaseListItems();
    }

    @Override
    public void init(IAddDialogCallback callback) {
        context = App.getContext();
        this.callback = callback;
    }

    @Override
    public void generateBaseListItems() {
        view.setTitleLabel(context.getString(R.string.add_content));
        items.clear();
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_pic)) {
            @Override
            public void doAction() {
                generatePicListItems();
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_audio_record)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentType.AUDIO_RECORD);
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_note)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentType.NOTE);
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_link)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentType.LINK);
            }
        });
        adaper.notifyDataSetChanged();
    }

    @Override
    public void generateAudioListItems() {
        view.setTitleLabel(context.getString(R.string.add_audio));
        items.clear();
        items.add(new AddDialogItemDataHolder(context.getString(R.string.audio_from_media)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent( ContentType.AUDIO_FILE);
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.audio_from_recorder)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentType.AUDIO_RECORD);
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.back)) {
            @Override
            public void doAction() {
                generateBaseListItems();
            }
        });
        adaper.notifyDataSetChanged();
    }

    @Override
    public void generatePicListItems() {
        view.setTitleLabel(context.getString(R.string.add_picture));
        items.clear();
        items.add(new AddDialogItemDataHolder(context.getString(R.string.pic_from_gallery)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentType.PICTURE_FILE);
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.pic_from_camera)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentType.PICTURE_CAPTURE);
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.back)) {
            @Override
            public void doAction() {
                generateBaseListItems();
            }
        });
        adaper.notifyDataSetChanged();
    }

    public interface IAddDialogCallback {
        void addContent(ContentType content);
    }
}
