package com.haleydu.cimoc.ui.activity;

import android.os.Bundle;
import android.view.View;

import com.haleydu.cimoc.R;
import com.haleydu.cimoc.manager.PreferenceManager;
import com.haleydu.cimoc.presenter.BackupPresenter;
import com.haleydu.cimoc.presenter.BasePresenter;
import com.haleydu.cimoc.ui.fragment.dialog.ChoiceDialogFragment;
import com.haleydu.cimoc.ui.fragment.dialog.MessageDialogFragment;
import com.haleydu.cimoc.ui.view.BackupView;
import com.haleydu.cimoc.ui.widget.Option;
import com.haleydu.cimoc.ui.widget.preference.CheckBoxPreference;
import com.haleydu.cimoc.utils.PermissionUtils;
import com.haleydu.cimoc.utils.StringUtils;

/**
 * Created by Hiroshi on 2016/10/19.
 */

public class BackupActivity extends BackActivity implements BackupView {

    private static final int DIALOG_REQUEST_RESTORE_COMIC = 0;
    private static final int DIALOG_REQUEST_RESTORE_TAG = 1;
    private static final int DIALOG_REQUEST_RESTORE_SETTINGS = 2;
    private static final int DIALOG_REQUEST_RESTORE_CLEAR = 3;

    View mLayoutView;
    CheckBoxPreference mSaveComicAuto;

    private BackupPresenter mPresenter;

    @Override
    protected BasePresenter initPresenter() {
        mPresenter = new BackupPresenter();
        mPresenter.attachView(this);
        return mPresenter;
    }

    @Override
    protected void initView() {
        super.initView();
        mLayoutView = findViewById(R.id.backup_layout);
        mSaveComicAuto = findViewById(R.id.backup_save_comic_auto);
        Option backupSaveComic = findViewById(R.id.backup_save_comic);
        backupSaveComic.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.saveComic();
            } else {
                onFileLoadFail();
            }
        });

        Option backupSaveTag = findViewById(R.id.backup_save_tag);
        backupSaveTag.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.saveTag();
            } else {
                onFileLoadFail();
            }
        });

        Option backupSaveSettings = findViewById(R.id.backup_save_settings);
        backupSaveSettings.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.saveSettings();
            } else {
                onFileLoadFail();
            }
        });

        Option backupRestoreComic = findViewById(R.id.backup_restore_comic);
        backupRestoreComic.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.loadComicFile();
            } else {
                onFileLoadFail();
            }
        });

        Option backupRestoreTag = findViewById(R.id.backup_restore_tag);
        backupRestoreTag.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.loadTagFile();
            } else {
                onFileLoadFail();
            }
        });

        Option backupRestoreSettings = findViewById(R.id.backup_restore_settings);
        backupRestoreSettings.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.loadSettingsFile();
            } else {
                onFileLoadFail();
            }
        });

        Option backupRestoreRecord = findViewById(R.id.backup_clear_record);
        backupRestoreRecord.setOnClickListener(view -> {
            showProgressDialog();
            if (PermissionUtils.hasStoragePermission(this)) {
                mPresenter.loadClearBackupFile();
            } else {
                onFileLoadFail();
            }
        });
        mSaveComicAuto.bindPreference(PreferenceManager.PREF_BACKUP_SAVE_COMIC, true);
    }

    @Override
    public void onDialogResult(int requestCode, Bundle bundle) {
        switch (requestCode) {
            case DIALOG_REQUEST_RESTORE_COMIC:
                showProgressDialog();
                mPresenter.restoreComic(bundle.getString(EXTRA_DIALOG_RESULT_VALUE));
                break;
            case DIALOG_REQUEST_RESTORE_TAG:
                showProgressDialog();
                mPresenter.restoreTag(bundle.getString(EXTRA_DIALOG_RESULT_VALUE));
                break;
            case DIALOG_REQUEST_RESTORE_SETTINGS:
                showProgressDialog();
                mPresenter.restoreSetting(bundle.getString(EXTRA_DIALOG_RESULT_VALUE));
                break;
            case DIALOG_REQUEST_RESTORE_CLEAR:
                showProgressDialog();
                mPresenter.clearBackup();
                break;
        }
    }

    @Override
    public void onComicFileLoadSuccess(String[] file) {
        showChoiceDialog(R.string.backup_restore_comic, file, DIALOG_REQUEST_RESTORE_COMIC);
    }

    @Override
    public void onTagFileLoadSuccess(String[] file) {
        showChoiceDialog(R.string.backup_restore_tag, file, DIALOG_REQUEST_RESTORE_TAG);
    }

    @Override
    public void onSettingsFileLoadSuccess(String[] file) {
        showChoiceDialog(R.string.backup_restore_settings, file, DIALOG_REQUEST_RESTORE_SETTINGS);
    }

    private void showChoiceDialog(int title, String[] item, int request) {
        hideProgressDialog();
        ChoiceDialogFragment fragment = ChoiceDialogFragment.newInstance(title, item, -1, request);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onClearFileLoadSuccess(String[] file) {
        hideProgressDialog();
        MessageDialogFragment fragment = MessageDialogFragment.newInstance(R.string.backup_clear_record,
                R.string.backup_clear_record_notice_summary, true, DIALOG_REQUEST_RESTORE_CLEAR);
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onFileLoadFail() {
        hideProgressDialog();
        showSnackbar(R.string.backup_restore_not_found);
    }

    @Override
    public void onBackupRestoreSuccess() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_success);
    }

    @Override
    public void onClearBackupSuccess() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_clear_success);
    }

    @Override
    public void onClearBackupFail() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_clear_fail);
    }

    @Override
    public void onBackupRestoreFail() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_fail);
    }

    @Override
    public void onBackupSaveSuccess(int size) {
        hideProgressDialog();
        showSnackbar(StringUtils.format(getString(R.string.backup_save_success), size));
    }

    @Override
    public void onBackupSaveFail() {
        hideProgressDialog();
        showSnackbar(R.string.common_execute_fail);
    }

    @Override
    protected String getDefaultTitle() {
        return getString(R.string.drawer_backup);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_backup;
    }

    @Override
    protected View getLayoutView() {
        return mLayoutView;
    }

}
