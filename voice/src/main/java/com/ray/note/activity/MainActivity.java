package com.ray.note.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ray.api.activity.BaseActivity;
import com.ray.note.R;
import com.ray.note.fragment.NoteMainFragment;
import com.ray.note.utils.Constant;

/**
 * Created by zhangll on 15/5/22.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private NoteMainFragment noteMainFragment;

    private ImageView ivNote, ivListen, ivMy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);

        ivNote = (ImageView) findViewById(R.id.iv_note);
        ivListen = (ImageView) findViewById(R.id.iv_listen);
        ivMy = (ImageView) findViewById(R.id.iv_my);

        setFragmentSelection(Constant.FRAGMENT_MAIN_LISTEN);
    }

    private void setFragmentSelection(String fragmentFlag) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (Constant.FRAGMENT_MAIN_NOTE.equals(fragmentFlag)) {

        } else if (Constant.FRAGMENT_MAIN_LISTEN.equals(fragmentFlag)) {

        } else if (Constant.FRAGMENT_MAIN_MY.equals(fragmentFlag)) {

        }

        transaction.commit();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        ivNote.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == ivNote) {

        } else if (v == ivListen) {

        } else if (v == ivMy) {

        }
    }
}
