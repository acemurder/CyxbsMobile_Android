package com.mredrock.cyxbs.ui.activity.qa

import android.os.Bundle
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*

class PointSpecActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_spec)

        toolbar.title = ""
        toolbar_title.text = "积分说明"
        toolbar.setNavigationOnClickListener { finish() }
    }
}
