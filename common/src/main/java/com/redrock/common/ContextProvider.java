package com.redrock.common;

import android.content.Context;

public class ContextProvider {

    private static Context context;

    public  static Context getContext() {
        return context;
    }

    public static void attachApplication(Context context) {
        ContextProvider.context = context;
    }
}
