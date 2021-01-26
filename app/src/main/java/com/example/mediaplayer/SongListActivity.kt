package com.example.mediaplayer

import Recycler.ListRecyclerAdapter
import SongClickListener
import ViewModels.SongViewModel
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediaplayer.databinding.ActivitySongListBinding

private lateinit var binding: ActivitySongListBinding

class SongList : AppCompatActivity(), SongClickListener {

    private val songs: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySongListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ListRecyclerAdapter(songs, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        binding.apply {
            recycler.adapter = adapter
            recycler.layoutManager = layoutManager
        }
    }

    override fun onCardClick(id: Int) {
        Log.e("Recycler", id.toString())
        val intent = Intent(this, MainActivity::class.java).apply{
            putExtra("id", id)
        }
        startActivity(intent)
    }
}