package com.mredrock.cyxbs.common.data;

import android.content.Context;

import com.mredrock.cyxbs.common.model.User;

/**
 * Created by ：AceMurder
 * Created on ：2018/1/24
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public interface DataProxy {
    boolean isDebug();
    User getUser();
    boolean isLogin();
    Context getContext();
}
