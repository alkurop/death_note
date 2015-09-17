package com.omar.deathnote.notes.item.ui;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import com.omar.deathnote.R;

/**
 * Created by omar on 9/8/15.
 */
public class NoteFragment extends BaseItemFragment {
    @InjectView(R.id.etTxt)
    EditText etText;



    @Override
    public void setContent1(String content1) {
        etText.setText(content1);
    }

    @Override
    public void setContent2(String content2) {

    }

    @Override
    public String getContent1() {
        return etText.getText().toString();
    }

    @Override
    public String getContent2() {
        return null;
    }

    @Override
    public int getLayout() {
        return  R.layout.note_elem_note;
    }

    @Override
    public void requestFocus() {


        etText.requestFocus();
    }


    @OnClick(R.id.tvTitle)
    void hideKeyboard(){
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }




}
