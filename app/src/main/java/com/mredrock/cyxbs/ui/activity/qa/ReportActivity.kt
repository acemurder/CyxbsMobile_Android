package com.mredrock.cyxbs.ui.activity.qa

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.component.widget.selector.MultiSelector
import com.mredrock.cyxbs.component.widget.selector.ViewInitializer
import com.mredrock.cyxbs.ui.activity.BaseActivity
import com.mredrock.cyxbs.ui.adapter.qa.ReportTypeSelectorViewWrapper
import com.mredrock.cyxbs.util.DensityUtils
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.item_report_selector.view.*
import kotlinx.android.synthetic.main.toolbar.*
import org.jetbrains.anko.startActivity

class ReportActivity : BaseActivity() {

    companion object {
        const val MAX_COUNT = 200

        @JvmStatic
        fun start(context: Context, qid: String) {
            context.startActivity<ReportActivity>("qid" to qid)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        initToolbar()
        initSelector()
        initEditor()
        submit.setOnClickListener { submit() }
    }

    private fun initToolbar() {
        toolbar.title = ""
        toolbar_title.text = "举报"
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initSelector() {
        var spanCount = DensityUtils.getScreenWidth(this) / DensityUtils.dp2px(this, 106f)
        if (spanCount > 3) spanCount = 3
        val layoutManager = GridLayoutManager(this, spanCount)
        val initializer = ViewInitializer.Builder(this)
                .layoutManager(layoutManager)
                .adapter(object : MultiSelector.Adapter<String, ReportTypeSelectorViewWrapper>(selector) {
                    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportTypeSelectorViewWrapper {
                        val layoutInflater = LayoutInflater.from(this@ReportActivity)
                        val container = layoutInflater.inflate(R.layout.item_report_selector, parent, false)
                        return ReportTypeSelectorViewWrapper(container)
                    }

                    override fun bindView(holder: ReportTypeSelectorViewWrapper, displayValue: String, selected: Boolean, position: Int) {
                        holder.setChecked(selected)
                        holder.setValue(displayValue)
                        holder.itemView.radioButton.setOnClickListener { selector.setSelected(position, true) }
                    }
                }).build()
        selector.setViewInitializer(initializer)
        selector.setSelected(0, true)
    }

    private fun initEditor() {
        counter.text = "0/$MAX_COUNT"
        content.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                counter.text = "${s.length}/$MAX_COUNT"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

        })
    }

    private fun submit() {
        //todo submit
    }
}
