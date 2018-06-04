package com.mredrock.cyxbs.ui.fragment.help

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import com.mredrock.cyxbs.R
import kotlinx.android.synthetic.main.dialog_select_help_tag.*
import org.jetbrains.anko.sdk25.coroutines.onCheckedChange
import org.jetbrains.anko.sdk25.coroutines.onClick

class SelectTagDialog(context: Context?, themeResId: Int, var tag: String = "", val listener: SelectDialogListener) : Dialog(context, themeResId) {
    private val TAG = "SelectTagDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_help_tag)
        initData()
        init()
    }

    private fun initData() {

    }

    private fun init() {

        btn_next.onClick {
            listener.onNext(tag)
            cancel()
        }

        if (tag != "") {
            img_search.visibility = View.GONE
            edt_tag.setText("#${tag}#")
            group_tag.setCheckedRadioButton(tag)
        } else {
            img_search.visibility = View.VISIBLE
        }

        edt_tag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                val text = edt_tag.text.toString().replace("#", "")
                if (group_tag.checkedRadioButtonIndex != -1 && text != group_tag.checkedRadioButtonText) {
                    group_tag.clearCheck()
                }

                tag = text
                listener.onChange(tag)

                if (tag != "") {
                    img_search.visibility = View.GONE
                } else {
                    img_search.visibility = View.VISIBLE
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        group_tag.onCheckedChange { group, checkedId ->
            run {
                img_search.visibility = View.GONE
                if (group_tag.checkedRadioButtonText != "") {
                    tag = group_tag.checkedRadioButtonText
                    edt_tag.setText("#${tag}#")
                }
            }
        }
    }

}