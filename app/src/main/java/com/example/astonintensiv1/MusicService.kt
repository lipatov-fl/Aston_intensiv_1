import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.IBinder
import android.util.Log
import com.example.astonintensiv1.R
import java.io.IOException

class MusicService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private var currentSongIndex = 0
    private val songs = arrayOf(R.raw.music_1, R.raw.music_2, R.raw.music_3)

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

    private fun playMusic() {
        try {
            if (!::mediaPlayer.isInitialized) {
                mediaPlayer = MediaPlayer()
                mediaPlayer.setOnCompletionListener {
                    nextSong()
                }
            }
            mediaPlayer.reset()
            mediaPlayer.setDataSource(applicationContext, Uri.parse("android.resource://$packageName/${songs[currentSongIndex]}"))
            mediaPlayer.prepare()
            mediaPlayer.start()
            Log.d("MusicService", "Playing song at index: $currentSongIndex")
        } catch (e: IOException) {
            Log.e("MusicService", "Error preparing MediaPlayer: ${e.message}")
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            Log.d("MusicService", "Music paused")
        }
    }

    private fun nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songs.size
        playMusic()
        Log.d("MusicService", "Playing next song at index: $currentSongIndex")
    }

    private fun previousSong() {
        currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
        playMusic()
        Log.d("MusicService", "Playing previous song at index: $currentSongIndex")
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}