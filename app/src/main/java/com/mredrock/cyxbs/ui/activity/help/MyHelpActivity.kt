package com.mredrock.cyxbs.ui.activity.help

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.ui.activity.BaseActivity

class MyHelpActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context) {
            context.startActivity(Intent(context, MyHelpActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_help)
    }
}
