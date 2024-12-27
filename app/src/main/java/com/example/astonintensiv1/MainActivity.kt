package com.example.astonintensiv1

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import com.example.astonintensiv1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_IS_PLAYING = "isPlaying"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var musicService: MusicService
    private var isBound = false
    private var isPlaying = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MyBinder
            musicService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            isPlaying = savedInstanceState.getBoolean(KEY_IS_PLAYING, false)
            if (isPlaying) {
                binding.togglePlaybackBtn.setImageResource(R.drawable.ic_pause_24)
            } else {
                binding.togglePlaybackBtn.setImageResource(R.drawable.ic_play_24)
            }
        }

        val musicServiceIntent = Intent(this, MusicService::class.java)
        startService(musicServiceIntent)
        bindService(musicServiceIntent, connection, Context.BIND_AUTO_CREATE)

        binding.togglePlaybackBtn.setOnClickListener {
            if (isBound) {
                if (isPlaying) {
                    musicService.pauseMusic()
                    binding.togglePlaybackBtn.setImageResource(R.drawable.ic_play_24)
                } else {
                    musicService.playMusic()
                    binding.togglePlaybackBtn.setImageResource(R.drawable.ic_pause_24)
                }
                isPlaying = !isPlaying
            }
        }

        binding.nextImgBtn.setOnClickListener {
            if (isBound) {
                musicService.nextSong()
                if (!isPlaying) {
                    musicService.playMusic()
                    binding.togglePlaybackBtn.setImageResource(R.drawable.ic_pause_24)
                    isPlaying = true
                }
            }
        }

        binding.previousImgBtn.setOnClickListener {
            if (isBound) {
                musicService.previousSong()
                if (!isPlaying) {
                    musicService.playMusic()
                    binding.togglePlaybackBtn.setImageResource(R.drawable.ic_pause_24)
                    isPlaying = true
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_PLAYING, isPlaying)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
    }
}

