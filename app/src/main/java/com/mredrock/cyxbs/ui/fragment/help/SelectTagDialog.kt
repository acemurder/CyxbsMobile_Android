package com.mredrock.cyxbs.ui.fragment.help

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.RadioGroup
import com.mredrock.cyxbs.R
import com.mredrock.cyxbs.util.DialogUtil
import com.mredrock.cyxbs.util.Utils
import kotlinx.android.synthetic.main.dialog_select_help_tag.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.textChangedListener

class SelectTagDialog(context: Context?, themeResId: Int, val listener: SelectTagListener, var tag: String = "") : Dialog(context, themeResId), CompoundButton.OnCheckedChangeListener {
    private val TAG = "SelectTagDialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_select_help_tag)
        init()
    }

    private fun init() {
        btn_tag_1.setOnCheckedChangeListener(this)
        btn_tag_2.setOnCheckedChangeListener(this)
        btn_tag_3.setOnCheckedChangeListener(this)
        btn_tag_4.setOnCheckedChangeListener(this)
        btn_tag_5.setOnCheckedChangeListener(this)
        btn_tag_6.setOnCheckedChangeListener(this)

        btn_next.onClick {
            cancel()
        }

        if (tag != "") {
            img_search.visibility = View.GONE
            edt_tag.setText("#${tag}#")

        } else {
            img_search.visibility = View.VISIBLE
        }

        edt_tag.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                group_tag_1.clearCheck()
                group_tag_2.clearCheck()

                tag = edt_tag.text.toString().replace("#", "")

                listener.changed(tag)

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
    }


    override fun onCheckedChanged(buttonView : CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.btn_tag_1 -> {
                group_tag_2.clearCheck()
            }
            R.id.btn_tag_2 -> {
                group_tag_2.clearCheck()
            }
            R.id.btn_tag_3 -> {
                group_tag_2.clearCheck()
            }
            R.id.btn_tag_4 -> {
                group_tag_2.clearCheck()
            }
            R.id.btn_tag_5 -> {
                group_tag_1.clearCheck()
            }
            R.id.btn_tag_6 -> {
                group_tag_1.clearCheck()
            }
        }
        if (isChecked) {
            tag = buttonView.text.toString()
            edt_tag.setText("#${tag}#")
        }

    }


    interface SelectTagListener {
        fun changed(tag: String)
    }
}