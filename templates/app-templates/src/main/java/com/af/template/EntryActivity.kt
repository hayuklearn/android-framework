package com.af.template

import android.os.Bundle
import com.af.template.base.BaseActivity
import com.af.template.databinding.TemplateActivityEntryBinding

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
class EntryActivity : BaseActivity() {

    private val binding by lazy { TemplateActivityEntryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.showLoading.setOnClickListener {
            showLoading(false)
        }
        binding.hideLoading.setOnClickListener {
            hideLoading()
        }
    }
}