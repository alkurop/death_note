package com.omar.deathnote.notes.item.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import com.omar.deathnote.R;

/**
 * Created by omar on 9/8/15.
 */
public class LinkFragment extends BaseItemFragment {
    @BindView(R.id.etText)
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
        return  R.layout.note_elem_link;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linkify();
    }

    private void linkify(){
        etText.setLinksClickable(true);
        Linkify.addLinks(etText, Linkify.WEB_URLS);
        etText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (Patterns.WEB_URL.matcher(s).matches()) {
                    Linkify.addLinks(etText, Linkify.WEB_URLS);
                    etText.setLinksClickable(true);
                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Patterns.WEB_URL.matcher(s).matches()) {
                    Linkify.addLinks(etText, Linkify.WEB_URLS);
                    etText.setLinksClickable(false);
                }
            }
        });
    }


}
