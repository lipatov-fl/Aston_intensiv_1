package com.example.astonintensiv1

import MusicService
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.astonintensiv1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val musicServiceIntent = Intent(this, MusicService::class.java)

        binding.togglePlaybackBtn.setOnClickListener {
            musicServiceIntent.action = "PLAY"
            startService(musicServiceIntent)
        }

        binding.nextImgBtn.setOnClickListener {
            musicServiceIntent.action = "NEXT"
            startService(musicServiceIntent)
        }

        binding.previousImgBtn.setOnClickListener {
            musicServiceIntent.action = "PREVIOUS"
            startService(musicServiceIntent)
        }
    }
}