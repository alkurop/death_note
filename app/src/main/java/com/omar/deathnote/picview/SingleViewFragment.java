package com.omar.deathnote.picview;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alkurop.database.Content;
import com.alkurop.database.ContentDao;
import com.omar.deathnote.AppComponent;
import com.omar.deathnote.ComponentContainer;
import com.omar.deathnote.Constants;
import com.omar.deathnote.R;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;

public class SingleViewFragment extends Fragment {

    @Inject
    ContentDao mContentDao;

    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ComponentContainer.getInstance().get(AppComponent.class).inject(this);
        return inflater.inflate(R.layout.single_view, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        Long id = Long.parseLong(getArguments().getString(Constants.PATH));
        Disposable subscribe = mContentDao.getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(mainThread())
                .subscribe(new Consumer<Content>() {
                    @Override
                    public void accept(Content content1) throws Exception {
                        ImageView imageView = getView().findViewById(R.id.imageview);
                        Picasso.with(getContext()).load(content1.getContent())
                                .resize(0, view.getMeasuredHeight())
                                .into(imageView);
                    }
                });
        mCompositeDisposable.add(subscribe);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCompositeDisposable.clear();
    }
}

