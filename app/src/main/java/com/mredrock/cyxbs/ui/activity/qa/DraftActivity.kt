package com.mredrock.cyxbs.ui.activity.qa

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.ui.activity.BaseActivity
import kotlinx.android.synthetic.main.toolbar.*

class DraftActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draft)
        initToolBar()
        initRv()
    }

    private fun initToolBar() {
        toolbar.title = ""
        toolbar_title.text = "草稿箱"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initRv() {

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_draft, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?) = when (item?.itemId) {
        R.id.edit -> {
            //todo edit menu
            true
        }
        else -> false
    }
}
