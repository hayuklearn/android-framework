package com.af.info.service

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.af.info.ApkTool
import com.af.info.databinding.ActivityRunningServiceListBinding

/**
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-11-18
 */
class RunningServiceListActivity: AppCompatActivity() {

    private val binding by lazy { ActivityRunningServiceListBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.runningServiceList.text = ApkTool.exec("dumpsys activity services | grep ServiceRecord | awk '{print \$4}' | sed 's/}//1g'")
    }
}
