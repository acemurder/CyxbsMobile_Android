package com.redrock.common;

import android.app.Application;
import android.content.Context;

public class BaseApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ContextProvider.attachApplication(base);
    }
}
