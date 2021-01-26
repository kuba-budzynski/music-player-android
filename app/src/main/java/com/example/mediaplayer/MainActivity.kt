package com.example.mediaplayer

import ViewModels.SongViewModel
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.mediaplayer.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var mp: MediaPlayer? = null
    private val songs: SongViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var startId = intent.getIntExtra("id", 1)
        songs.setCurrent(startId)
        controlSong(songs.getCurrent())

        binding.apply {
            bNext.setOnClickListener {
                if(mp != null){
                    cleanUp()
                    controlSong(songs.getNext())
                    mp?.start()
                }
            }
            bPrevious.setOnClickListener {
                if(mp != null){
                    cleanUp()
                    controlSong(songs.getPrev())
                    mp?.start()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mp != null){
            cleanUp()
        }
    }

    override fun onStop() {
        super.onStop()
        if(mp != null){
            cleanUp()
        }
    }

    private fun controlSong(id: Int){
        mp = MediaPlayer.create(applicationContext, id)
        val meta = MediaMetadataRetriever()
        val fileDescription = resources.openRawResourceFd(id)
        meta.setDataSource(
            fileDescription.fileDescriptor,
            fileDescription.startOffset,
            fileDescription.length
        );
        initializeSeekBar()

        binding.apply {

            songTitle.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            songAuthor.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)

            if(meta.embeddedPicture != null){
                val image = BitmapFactory.decodeByteArray(meta.embeddedPicture,0, (meta.embeddedPicture)!!.size)
                coverArt.load(image){transformations(RoundedCornersTransformation(48f))}
            }
            else coverArt.load(R.drawable.ic_note)

            bPlay.setOnClickListener {
                if (mp == null) {
                    mp = MediaPlayer.create(applicationContext, id)
                    initializeSeekBar()
                }
                mp?.start()
            }

            bPause.setOnClickListener {
                if (mp != null) mp?.pause()
            }

            bStop.setOnClickListener {
                if (mp != null) {
                   cleanUp()
                   controlSong(songs.getCurrent())
                }
            }

            bBack10.setOnClickListener{
                if(mp != null){
                    var newSeek = (mp!!.currentPosition - 10000).coerceAtLeast(0)
                    mp?.seekTo(newSeek)
                }
            }

            bForward10.setOnClickListener {
                if(mp != null){
                    val newSeek = (mp!!.currentPosition + 10000).coerceAtMost(mp!!.duration)
                    mp?.seekTo(newSeek)
                }
            }

            seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (fromUser) mp?.seekTo(progress)
                }
                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }
                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            })
        }
    }
    private fun initializeSeekBar(){
        binding.apply {
            seekBar.max = mp!!.duration
            seekBarUpper.text = toTime(mp!!.duration)
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    try {
                        seekBar.progress = mp!!.currentPosition
                        seekBarLower.text = toTime(mp!!.currentPosition)
                        handler.postDelayed(this, 1000)
                    } catch (e: Exception) {
                        seekBar.progress = 0
                    }
                }
            }, 0)
        }
    }

    private fun toTime(duration: Int): String{
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        );
    }

    private fun cleanUp(){
        if(mp != null){
            binding.apply {
                mp?.stop()
                mp?.reset()
                mp?.release()
                mp = null
                seekBarLower.text = toTime(0)
                seekBarUpper.text = toTime(0)
                seekBar.progress = 0
                songAuthor.text = ""
                songTitle.text = ""
                seekBar.setOnSeekBarChangeListener(null)
            }
        }
    }
}