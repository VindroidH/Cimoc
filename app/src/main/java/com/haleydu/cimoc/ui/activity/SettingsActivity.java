package com.haleydu.cimoc.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.haleydu.cimoc.App;
import com.haleydu.cimoc.R;
import com.haleydu.cimoc.global.Extra;
import com.haleydu.cimoc.manager.PreferenceManager;
import com.haleydu.cimoc.presenter.BasePresenter;
import com.haleydu.cimoc.presenter.SettingsPresenter;
import com.haleydu.cimoc.saf.DocumentFile;
import com.haleydu.cimoc.service.DownloadService;
import com.haleydu.cimoc.ui.activity.settings.ReaderConfigActivity;
import com.haleydu.cimoc.ui.fragment.dialog.MessageDialogFragment;
import com.haleydu.cimoc.ui.fragment.dialog.StorageEditorDialogFragment;
import com.haleydu.cimoc.ui.view.SettingsView;
import com.haleydu.cimoc.ui.widget.Option;
import com.haleydu.cimoc.ui.widget.preference.CheckBoxPreference;
import com.haleydu.cimoc.ui.widget.preference.ChoicePreference;
import com.haleydu.cimoc.ui.widget.preference.SliderPreference;
import com.haleydu.cimoc.utils.ServiceUtils;
import com.haleydu.cimoc.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * Created by Hiroshi on 2016/9/21.
 */

public class SettingsActivity extends BackActivity implements SettingsView {

    private static final int DIALOG_REQUEST_OTHER_LAUNCH = 0;
    private static final int DIALOG_REQUEST_READER_MODE = 1;
    private static final int DIALOG_REQUEST_OTHER_THEME = 2;
    private static final int DIALOG_REQUEST_OTHER_STORAGE = 3;
    private static final int DIALOG_REQUEST_DOWNLOAD_THREAD = 4;
    private static final int DIALOG_REQUEST_DOWNLOAD_SCAN = 6;
    private static final int DIALOG_REQUEST_OTHER_NIGHT_ALPHA = 7;
    private static final int DIALOG_REQUEST_READER_SCALE_FACTOR = 8;
    private static final int DIALOG_REQUEST_READER_CONTROLLER_TRIG_THRESHOLD = 9;

    List<TextView> mTitleList;
    View mSettingsLayout;
    CheckBoxPreference mReaderKeepBright;
    CheckBoxPreference mReaderHideInfo;
    CheckBoxPreference mReaderHideNav;
    CheckBoxPreference mReaderBanDoubleClick;
    CheckBoxPreference mReaderPaging;
    CheckBoxPreference mReaderCloseAutoResizeImage;
    CheckBoxPreference mReaderPagingReverse;
    CheckBoxPreference mReaderWhiteEdge;
    CheckBoxPreference mReaderWhiteBackground;
    CheckBoxPreference mReaderVolumeKeyControls;
    CheckBoxPreference mSearchAutoComplete;
    CheckBoxPreference mCheckCimocUpdate;
    CheckBoxPreference mCheckSoftwareUpdate;
    ChoicePreference mReaderMode;
    ChoicePreference mOtherLaunch;
    ChoicePreference mOtherTheme;
    SliderPreference mReaderScaleFactor;
    SliderPreference mReaderControllerTrigThreshold;
    CheckBoxPreference mOtherShowTopbar;
    SliderPreference mOtherNightAlpha;
    SliderPreference mDownloadThread;
    CheckBoxPreference mConnectOnlyWifi;
    CheckBoxPreference mLoadCoverOnlyWifi;

    private SettingsPresenter mPresenter;

    private String mStoragePath;
    private String mTempStorage;

    private final int[] mResultArray = new int[6];
    private final Intent mResultIntent = new Intent();

    @Override
    protected BasePresenter initPresenter() {
        mPresenter = new SettingsPresenter();
        mPresenter.attachView(this);
        return mPresenter;
    }

    @Override
    protected void initView() {
        super.initView();
        mTitleList = new ArrayList<>();
        mTitleList.add(findViewById(R.id.settings_reader_title));
        mTitleList.add(findViewById(R.id.settings_download_title));
        mTitleList.add(findViewById(R.id.settings_other_title));
        mTitleList.add(findViewById(R.id.settings_search_title));
        mSettingsLayout = findViewById(R.id.settings_layout);
        mReaderKeepBright = findViewById(R.id.settings_reader_keep_bright);
        mReaderHideInfo = findViewById(R.id.settings_reader_hide_info);
        mReaderHideNav = findViewById(R.id.settings_reader_hide_nav);
        mReaderBanDoubleClick = findViewById(R.id.settings_reader_ban_double_click);
        mReaderPaging = findViewById(R.id.settings_reader_paging);
        mReaderCloseAutoResizeImage = findViewById(R.id.settings_reader_closeautoresizeimage);
        mReaderPagingReverse = findViewById(R.id.settings_reader_paging_reverse);
        mReaderWhiteEdge = findViewById(R.id.settings_reader_white_edge);
        mReaderWhiteBackground = findViewById(R.id.settings_reader_white_background);
        mReaderVolumeKeyControls = findViewById(R.id.settings_reader_volume_key);
        mCheckCimocUpdate = findViewById(R.id.settings_other_check_update);
        mSearchAutoComplete = findViewById(R.id.settings_search_auto_complete);
        mCheckSoftwareUpdate = findViewById(R.id.settings_check_update);
        mReaderMode = findViewById(R.id.settings_reader_mode);
        mOtherLaunch = findViewById(R.id.settings_other_launch);
        mOtherTheme = findViewById(R.id.settings_other_theme);
        mReaderScaleFactor = findViewById(R.id.settings_reader_scale_factor);
        mReaderControllerTrigThreshold = findViewById(R.id.settings_reader_controller_trig_threshold);
        mOtherShowTopbar = findViewById(R.id.settings_reader_show_topbar);
        mOtherNightAlpha = findViewById(R.id.settings_other_night_alpha);
        mDownloadThread = findViewById(R.id.settings_download_thread);
        mConnectOnlyWifi = findViewById(R.id.settings_other_connect_only_wifi);
        mLoadCoverOnlyWifi = findViewById(R.id.settings_other_loadcover_only_wifi);

        Option settingsReadConfig = findViewById(R.id.settings_reader_config);
        settingsReadConfig.setOnClickListener(view -> {
            Intent intent = new Intent(this, ReaderConfigActivity.class);
            startActivity(intent);
        });

        Option settingsOtherStorage = findViewById(R.id.settings_other_storage);
        settingsOtherStorage.setOnClickListener(view -> {
            if (ServiceUtils.isServiceRunning(this, DownloadService.class)) {
                showSnackbar(R.string.download_ask_stop);
            } else {
                StorageEditorDialogFragment fragment = StorageEditorDialogFragment.newInstance(R.string.settings_other_storage,
                        mStoragePath, DIALOG_REQUEST_OTHER_STORAGE);
                fragment.show(getSupportFragmentManager(), null);
            }
        });

        Option settingsDownloadScan = findViewById(R.id.settings_download_scan);
        settingsDownloadScan.setOnClickListener(view -> {
            if (ServiceUtils.isServiceRunning(this, DownloadService.class)) {
                showSnackbar(R.string.download_ask_stop);
            } else {
                MessageDialogFragment fragment = MessageDialogFragment.newInstance(R.string.dialog_confirm,
                        R.string.settings_download_scan_confirm, true, DIALOG_REQUEST_DOWNLOAD_SCAN);
                fragment.show(getSupportFragmentManager(), null);
            }
        });


        Option settingsOtherClearCache = findViewById(R.id.settings_other_clear_cache);
        settingsOtherClearCache.setOnClickListener(view -> {
            showProgressDialog();
            mPresenter.clearCache();
            showSnackbar(R.string.common_execute_success);
            hideProgressDialog();
        });

        mStoragePath = getAppInstance().getDocumentFile().getUri().toString();
        mReaderKeepBright.bindPreference(PreferenceManager.PREF_READER_KEEP_BRIGHT, false);
        mReaderHideInfo.bindPreference(PreferenceManager.PREF_READER_HIDE_INFO, false);
        mReaderHideNav.bindPreference(PreferenceManager.PREF_READER_HIDE_NAV, false);
        mReaderBanDoubleClick.bindPreference(PreferenceManager.PREF_READER_BAN_DOUBLE_CLICK, false);
        mReaderPaging.bindPreference(PreferenceManager.PREF_READER_PAGING, false);
        mReaderCloseAutoResizeImage.bindPreference(PreferenceManager.PREF_READER_CLOSEAUTORESIZEIMAGE, false);
        mReaderPagingReverse.bindPreference(PreferenceManager.PREF_READER_PAGING_REVERSE, false);
        mReaderWhiteEdge.bindPreference(PreferenceManager.PREF_READER_WHITE_EDGE, false);
        mReaderWhiteBackground.bindPreference(PreferenceManager.PREF_READER_WHITE_BACKGROUND, false);
        mReaderVolumeKeyControls.bindPreference(PreferenceManager.PREF_READER_VOLUME_KEY_CONTROLS_PAGE_TURNING, false);
        mSearchAutoComplete.bindPreference(PreferenceManager.PREF_SEARCH_AUTO_COMPLETE, false);
        mCheckCimocUpdate.bindPreference(PreferenceManager.PREF_OTHER_CHECK_UPDATE, false);
        mCheckSoftwareUpdate.bindPreference(PreferenceManager.PREF_OTHER_CHECK_SOFTWARE_UPDATE, false);
        mConnectOnlyWifi.bindPreference(PreferenceManager.PREF_OTHER_CONNECT_ONLY_WIFI, false);
        mLoadCoverOnlyWifi.bindPreference(PreferenceManager.PREF_OTHER_LOADCOVER_ONLY_WIFI, false);
        mOtherShowTopbar.bindPreference(PreferenceManager.PREF_OTHER_SHOW_TOPBAR, false);
        mReaderMode.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_READER_MODE,
                PreferenceManager.READER_MODE_PAGE, R.array.reader_mode_items, DIALOG_REQUEST_READER_MODE);
        mOtherLaunch.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_OTHER_LAUNCH,
                PreferenceManager.HOME_FAVORITE, R.array.launch_items, DIALOG_REQUEST_OTHER_LAUNCH);
        mOtherTheme.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_OTHER_THEME,
                ThemeUtils.THEME_BLUE, R.array.theme_items, DIALOG_REQUEST_OTHER_THEME);
        mReaderScaleFactor.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_READER_SCALE_FACTOR, 200,
                R.string.settings_reader_scale_factor, DIALOG_REQUEST_READER_SCALE_FACTOR);
        mReaderControllerTrigThreshold.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_READER_CONTROLLER_TRIG_THRESHOLD, 30,
                R.string.settings_reader_controller_trig_threshold, DIALOG_REQUEST_READER_CONTROLLER_TRIG_THRESHOLD);
        mOtherNightAlpha.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_OTHER_NIGHT_ALPHA, 0xB0,
                R.string.settings_other_night_alpha, DIALOG_REQUEST_OTHER_NIGHT_ALPHA);
        mDownloadThread.bindPreference(getSupportFragmentManager(), PreferenceManager.PREF_DOWNLOAD_THREAD, 2,
                R.string.settings_download_thread, DIALOG_REQUEST_DOWNLOAD_THREAD);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case DIALOG_REQUEST_OTHER_STORAGE:
                    showProgressDialog();
                    Uri uri = data.getData();
                    int flags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uri, flags);
                    mTempStorage = uri.toString();
                    mPresenter.moveFiles(DocumentFile.fromTreeUri(this, uri));
                    break;
            }
        }
    }

    @Override
    public void onDialogResult(int requestCode, Bundle bundle) {
        switch (requestCode) {
            case DIALOG_REQUEST_READER_MODE:
                mReaderMode.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_INDEX));
                break;
            case DIALOG_REQUEST_READER_SCALE_FACTOR:
                mReaderScaleFactor.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_VALUE));
                break;
            case DIALOG_REQUEST_READER_CONTROLLER_TRIG_THRESHOLD:
                mReaderControllerTrigThreshold.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_VALUE));
                break;
            case DIALOG_REQUEST_OTHER_LAUNCH:
                mOtherLaunch.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_INDEX));
                break;
            case DIALOG_REQUEST_OTHER_THEME:
                int index = bundle.getInt(EXTRA_DIALOG_RESULT_INDEX);
                if (mOtherTheme.getValue() != index) {
                    mOtherTheme.setValue(index);
                    int theme = ThemeUtils.getThemeById(index);
                    setTheme(theme);
                    int primary = ThemeUtils.getResourceId(this, androidx.appcompat.R.attr.colorAccent);
                    int accent = ThemeUtils.getResourceId(this, androidx.appcompat.R.attr.colorAccent);
                    changeTheme(primary, accent);
                    mResultArray[0] = 1;
                    mResultArray[1] = theme;
                    mResultArray[2] = primary;
                    mResultArray[3] = accent;
                    mResultIntent.putExtra(Extra.EXTRA_RESULT, mResultArray);
                    setResult(Activity.RESULT_OK, mResultIntent);
                }
                break;
            case DIALOG_REQUEST_OTHER_STORAGE:
                showSnackbar(R.string.settings_other_storage_not_found);
                break;
            case DIALOG_REQUEST_DOWNLOAD_THREAD:
                mDownloadThread.setValue(bundle.getInt(EXTRA_DIALOG_RESULT_VALUE));
                break;
            case DIALOG_REQUEST_DOWNLOAD_SCAN:
                showProgressDialog();
                mPresenter.scanTask();
                break;
            case DIALOG_REQUEST_OTHER_NIGHT_ALPHA:
                int alpha = bundle.getInt(EXTRA_DIALOG_RESULT_VALUE);
                mOtherNightAlpha.setValue(alpha);
                if (mNightMask != null) {
                    mNightMask.setBackgroundColor(alpha << 24);
                }
                mResultArray[4] = 1;
                mResultArray[5] = alpha;
                mResultIntent.putExtra(Extra.EXTRA_RESULT, mResultArray);
                setResult(Activity.RESULT_OK, mResultIntent);
                break;
        }
    }

    private void changeTheme(int primary, int accent) {
        if (mToolbar != null) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, primary));
        }
        for (TextView textView : mTitleList) {
            textView.setTextColor(ContextCompat.getColor(this, primary));
        }
        ColorStateList stateList = new ColorStateList(new int[][]{{-android.R.attr.state_checked}, {android.R.attr.state_checked}},
                new int[]{0x8A000000, ContextCompat.getColor(this, accent)});
        mReaderKeepBright.setColorStateList(stateList);
        mReaderHideInfo.setColorStateList(stateList);
        mReaderHideNav.setColorStateList(stateList);
        mReaderBanDoubleClick.setColorStateList(stateList);
        mReaderPaging.setColorStateList(stateList);
        mReaderPagingReverse.setColorStateList(stateList);
        mReaderWhiteEdge.setColorStateList(stateList);
        mReaderWhiteBackground.setColorStateList(stateList);
        mSearchAutoComplete.setColorStateList(stateList);
        mCheckCimocUpdate.setColorStateList(stateList);
        mCheckSoftwareUpdate.setColorStateList(stateList);
        mConnectOnlyWifi.setColorStateList(stateList);
        mLoadCoverOnlyWifi.setColorStateList(stateList);
        mOtherShowTopbar.setColorStateList(stateList);
        mReaderCloseAutoResizeImage.setColorStateList(stateList);
        mReaderVolumeKeyControls.setColorStateList(stateList);
    }

    @Override
    public void onFileMoveSuccess() {
        hideProgressDialog();
        mPreference.putString(PreferenceManager.PREF_OTHER_STORAGE, mTempStorage);
        mStoragePath = mTempStorage;
        ((App) getApplication()).initRootDocumentFile();
        showSnackbar(R.string.common_execute_success);
    }

    @Override
    public void onExecuteSuccess() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_success);
    }

    @Override
    public void onExecuteFail() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_fail);
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.drawer_settings);
    }

    @Override
    protected View getLayoutView() {
        return mSettingsLayout;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_settings;
    }

}
