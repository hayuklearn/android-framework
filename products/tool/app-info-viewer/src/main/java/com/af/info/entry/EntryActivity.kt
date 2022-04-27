package com.af.info.entry

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.af.info.ApkTool
import com.af.info.app.InstalledAppListActivity
import com.af.info.databinding.ActivityEntryBinding
import com.af.info.service.RunningServiceListActivity

/**
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-11-18
 */
class EntryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEntryBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.entryAppList.setOnClickListener {

            val intent = Intent(this@EntryActivity, InstalledAppListActivity::class.java)
            startActivity(intent)
        }
        binding.entryServiceList.setOnClickListener {

            // ApkTool.exec("dumpsys activity services | grep ServiceRecord | awk '{print \$4}' | sed 's/}//1g'")
            val intent = Intent(this@EntryActivity, RunningServiceListActivity::class.java)
            startActivity(intent)
        }
    }
}
