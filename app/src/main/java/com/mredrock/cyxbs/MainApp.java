package com.mredrock.cyxbs;

import android.content.Context;
import android.os.Build;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.util.Log;

import com.redrock.common.BaseApp;
import com.redrock.common.account.AccountManager;
import com.redrock.common.config.Const;
import com.mredrock.cyxbs.model.Course;
import com.redrock.common.account.User;
import com.mredrock.cyxbs.network.RequestManager;
import com.mredrock.cyxbs.ui.activity.exception.ExceptionActivity;
import com.mredrock.cyxbs.util.LogUtils;
import com.redrock.common.utils.SPUtils;
import com.mredrock.cyxbs.util.Utils;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import org.android.agoo.huawei.HuaWeiRegister;
import org.android.agoo.xiaomi.MiPushRegistar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.redrock.common.account.AccountManager.isLogin;


/**
 * Created by cc on 16/3/18.
 */
public class MainApp extends BaseApp {
    private static Context context;

    public static final String TAG = "myAPP";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        context = base;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Config.DEBUG = BuildConfig.DEBUG;
        initPush();
        UMShareAPI.get(this);
        initShareKey();
        initThemeMode();
        //  FIR.init(this);
        ExceptionActivity.install(getApplicationContext(), true);
        // Refresh Course List When Start
        reloadCourseList();
        disableFileUriExposedException();
        initBugly();
    }

    private void initPush() {
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "123b419248120b9fb91a38260a13e972");
        UMConfigure.setLogEnabled(true);
        PushAgent mPushAgent = PushAgent.getInstance(this);
        MiPushRegistar.register(this,"2882303761517258683","5341725868683");
        HuaWeiRegister.register(this);
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                Log.d(TAG, "友盟注册成功: " + deviceToken);
            }
            @Override
            public void onFailure(String s, String s1) {
                Log.e(TAG, "onFailure: 友盟注册失败" + s + s1);
            }
        });
    }

    private void initShareKey() {
        PlatformConfig.setSinaWeibo("197363903", "7700116c567ab2bb28ffec2dcf67851d", "http://hongyan.cqupt.edu.cn/app/");
        PlatformConfig.setQQZone("1106072365", "v9w1F3OSDhkX14gA");
    }

    public void reloadCourseList() {
        if (isLogin()) {
            User user = AccountManager.getUser();
            RequestManager.getInstance().getCourseList(new Observer<List<Course>>() {
                @Override
                public void onComplete() {
                }

                @Override
                public void onError(Throwable e) {
                    Log.e("CSET", "reloadCourseList", e);
                }

                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(List<Course> courses) {
                }
            }, user.stuNum, user.idNum, 0, true);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    private void initThemeMode() {
        boolean isNight = (boolean) SPUtils.get(this, Const.SP_KEY_IS_NIGHT, false);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void disableFileUriExposedException() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Method disableDeathOnFileUriExposure = StrictMode.class.getDeclaredMethod("disableDeathOnFileUriExposure");
                disableDeathOnFileUriExposure.setAccessible(true);
                disableDeathOnFileUriExposure.invoke(null);
            } catch (Exception e) {
                LogUtils.LOGE("FileUriExposure", "Can't disable death on file uri exposure", e);
            }
        }
    }

    private void initBugly() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setAppVersion(Utils.getAppVersionName(context));      //App的版本

        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = getProcessName(android.os.Process.myPid());

        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        CrashReport.initCrashReport(getApplicationContext(), BuildConfig.BUGLY_APP_ID, BuildConfig.DEBUG, strategy);
    }

    @Nullable
    private static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

}
