package com.lyy.scanner.phone

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * 摄像头灯光开关
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-11-04
 */
class LightViewModel : ViewModel() {

    private val onInner = MutableLiveData(false)
    val on: LiveData<Boolean> = onInner

    fun toggle() {
        onInner.value?.let {
            onInner.value = !it
        }
    }

    fun off() {

        onInner.value = false
    }
}
