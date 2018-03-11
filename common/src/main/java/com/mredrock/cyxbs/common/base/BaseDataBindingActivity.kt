package com.mredrock.cyxbs.common.base

import android.app.Activity
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.mredrock.cyxbs.common.BR
import com.mredrock.cyxbs.common.R
import com.mredrock.cyxbs.common.util.ViewUtil
import java.lang.Exception
import java.lang.ref.WeakReference
import java.lang.reflect.ParameterizedType

/**
 * Created by ：AceMurder
 * Created on ：2017/11/13
 * Created for : LightMusic.
 * Enjoy it !!!
 */
abstract class BaseDataBindingActivity<P : BasePresenter, T : ViewDataBinding> : AppCompatActivity(), IView, IToolbar {
    protected lateinit var binding: T
    protected lateinit var presenter: P
    protected lateinit var activity: Activity
    protected lateinit var rootView: ViewGroup
    protected lateinit var contentView: ViewGroup
    protected val TAG = javaClass.simpleName
    protected lateinit var mHandler: BaseHandler
    protected lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        activity = this
        rootView = findViewById(R.id.root_view)
        contentView = findViewById(R.id.container)
        setContentView(createContentView(layoutInflater, contentView, savedInstanceState))
        initToolbar()
        initDataBinding()
        initBasePresenter()
        initStatusBarColor()
        viewCreated(savedInstanceState)
    }

    fun initToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    fun initStatusBarColor() {
        try {
            ViewUtil.setWindowStatusBarColor(this, R.color.colorPrimary);
        } catch (e: Exception) {

        }
    }

    fun initDataBinding() {
        binding = DataBindingUtil.bind(contentView.getChildAt(0))
    }

    fun initBasePresenter() {
        val p: P? = createPresenter()
        p?.let {
            presenter = p
            binding.setVariable(BR.presenter, presenter)

        }
    }

    override fun <P : BasePresenter> createPresenter(): P? {
        var presenterClass = getPresenterClass()
        presenterClass?.let {
            try {
                var constructor = presenterClass.getConstructor(FragmentActivity::class.java)
                if (constructor.isAccessible)
                    constructor.isAccessible = true
                return constructor.newInstance(this) as P

            } catch (e: Exception) {

            }
        }
        return null
    }


    private fun getPresenterClass(): Class<P>? {
        val genericSuperclass = javaClass.genericSuperclass
        if (genericSuperclass is ParameterizedType) {
            if (genericSuperclass.actualTypeArguments.size == 2) {
                val types = genericSuperclass.actualTypeArguments
                return types[0] as Class<P>
            }
        }
        return null
    }


    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutRes = getLayoutRes()
        if (layoutRes != 0) {
            Log.d(TAG, "createContentView: ")
            return inflater.inflate(layoutRes, container, false)
        }
        return null
    }


    override fun setContentView(view: View?) {
        contentView.removeAllViews()
        contentView.addView(view)
    }

    override fun setContentView(view: View?, params: ViewGroup.LayoutParams?) {
        contentView.removeAllViews()
        contentView.addView(view, params)
    }

    private fun getPresentClass(): Class<*>? {
        val genericSuperclass = javaClass.genericSuperclass
        if (genericSuperclass is ParameterizedType) {
            if (genericSuperclass.actualTypeArguments.size == 2) {
                val types = genericSuperclass.actualTypeArguments
                return types[0] as Class<P>
            }
        }
        return null
    }

    protected fun getHandler(): BaseHandler {
        if (mHandler == null) {
            mHandler = BaseHandler(this, getHandlerCallBack())
        }
        return mHandler
    }

    private fun getHandlerCallBack(): BaseHandler.HanlderOperate {
        return object : BaseHandler.HanlderOperate {
            override fun handleMessage(message: Message) {
                progressHandler(message)
            }
        }
    }

    protected fun progressHandler(message: Message) {

    }

    override fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    override fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }

    override fun setTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun setTitle(titleId: Int) {
        supportActionBar?.setTitle(titleId)
    }





    protected class BaseHandler() : Handler() {

        private lateinit var activity: WeakReference<Context>
        private lateinit var handlerOperate: HanlderOperate

        constructor(activity: Context, operate: HanlderOperate) : this() {
            this.activity = WeakReference(activity)
            this.handlerOperate = operate


        }

        override fun handleMessage(msg: Message?) {

        }

        interface HanlderOperate {
            fun handleMessage(message: Message)
        }

    }

}