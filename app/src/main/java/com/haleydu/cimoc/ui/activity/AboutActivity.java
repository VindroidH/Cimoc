package com.haleydu.cimoc.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.haleydu.cimoc.App;
import com.haleydu.cimoc.Constants;
import com.haleydu.cimoc.R;
import com.haleydu.cimoc.manager.PreferenceManager;
import com.haleydu.cimoc.presenter.AboutPresenter;
import com.haleydu.cimoc.presenter.BasePresenter;
import com.haleydu.cimoc.ui.view.AboutView;
import com.haleydu.cimoc.utils.HintUtils;
import com.haleydu.cimoc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Hiroshi on 2016/9/21.
 */

public class AboutActivity extends BackActivity implements AboutView, AdapterView.OnItemSelectedListener {

    @BindView(R.id.about_update_summary)
    TextView mUpdateText;
    @BindView(R.id.about_version_name)
    TextView mVersionName;
    @BindView(R.id.about_layout)
    View mLayoutView;

    private AboutPresenter mPresenter;
    private boolean update = false;
    private boolean checking = false;

    private final List<String> listSources = new ArrayList<>();

    @Override
    protected BasePresenter initPresenter() {
        mPresenter = new AboutPresenter();
        mPresenter.attachView(this);
        return mPresenter;
    }

    @Override
    protected void initView() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            mVersionName.setText(StringUtils.format("Version  %s (%s)", info.versionName, info.versionCode));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.home_page_btn)
    void onHomeClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.home_page_url)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            showSnackbar(R.string.about_resource_fail);
        }
    }

    @OnClick(R.id.about_support_btn)
    void onSupportClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_support_url)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            showSnackbar(R.string.about_resource_fail);
        }
    }

    @OnClick(R.id.about_resource_btn)
    void onResourceClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_resource_url)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            showSnackbar(R.string.about_resource_fail);
        }
    }

    @OnClick(R.id.about_resource_ori_btn)
    void onOriResourceClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_resource_ori_url)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            showSnackbar(R.string.about_resource_fail);
        }
    }

    @OnClick(R.id.about_update_btn)
    void onUpdateClick() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_update_url)));
        try {
            startActivity(intent);
        } catch (Exception e) {
            showSnackbar(R.string.about_resource_fail);
        }
    }

    @Override
    public void onUpdateNone() {
        mUpdateText.setText(R.string.about_update_latest);
        HintUtils.showToast(this, R.string.about_update_latest);
        checking = false;
    }

    @Override
    public void onUpdateReady() {
        update();
        checking = false;
        update = true;
    }

    @Override
    public void onCheckError() {
        mUpdateText.setText(R.string.about_update_fail);
        checking = false;
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.drawer_about);
    }

    @Override
    protected View getLayoutView() {
        return mLayoutView;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_about;
    }

    private void update() {
        mUpdateText.setText(R.string.about_update_summary);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                showSnackbar("请选择一个下载源");
                break;
            case 1:
                App.setUpdateCurrentUrl(Constants.UPDATE_GITHUB_URL);
                update = false;
                App.getPreferenceManager().putString(PreferenceManager.PREF_UPDATE_CURRENT_URL, App.getUpdateCurrentUrl());
                break;
            case 2:
                App.setUpdateCurrentUrl(Constants.UPDATE_GITEE_URL);
                update = false;
                App.getPreferenceManager().putString(PreferenceManager.PREF_UPDATE_CURRENT_URL, App.getUpdateCurrentUrl());
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
}
