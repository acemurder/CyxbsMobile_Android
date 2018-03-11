package com.mredrock.cyxbs.common.base

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mredrock.cyxbs.common.BR
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType

/**
 * Created by ：AceMurder
 * Created on ：2017/11/13
 * Created for : LightMusic.
 * Enjoy it !!!
 */
abstract class BaseDataBindingFragment<P : BasePresenter, T : ViewDataBinding> : Fragment(), IView, IToolbar {
    protected lateinit var binding: T
    protected lateinit var presenter: P
    protected lateinit var iToolbar: IToolbar

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        context?.let {
            if (context is IToolbar)
                iToolbar = context
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createContentView(inflater, container, savedInstanceState)
    }

    override fun createContentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layoutRes = getLayoutRes()
        if (layoutRes == 0) {
            return super.onCreateView(inflater, container, savedInstanceState)
        } else {
            return inflater.inflate(layoutRes, container, false)
        }
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDataBinding()
        initBasePresenter()
        viewCreated(savedInstanceState)
    }

    fun initDataBinding() {
        binding = DataBindingUtil.bind(view)
    }

    fun initBasePresenter(){
        val p : P? = createPresenter()
        p?.let {
            presenter = p
            binding.setVariable(BR.presenter,presenter)
        }
    }


    override fun <P : BasePresenter> createPresenter(): P? {
        val presentClass = getPresentClass()
        if (presentClass != null) {
            try {
                val constructor = presentClass.getConstructor(FragmentActivity::class.java)
                return constructor.newInstance(activity) as P
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: java.lang.InstantiationException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }

        }
        return null
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

    override fun setTitle(resId: Int) {
        iToolbar.setTitle(getString(resId))
    }

    override fun setTitle(title: String) {
        iToolbar.setTitle(title)
    }

    override fun hideToolbar() {
        iToolbar.hideToolbar()
    }

    override fun showToolbar() {
        iToolbar.showToolbar()
    }
}

