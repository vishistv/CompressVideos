package com.vishistv.compressvideo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    private var mIsPaused = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val intent = intent
        val path = intent.getStringExtra("path")
        videoView.setVideoPath(path)
        videoView.start()
    }

    fun onPausePlayClick(view: View) {
        if (mIsPaused) {
            playVideo()
            btnPausePlay.setImageResource(R.drawable.ic_pause_circle_outline_green_24dp)
            mIsPaused = false
        } else {
            pauseVideo()
            btnPausePlay.setImageResource(R.drawable.ic_play_circle_outline_green_24dp)
            mIsPaused = true
        }
    }

    private fun pauseVideo() {
        if (videoView.isPlaying) {
            videoView.pause()
        }
    }

    private fun playVideo() {
        if (!videoView.isPlaying) {
            videoView.start()
        }
    }


}
