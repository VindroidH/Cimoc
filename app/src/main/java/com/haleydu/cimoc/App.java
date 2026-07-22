package com.haleydu.cimoc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.DownsampleMode;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.haleydu.cimoc.component.AppGetter;
import com.haleydu.cimoc.core.Storage;
import com.haleydu.cimoc.fresco.ControllerBuilderProvider;
import com.haleydu.cimoc.fresco.OkHttpNetworkFetcher;
import com.haleydu.cimoc.helper.UpdateHelper;
import com.haleydu.cimoc.manager.PreferenceManager;
import com.haleydu.cimoc.manager.SourceManager;
import com.haleydu.cimoc.misc.ActivityLifecycle;
import com.haleydu.cimoc.model.DaoSession;
import com.haleydu.cimoc.model.MyObjectBox;
import com.haleydu.cimoc.saf.DocumentFile;
import com.haleydu.cimoc.ui.adapter.GridAdapter;
import com.haleydu.cimoc.utils.DocumentUtils;
import com.haleydu.cimoc.utils.StringUtils;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.recyclerview.widget.RecyclerView;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.objectbox.BoxStore;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

/**
 * Created by Hiroshi on 2016/7/5.
 */
public class App extends MultiDexApplication implements AppGetter, Thread.UncaughtExceptionHandler {

    public static int mWidthPixels;
    public static int mHeightPixels;
    public static int mCoverWidthPixels;
    public static int mCoverHeightPixels;
    public static int mLargePixels;

    private static OkHttpClient mHttpClient;
    private static PreferenceManager mPreferenceManager;
    private static WifiManager mWifiManager;
    private static App mApp;
    private static Activity sActivity;
    private DocumentFile mDocumentFile;
    private ControllerBuilderProvider mBuilderProvider;
    private RecyclerView.RecycledViewPool mRecycledPool;
    private DaoSession mDaoSession;
    private ActivityLifecycle mActivityLifecycle;

    public static Context getAppContext() {
        return mApp;
    }

    public static Resources getAppResources() {
        return mApp.getResources();
    }

    public static Activity getActivity() {
        return sActivity;
    }

    public static WifiManager getWifiManager() {
        return mWifiManager;
    }

    public static PreferenceManager getPreferenceManager() {
        return mPreferenceManager;
    }

    public static OkHttpClient getHttpClient() {
        if (!mWifiManager.isWifiEnabled()
                && mPreferenceManager.getBoolean(
                PreferenceManager.PREF_OTHER_CONNECT_ONLY_WIFI, false)) {
            return null;
        }
        if (mHttpClient == null) {
            try {
                @SuppressLint("CustomX509TrustManager") final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                            }

                            @SuppressLint("TrustAllX509TrustManager")
                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, trustAllCerts, new SecureRandom());
                mHttpClient = new OkHttpClient().newBuilder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .retryOnConnectionFailure(true)
                        .hostnameVerifier((s, sslSession) -> true)
                        .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                        .build();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                mHttpClient = new OkHttpClient().newBuilder()
                        .followRedirects(true)
                        .followSslRedirects(true)
                        .retryOnConnectionFailure(true)
                        .build();
            }
        }
        return mHttpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mActivityLifecycle = new ActivityLifecycle();
        registerActivityLifecycleCallbacks(mActivityLifecycle);
        mPreferenceManager = new PreferenceManager(this);
        BoxStore boxStore = MyObjectBox.builder()
                .androidContext(getApplicationContext())
                .name("cimoc2.db")
                .build();
        mDaoSession = new DaoSession(boxStore);
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        UpdateHelper.update(mPreferenceManager, getDaoSession());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setNetworkFetcher(new OkHttpNetworkFetcher(getHttpClient()))
                .setDownsampleMode(DownsampleMode.AUTO)
                .build();
        Fresco.initialize(this, config);
        initPixels();

        //获取栈顶Activity以及当前App上下文
        mApp = this;
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
                sActivity = activity;
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        StringBuilder sb = new StringBuilder();
        sb.append("MODEL: ").append(Build.MODEL).append('\n');
        sb.append("SDK: ").append(Build.VERSION.SDK_INT).append('\n');
        sb.append("RELEASE: ").append(Build.VERSION.RELEASE).append('\n');
        sb.append('\n').append(e.getLocalizedMessage()).append('\n');
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append('\n');
            sb.append(element.toString());
        }
        try {
            DocumentFile doc = getDocumentFile();
            DocumentFile dir = DocumentUtils.getOrCreateSubDirectory(doc, "log");
            DocumentFile file = DocumentUtils.getOrCreateFile(dir, StringUtils.getDateStringWithSuffix("log"));
            DocumentUtils.writeStringToFile(getContentResolver(), file, sb.toString());
        } catch (Exception ex) {
        }
        mActivityLifecycle.clear();
        System.exit(1);
    }

    @Override
    public App getAppInstance() {
        return this;
    }

    private void initPixels() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        mWidthPixels = metrics.widthPixels;
        mHeightPixels = metrics.heightPixels;
        mCoverWidthPixels = mWidthPixels / 3;
        mCoverHeightPixels = mHeightPixels * mCoverWidthPixels / mWidthPixels;
        mLargePixels = 3 * metrics.widthPixels * metrics.heightPixels;
    }

    public void initRootDocumentFile() {
        String uri = mPreferenceManager.getString(PreferenceManager.PREF_OTHER_STORAGE);
        mDocumentFile = Storage.initRoot(this, uri);
    }

    public DocumentFile getDocumentFile() {
        if (mDocumentFile == null) {
            initRootDocumentFile();
        }
        return mDocumentFile;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public RecyclerView.RecycledViewPool getGridRecycledPool() {
        if (mRecycledPool == null) {
            mRecycledPool = new RecyclerView.RecycledViewPool();
            mRecycledPool.setMaxRecycledViews(GridAdapter.TYPE_GRID, 20);
        }
        return mRecycledPool;
    }

    public ControllerBuilderProvider getBuilderProvider() {
        if (mBuilderProvider == null) {
            mBuilderProvider = new ControllerBuilderProvider(getApplicationContext(),
                    SourceManager.getInstance(this).new HeaderGetter(), true);
        }
        return mBuilderProvider;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
