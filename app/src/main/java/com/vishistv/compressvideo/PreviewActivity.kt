package com.vishistv.compressvideo

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_preview.*
import kotlinx.android.synthetic.main.activity_preview.videoView
import kotlinx.android.synthetic.main.activity_result.*


class PreviewActivity : AppCompatActivity() {

    private var mPath: String? = null
    private var mViewModel: PreviewViewModel? = null
    private var mProgressDialog: ProgressDialog? = null

    companion object {
        const val TAG = "PreviewActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        mProgressDialog = ProgressDialog(this)
        mProgressDialog?.setMessage("Compressing, Please wait...")
        mProgressDialog?.setCancelable(false)

        mViewModel = ViewModelProvider(this).get(PreviewViewModel::class.java)

        val intent = intent
        mPath = intent.getStringExtra("path")

        videoView.setVideoPath(mPath)
        videoView.start()

        btn_compress.setOnClickListener {
            if (et_bitrate.text.isNotEmpty()) {
                mViewModel?.startCompression(this.filesDir, et_bitrate.text.toString(), mPath)
            }
        }

        mViewModel?.mOutputPath?.observe(this, Observer { path ->
            if (path != null && path.isNotEmpty()) {
                val i = Intent(this, ResultActivity::class.java)
                i.putExtra("path", path)
                startActivity(i)
            }
        })

        mViewModel?.mIsCompressing?.observe(this, Observer { isCompressing ->
            if (isCompressing) {
                mProgressDialog?.show()
            } else {
                mProgressDialog?.dismiss()
            }
        })


    }
}
