package com.omar.deathnote.main.bll;

import com.omar.deathnote.main.ui.IMainView;

/**
 * Created by omar on 8/27/15.
 */
public interface IMainEventHandler {

    void Init(IMainView _view);

    void DisplayView();

    void FabClicked();

    void SetList();

}
