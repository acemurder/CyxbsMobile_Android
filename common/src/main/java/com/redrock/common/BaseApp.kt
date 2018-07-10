package com.redrock.common

import android.content.Context
import android.support.multidex.MultiDexApplication

open class BaseApp : MultiDexApplication() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ContextProvider.attachApplication(base)
    }
}
