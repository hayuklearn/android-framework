package com.af.demo.business.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.af.demo.business.R
import com.mod.lifecycle.BaseLifecycleFragment

class MineFragment : BaseLifecycleFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_main_mine, container, false)

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        Log.d(TAG, "onHiddenChanged, hidden = $hidden")
    }

    companion object {

        private const val TAG = "mine"
    }
}
