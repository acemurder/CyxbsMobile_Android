package com.mredrock.cyxbs.common.util

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.Base64
import android.view.View
import android.view.WindowManager
import android.widget.ListView

/**
 * Created by ：AceMurder
 * Created on ：2017/11/13
 * Created for : LightMusic.
 * Enjoy it !!!
 */
object ViewUtil{
    fun show(view: View?) {
        if (view != null) {
            view.visibility = View.VISIBLE
        }
    }

    //改这段代码的时候，只需要同时按下option + space
    fun isViewVisible(view: View?): Boolean {
        return view != null && view.visibility == View.VISIBLE
    }

    fun hide(view: View?) {
        if (view != null) {
            view.visibility = View.GONE
        }
    }

    fun setViewVisibleOrGone(view: View?, visible: Boolean) {
        if (view != null) {
            view.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    fun setVisibleOrInvisible(view: View?, visible: Boolean) {
        if (view != null) {
            view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        }
    }

    fun setViewInvisible(view: View?) {
        if (view != null) {
            view.visibility = View.INVISIBLE
        }
    }

    fun inverseVisibleOrGone(view: View) {
        setViewVisibleOrGone(view, !isViewVisible(view))
    }

    fun setListViewHeightBasedOnChildren(listView: ListView?) {
        if (listView == null) return
        val listAdapter = listView.adapter ?: // pre-condition
                return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
    }

    fun setGradientDrawable(view: View?, color: String, radius: Float) {
        val drawable = GradientDrawable()
        try {
            drawable.setColor(Color.parseColor(color))
            drawable.shape = GradientDrawable.RECTANGLE
            //            drawable.setCornerRadius(radius);
        } catch (e: IllegalArgumentException) {
            //DebugLogger.d("ViewUtils  setGradientDrawable: " + e.message)
        }

        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = drawable
            } else {
                view.setBackgroundDrawable(drawable)
            }
        }
    }

    fun setCornorDrawable(view: View?, color: String, radius: Float) {
        val drawable = GradientDrawable()
        try {
            drawable.setColor(Color.parseColor(color))
            drawable.shape = GradientDrawable.RECTANGLE
            if (radius > 0) {
                drawable.cornerRadius = radius
            }
        } catch (e: IllegalArgumentException) {
           // DebugLogger.d("ViewUtils  setGradientDrawable: " + e.message)
        }

        if (view != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.background = drawable
            } else {
                view.setBackgroundDrawable(drawable)
            }
        }
    }

    fun getBase64Bitmap(bitmapString: String?): Bitmap? {
        var bitmap: Bitmap? = null
        if (bitmapString != null) {
            try {
                val value = bitmapString.substring(bitmapString.indexOf(",") + 1, bitmapString.length)
                bitmap = getBitmapFromBase64(value)
            } catch (e: Exception) {
               // ToastUtil.showToastShort(R.string.error_request_captcha)
               //   DebugLogger.d("getVerificationCodeBitmap: " + e.message)
            }

        }
        return bitmap
    }

    fun getBitmapFromBase64(base64Data: String): Bitmap {
        val bytes = Base64.decode(base64Data, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }


    fun setWindowStatusBarColor(activity: Activity, colorResId: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = activity.resources.getColor(colorResId)

                //底部导航栏
                //window.setNavigationBarColor(activity.getResources().getColor(colorResId));
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}