package com.lyy.scanner.pda

import android.util.Log
import com.android.decode.*
import com.lyy.scanner.IScannerLifecycle
import com.lyy.scanner.ScannerListener

/**
 * CHITENG C65 PDA 扫码代理
 *
 * @author liangxiaxu@leyaoyao.com
 * @date 2021-09-30
 */
class PDAScannerDelegate(private var scannerListener: ScannerListener) : IScannerLifecycle {

    companion object {

        const val TAG = "PDAScannerDelegate"
    }

    private var mBarcodeManager: BarcodeManager? = null
    private var mStartListener: StartListener? = null
    private var mStopListener: StopListener? = null
    private var mReadListener: ReadListener? = null
    private var mTimeoutListener: TimeoutListener? = null

    override fun onScannerResourceInit() {

        Log.d(TAG, "onScannerResourceInit")
        try {
            // FIXME
            val localBarcodeManager = BarcodeManager()
            localBarcodeManager.isInitialized
            mBarcodeManager = localBarcodeManager

            mStartListener = StartListener {

                onScanStart()
            }
            mStopListener = StopListener {

                onScanStop()
            }
            mReadListener = ReadListener { decodeResult ->

                scannerListener.onScanRead(
                    decodeResult.rawData,
                    decodeResult.text,
                    // decodeResult.barcodeID.toString()
                )
            }
            mTimeoutListener = TimeoutListener {

                onScanTimeout()
            }
            mBarcodeManager!!.addStartListener(mStartListener)
            mBarcodeManager!!.addStopListener(mStopListener)
            mBarcodeManager!!.addReadListener(mReadListener)
            mBarcodeManager!!.addTimeoutListener(mTimeoutListener)
        } catch (initException: Exception) {
            Log.e(TAG, initException.message ?: "onScannerResourceInit exception")
        }
    }

    override fun onScanStart() {
        Log.d(TAG, "onScanStart")
        scannerListener.onScanLoading(true)
    }

    override fun onScanStop() {
        Log.d(TAG, "onScanStop")
        scannerListener.onScanLoading(false)
    }

    override fun onScanTimeout() {
        Log.d(TAG, "onScanTimeout")
        scannerListener.onScanLoading(false)
    }

    override fun onScannerResourceFreeze() {
        try {
            if (mBarcodeManager != null) {
                mBarcodeManager!!.stopDecode()

                mBarcodeManager!!.removeStartListener(mStartListener)
                mBarcodeManager!!.removeStopListener(mStopListener)
                mBarcodeManager!!.removeReadListener(mReadListener)
                mBarcodeManager!!.removeTimeoutListener(mTimeoutListener)
                mBarcodeManager!!.release()
                mBarcodeManager = null
            }
            return
        } catch (releaseException: Exception) {
            Log.e(TAG, releaseException.message ?: "onScanRelease exception")
        } finally {
            Log.d(TAG, "onScannerResourceFreeze")
        }
    }

    override fun onScannerResourceRelease() {
        Log.d(TAG, "onScannerResourceRelease")
    }
}
