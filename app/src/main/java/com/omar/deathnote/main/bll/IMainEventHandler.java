package com.omar.deathnote.main.bll;

import com.omar.deathnote.main.ui.IMainView;

/**
 * Created by omar on 8/27/15.
 */
public interface IMainEventHandler {

    void init(IMainView _view);

    void fabClicked();

    void loadData();



}
