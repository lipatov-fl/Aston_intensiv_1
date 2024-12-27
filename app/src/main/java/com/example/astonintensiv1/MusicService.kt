package com.example.astonintensiv1

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import java.io.IOException

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var currentSongIndex = 0
    private val songs = arrayOf(R.raw.music_1, R.raw.music_2, R.raw.music_3)
    private val binder = MyBinder()

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener {
            nextSong()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                "PLAY" -> playMusic()
                "PAUSE" -> pauseMusic()
                "NEXT" -> nextSong()
                "PREVIOUS" -> previousSong()
            }
        }
        return START_STICKY
    }

    private fun showNotification() {
        val playPauseAction = if (mediaPlayer.isPlaying) {
            NotificationCompat.Action(
                R.drawable.ic_pause_24,
                "Pause",
                PendingIntent.getService(
                    this,
                    0,
                    Intent(this, MusicService::class.java).apply { action = "PAUSE" },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        } else {
            NotificationCompat.Action(
                R.drawable.ic_play_24,
                "Play",
                PendingIntent.getService(
                    this,
                    0,
                    Intent(this, MusicService::class.java).apply { action = "PLAY" },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
        }

        val notification = NotificationCompat.Builder(this, "music_service_channel")
            .setContentTitle("Music Player")
            .setContentText("Playing: ${songs[currentSongIndex]}")
            .setSmallIcon(R.drawable.ic_music_note_24)
            .addAction(playPauseAction)
            .addAction(
                R.drawable.ic_next_24,
                "Next",
                PendingIntent.getService(
                    this,
                    0,
                    Intent(this, MusicService::class.java).apply { action = "NEXT" },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .addAction(
                R.drawable.ic_previous_24,
                "Previous",
                PendingIntent.getService(
                    this,
                    0,
                    Intent(this, MusicService::class.java).apply { action = "PREVIOUS" },
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)
    }

    fun playMusic() {
        if (!::mediaPlayer.isInitialized) {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setOnCompletionListener {
                nextSong()
            }
        }
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(
                applicationContext,
                Uri.parse("android.resource://$packageName/${songs[currentSongIndex]}")
            )
            mediaPlayer.prepare()
            mediaPlayer.start()
            showNotification()
        } catch (e: IOException) {
            Log.e(
                "com.example.astonishments1.MusicService",
                "Error preparing MediaPlayer: ${e.message}"
            )
        }
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            showNotification()
        }
    }

    fun nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songs.size
        playMusic()
    }

    fun previousSong() {
        currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
        playMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class MyBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }
}