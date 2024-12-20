package com.haleydu.cimoc.core;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.haleydu.cimoc.App;
import com.haleydu.cimoc.R;
import com.king.app.dialog.AppDialog;
import com.king.app.updater.AppUpdater;
import com.king.app.updater.http.OkHttpManager;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;

//import com.azhon.appupdate.config.UpdateConfiguration;
//import com.azhon.appupdate.manager.DownloadManager;

/**
 * Created by Hiroshi on 2016/8/24.
 */
public class Update {

    private static final String UPDATE_URL = "https://api.github.com/repos/VindroidH/cimoc/releases/latest";
    private static final String UPDATE_URL_GITHUB = "https://raw.githubusercontent.com/VindroidH/update/master/Update.json";
    private static final String UPDATE_URL_GITEE = "https://gitee.com/VindroidH/update/raw/master/Update.json";
    private static final String SERVER_FILENAME = "tag_name";
    private AppUpdater mAppUpdater;
//    private static final String LIST = "list";

    public static Observable<String> check() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient client = App.getHttpClient();
                Request request = new Request.Builder().url(UPDATE_URL).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String json = response.body().string();
//                        JSONObject object = new JSONObject(json).getJSONArray(LIST).getJSONObject(0);
                        String version = new JSONObject(json).getString(SERVER_FILENAME);
                        subscriber.onNext(version);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
                subscriber.onError(new Exception());
            }
        }).subscribeOn(Schedulers.io());
    }

    public static Observable<String> checkGitee() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient client = App.getHttpClient();
                Request request = new Request.Builder().url(UPDATE_URL_GITEE).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        String json = response.body().string();
                        subscriber.onNext(json);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (response != null) {
                        response.close();
                    }
                }
                subscriber.onError(new Exception());
            }
        }).subscribeOn(Schedulers.io());
    }

    public void startUpdate(String versionName, String content, String mUrl, int versionCode, String md5) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update, null);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText(R.string.main_start_update);
        tvTitle.append(versionName);
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(content);

        Button btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppDialog.INSTANCE.dismissDialog();
            }
        });
        Button btnOK = view.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAppUpdater = new AppUpdater.Builder()
                        .setApkMD5(md5)//支持MD5校验，如果缓存APK的MD5与此MD5相同，则直接取本地缓存安装，推荐使用MD5校验的方式
                        .setUrl(mUrl)
                        .setVersionCode(versionCode)//支持versionCode校验，设置versionCode之后，新版本versionCode相同的apk只下载一次,优先取本地缓存,推荐使用MD5校验的方式
                        .setVibrate(true)  //振动
                        .setFilename("Cimoc_" + versionName + ".apk")
                        .build(getContext());

                mAppUpdater.setHttpManager(OkHttpManager.getInstance()).start();
                AppDialog.INSTANCE.dismissDialog();
            }
        });

        AppDialog.INSTANCE.showDialog(getContext(), view);
    }

    public Context getContext() {
        return App.getActivity();
    }
}
