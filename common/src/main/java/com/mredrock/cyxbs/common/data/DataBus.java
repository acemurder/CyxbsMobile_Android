package com.mredrock.cyxbs.common.data;

import com.mredrock.cyxbs.common.model.User;

/**
 * Created by ：AceMurder
 * Created on ：2018/1/24
 * Created for : CyxbsMobile_Android.
 * Enjoy it !!!
 */

public class DataBus {
    private static DataProxy dataProxy;

    public static boolean isDebug(){
        return dataProxy.isDebug();
    }

    public static boolean isLogin(){
        return dataProxy.isLogin();
    }

    public static User getUser(){
        return dataProxy.getUser();
    }

}
