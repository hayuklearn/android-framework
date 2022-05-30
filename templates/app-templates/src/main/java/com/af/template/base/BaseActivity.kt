package com.af.template.base

import android.os.Bundle
import android.util.Log
import android.view.View
import com.af.lib.ktext.getScreenHeight
import com.af.template.databinding.TemplateActivityBaseBinding
import com.mod.lifecycle.BaseLifecycleActivity

/**
 * =================================================================================================
 *     __
 *    / /  ___    __  __  ____ _  ____    __  __  ____ _  ____
 *   / /  / _ \  / / / / / __ `/ / __ \  / / / / / __ `/ / __ \
 *  / /  /  __/ / /_/ / / /_/ / / /_/ / / /_/ / / /_/ / / /_/ /
 * /_/   \___/  \__, /  \__,_/  \____/  \__, /  \__,_/  \____/
 *             /____/                  /____/
 * =================================================================================================
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2022-05-19
 */
open class BaseActivity : BaseLifecycleActivity(), ILoading {

    private val binding by lazy { TemplateActivityBaseBinding.inflate(layoutInflater) }

    private val loadingDelegate by lazy { LoadingDelegate(this.provideLoadingable()) }

    private val blockClickListener by lazy {
        View.OnClickListener {
            Log.d("block", "拦截业务界面的点击事件")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(binding.root)
        this.binding.loading.setProgressViewEndTarget(
            true,
            getScreenHeight() / 10 * 4
        )
        this.binding.block.setOnClickListener(blockClickListener)
        this.binding.block.isClickable = false
    }

    override fun setContentView(view: View?) {

        this.binding.container.removeAllViews()
        view?.let {
            this.binding.container.addView(it)
        }
    }

    override fun provideLoadingable() = this.binding.loading

    override fun showLoading(block: Boolean) {

        this.binding.block.isClickable = block
        this.loadingDelegate.showLoading()
    }

    override fun hideLoading() {

        this.loadingDelegate.hideLoading()
        this.binding.block.isClickable = false
    }

    override fun isLoading() = this.loadingDelegate.isLoading()
}