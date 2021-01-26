package Recycler

import SongClickListener
import ViewModels.SongViewModel
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mediaplayer.R
import java.util.concurrent.TimeUnit

class ListRecyclerAdapter(
    private val songs: SongViewModel,
    private val onSongClick: SongClickListener
    ) : RecyclerView.Adapter<ListRecyclerAdapter.ListViewHolder>() {

    inner class ListViewHolder(characterView: View): RecyclerView.ViewHolder(characterView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val id = songs.songs[position]
        val cover = holder.itemView.findViewById<ImageView>(R.id.listItemCoverArt)
        val title = holder.itemView.findViewById<TextView>(R.id.listItemTitle)
        val author = holder.itemView.findViewById<TextView>(R.id.listItemAuthor)
        val time = holder.itemView.findViewById<TextView>(R.id.listItemTime)

        val meta = MediaMetadataRetriever()
        val file = holder.itemView.resources.openRawResourceFd(id)
        meta.setDataSource(
            file.fileDescriptor,
            file.startOffset,
            file.length
        );

        if(meta.embeddedPicture != null){
            val image = BitmapFactory.decodeByteArray(meta.embeddedPicture,0, (meta.embeddedPicture)!!.size)
            cover.load(image)
        }
        else cover.load(R.drawable.ic_note)

        title.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        author.text = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        time.text = toTime(meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt())

        holder.itemView.setOnClickListener {
            onSongClick.onCardClick(position)
        }
    }

    override fun getItemCount(): Int {
        return songs.songs.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun toTime(duration: Int): String{
        return String.format("%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
        );
    }
}