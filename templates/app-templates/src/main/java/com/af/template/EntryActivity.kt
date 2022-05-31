package com.af.template

import android.os.Bundle
import android.util.Log
import com.af.lib.ktext.toast
import com.af.lib.pinyin.Pinyin
import com.af.lib.utils.UUIDCompat
import com.af.template.base.BaseActivity
import com.af.template.databinding.TemplateActivityEntryBinding
import com.lyy.database.SecurityDatabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        binding.pinyin.setOnClickListener {
            val city = "广州"
            toast(Pinyin.toPinyin(city, " "))
        }
        binding.uuid.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("uuid", UUIDCompat.getUUID(this@EntryActivity) ?: "")
            }
        }
        binding.database.setOnClickListener {
            val item = Item("0", "数据0", "我是数据0")
            SecurityDatabaseHelper.saveOrUpdate(item, item.id)
            val list = SecurityDatabaseHelper.query<Item>()
            list.forEach {
                println(it)
            }
        }
        binding.log.setOnClickListener {
        }
    }


}