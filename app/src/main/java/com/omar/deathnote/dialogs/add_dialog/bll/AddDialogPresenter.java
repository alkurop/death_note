package com.omar.deathnote.dialogs.add_dialog.bll;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import com.omar.deathnote.App;
import com.omar.deathnote.R;
import com.omar.deathnote.dialogs.add_dialog.item.AddDialogItemDataHolder;
import com.omar.deathnote.dialogs.add_dialog.item.IAddDialogItemDataHolder;
import com.omar.deathnote.dialogs.add_dialog.ui.IAddDialogView;
import com.omar.deathnote.models.Content;
import com.omar.deathnote.models.ContentFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omar on 9/15/15.
 */
public class AddDialogPresenter implements IAddDialogPresenter {
    private IAddDialogCallback callback;
    private IAddDialogView view;

    private AddDialogAdaper adaper;
    private Context context;

    @Override
    public void setView(IAddDialogView view) {
        this.view = view;
    }

    @Override
    public void displayView() {
        adaper = new AddDialogAdaper();
        (((RecyclerView) view.getRecyclerView())).setAdapter(adaper);
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
        List<IAddDialogItemDataHolder>  items = new ArrayList<>();
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_pic)) {
            @Override
            public void doAction() {
                generatePicListItems();
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_audio)) {
            @Override
            public void doAction() {
                generateAudioListItems();
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_note)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentFactory.getContent(Content.ContentType.NOTE));
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.new_link)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentFactory.getContent(Content.ContentType.LINK));
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.back)) {
            @Override
            public void doAction() {
                view.dismiss();
            }
        });
        adaper.setContent(items);
    }

    @Override
    public void generateAudioListItems() {
        view.setTitleLabel(context.getString(R.string.add_audio));
        List<IAddDialogItemDataHolder>  items = new ArrayList<>();
        items.add(new AddDialogItemDataHolder(context.getString(R.string.audio_from_media)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentFactory.getContent(Content.ContentType.AUDIO_FILE));
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.audio_from_recorder)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentFactory.getContent(Content.ContentType.AUDIO_RECORD));
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.back)) {
            @Override
            public void doAction() {
                generateBaseListItems();
            }
        });
        adaper.setContent(items);
    }

    @Override
    public void generatePicListItems() {
        view.setTitleLabel(context.getString(R.string.add_picture));
        List<IAddDialogItemDataHolder>  items = new ArrayList<>();
        items.add(new AddDialogItemDataHolder(context.getString(R.string.pic_from_gallery)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentFactory.getContent(Content.ContentType.PICTURE_FILE));
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.pic_from_camera)) {
            @Override
            public void doAction() {
                view.dismiss();
                callback.addContent(ContentFactory.getContent(Content.ContentType.PICTURE_CAPTURE));
            }
        });
        items.add(new AddDialogItemDataHolder(context.getString(R.string.back)) {
            @Override
            public void doAction() {
                view.dismiss();
            }
        });
        adaper.setContent(items);
    }


    public interface IAddDialogCallback {
        void addContent(Content content);
    }
}
