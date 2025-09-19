package com.haleydu.cimoc.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.haleydu.cimoc.R;
import com.haleydu.cimoc.global.Extra;
import com.haleydu.cimoc.presenter.BasePresenter;
import com.haleydu.cimoc.presenter.SourceDetailPresenter;
import com.haleydu.cimoc.ui.view.SourceDetailView;
import com.haleydu.cimoc.ui.widget.Option;

/**
 * Created by Hiroshi on 2017/1/18.
 */

public class SourceDetailActivity extends BackActivity implements SourceDetailView {

    Option mSourceType;
    Option mSourceTitle;
    Option mSourceFavorite;
    private SourceDetailPresenter mPresenter;

    public static Intent createIntent(Context context, int type) {
        Intent intent = new Intent(context, SourceDetailActivity.class);
        intent.putExtra(Extra.EXTRA_SOURCE, type);
        return intent;
    }

    @Override
    protected void initView() {
        super.initView();
        mSourceType = findViewById(R.id.source_detail_type);
        mSourceTitle = findViewById(R.id.source_detail_title);
        mSourceFavorite = findViewById(R.id.source_detail_favorite);
    }

    @Override
    protected BasePresenter initPresenter() {
        mPresenter = new SourceDetailPresenter();
        mPresenter.attachView(this);
        return mPresenter;
    }

    @Override
    protected void initData() {
        mPresenter.load(getIntent().getIntExtra(Extra.EXTRA_SOURCE, -1));
    }

    @Override
    public void onSourceLoadSuccess(int type, String title, long count) {
        mSourceType.setSummary(String.valueOf(type));
        mSourceTitle.setSummary(title);
        mSourceFavorite.setSummary(String.valueOf(count));
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_source_detail;
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.source_detail);
    }

}
