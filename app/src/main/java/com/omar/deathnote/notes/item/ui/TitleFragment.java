package com.omar.deathnote.notes.item.ui;

import android.widget.EditText;
import butterknife.InjectView;
import com.omar.deathnote.R;

/**
 * Created by omar on 9/8/15.
 */
public class TitleFragment extends BaseItemFragment {
    @InjectView(R.id.tvTitle)
    EditText etTitle;


    @Override
    public void SetContent1(String content1) {
        etTitle.setText(content1);
    }

    @Override
    public void SetContent2(String content2) {

    }

    @Override
    public String GetContent1() {
        return etTitle.getText().toString();
    }

    @Override
    public String GetContent2() {
        return null;
    }

    @Override
    public int GetLayout() {
        return  R.layout.note_elem_default;
    }
}
