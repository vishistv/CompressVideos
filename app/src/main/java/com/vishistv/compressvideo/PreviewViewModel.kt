package com.vishistv.compressvideo

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PreviewViewModel(application: Application) : AndroidViewModel(application) {

    private val _mOutputPath = MutableLiveData("")
    private val _mIsCompressing = MutableLiveData(false)
    val mOutputPath: LiveData<String> = _mOutputPath
    val mIsCompressing: LiveData<Boolean> = _mIsCompressing

    fun startCompression(dir: File, bitRate: String, path: String?) {
        _mIsCompressing.postValue(true)
        viewModelScope.launch {
            compress(dir, bitRate, path)
        }
    }

    private suspend fun compress(dir: File, bitRate: String, path: String?) {
        val isFolderCreated = createFolder(dir)

        if (isFolderCreated) {
            val outputPath =
                "$dir/CompressedVideos/${SimpleDateFormat("yyyyMMddHHmmss").format(Date())}.mp4"

            val cmd = arrayOf("-i", path, "-b", bitRate, "-y", outputPath)

            val ffmpeg = FFmpeg.getInstance(getApplication())
            try {
                ffmpeg?.execute(cmd, object : ExecuteBinaryResponseHandler() {
                    override fun onStart() {}
                    override fun onProgress(message: String) {
                        Log.d(PreviewActivity.TAG, "progress: $message")
                    }
                    override fun onFailure(message: String) {
                        Log.d(PreviewActivity.TAG, "failed: $message")
                        _mIsCompressing.postValue(false)
                    }

                    override fun onSuccess(message: String) {
                        Log.d(PreviewActivity.TAG, "success: $message")
                        _mOutputPath.postValue(outputPath)
                        _mIsCompressing.postValue(false)
                    }

                    override fun onFinish() {
                        _mIsCompressing.postValue(false)
                    }
                })
            } catch (e: FFmpegCommandAlreadyRunningException) {
            }
        } else {
            _mIsCompressing.postValue(false)
        }
    }

    private suspend fun createFolder(dir: File): Boolean {
        return withContext(Dispatchers.IO) {
            val folder = "/CompressedVideos"
            val documentsFolder = File(dir, folder)
            if (!documentsFolder.exists())
                return@withContext documentsFolder.mkdirs()
            else
                return@withContext true
        }
    }

}