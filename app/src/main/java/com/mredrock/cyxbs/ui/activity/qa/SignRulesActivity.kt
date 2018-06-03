package com.mredrock.cyxbs.ui.activity.qa

import android.os.Bundle
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*

class SignRulesActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_rules)

        toolbar.title = ""
        toolbar_title.text = "签到规则"
        toolbar.setNavigationOnClickListener { finish() }
    }
}
