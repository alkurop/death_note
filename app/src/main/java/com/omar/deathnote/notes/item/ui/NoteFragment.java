package com.omar.deathnote.notes.item.ui;

import android.widget.EditText;
import butterknife.InjectView;
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


}
