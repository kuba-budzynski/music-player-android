package ViewModels

import androidx.lifecycle.ViewModel
import com.example.mediaplayer.R

class SongViewModel: ViewModel() {
    var currentSong = 0
    var songs = mutableListOf(
        R.raw.the_trial,
        R.raw.bohemian_rhapsody,
        R.raw.broken_crown,
        R.raw.cold_blood,
        R.raw.game_of_thrones,
        R.raw.burn_the_house,
        R.raw.legend
    )
    fun getCurrent(): Int{
        return songs[currentSong]
    }

    fun getNext(): Int{
        currentSong = if(currentSong + 1 > songs.size -1) 0 else currentSong + 1
        return songs[currentSong]
    }

    fun getPrev(): Int{
        currentSong = if(currentSong - 1 < 0) songs.size - 1 else currentSong - 1
        return songs[currentSong]
    }

    fun setCurrent(id: Int){
        if(id < 0) currentSong = 0
        else if(id > songs.size) currentSong = songs.size
        else currentSong = id
    }
}