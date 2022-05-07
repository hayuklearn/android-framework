package com.af.demo.jetpack.lifecycle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import com.af.demo.jetpack.R
import com.mod.lifecycle.BaseLifecycleFragment

/**
 * Created by hayukleung@gmail.com on 2021-09-09.
 */
class TryLifecycleFragment : BaseLifecycleFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_lifecycle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        view.findViewById<AppCompatButton>(R.id.button_hide_fragment).setOnClickListener {

           parentFragmentManager.beginTransaction().remove(this).commit()
        }
    }
}
