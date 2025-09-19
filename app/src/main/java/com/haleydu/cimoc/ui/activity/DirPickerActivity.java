package com.haleydu.cimoc.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.haleydu.cimoc.R;
import com.haleydu.cimoc.global.Extra;
import com.haleydu.cimoc.ui.adapter.BaseAdapter;
import com.haleydu.cimoc.ui.adapter.DirAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hiroshi on 2016/12/6.
 */

public class DirPickerActivity extends CoordinatorActivity {

    private DirAdapter mDirAdapter;
    private File mFile;

    @Override
    protected void initView() {
        super.initView();
        FloatingActionButton coordinatorAction = findViewById(R.id.coordinator_action_button);
        coordinatorAction.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.putExtra(Extra.EXTRA_PICKER_PATH, mFile.getAbsolutePath());
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
    }

    @Override
    protected BaseAdapter initAdapter() {
        mDirAdapter = new DirAdapter(this, new ArrayList<String>());
        return mDirAdapter;
    }

    @Override
    protected void initActionButton() {
        mActionButton.setImageResource(R.drawable.ic_done_white_24dp);
        mActionButton.show();
    }

    @Override
    protected void initData() {
        mFile = Environment.getExternalStorageDirectory();
        updateData();
        hideProgressBar();
    }

    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            if (mFile.getParentFile() == null) {
                return;
            }
            mFile = mFile.getParentFile();
        } else {
            String title = mDirAdapter.getItem(position);
            mFile = new File(mFile.getAbsolutePath(), title);
        }
        updateData();
        mActionButton.show();
    }

    private void updateData() {
        mDirAdapter.setData(listDir(mFile));
        if (mToolbar != null) {
            mToolbar.setTitle(mFile.getAbsolutePath());
        }
    }

    private List<String> listDir(File parent) {
        List<String> list = new ArrayList<>();
        File[] files = parent.listFiles();
        if (files != null) {
            for (File dir : parent.listFiles()) {
                if (dir.isDirectory()) {
                    list.add(dir.getName());
                }
            }
            Collections.sort(list);
        }
        list.add(0, getString(R.string.dir_picker_parent));
        return list;
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.dir_picker);
    }

}
