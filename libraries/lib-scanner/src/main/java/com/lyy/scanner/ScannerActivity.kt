package com.lyy.scanner

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lyy.scanner.pda.PDAScannerActivity
import com.lyy.scanner.phone.PhoneScannerActivity

/**
 *
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-09-30
 */
class ScannerActivity : AppCompatActivity() {

    companion object {
        const val PDA_ONLY_CLASS = "com.android.interfaces.scanner.sdk.server.IScannerService"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isClassExists(PDA_ONLY_CLASS)) {
            // PDA
            val intent = Intent(this, PDAScannerActivity::class.java)
            startActivity(intent)
        } else {
            // Phone
            val intent = Intent(this, PhoneScannerActivity::class.java)
            startActivity(intent)
        }
        finish()
    }

    private fun isClassExists(className: String): Boolean {
        return try {
            Class.forName(className)
            true
        } catch (exception: ClassNotFoundException) {
            false
        }
    }
}
