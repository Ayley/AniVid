package me.kleidukos.anicloud.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.circularreveal.CircularRevealHelper
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.activities.MainActivity
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.models.Season
import me.kleidukos.anicloud.models.Stream
import me.kleidukos.anicloud.room.RoomDisplayStream
import me.kleidukos.anicloud.scraping.SeasonFetcher
import me.kleidukos.anicloud.scraping.StreamFetcher
import me.kleidukos.anicloud.ui.stream.StreamView
import java.lang.Exception

class StreamAdapterRecycler( private val context: Context,private val displayStreams: List<DisplayStream>) : RecyclerView.Adapter<StreamAdapterRecycler.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.card_title)
        val imageView: ImageView = view.findViewById(R.id.card_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.serie_card, parent, false)

        val params: RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams

        val width = (context.resources.displayMetrics.widthPixels / 3) - 15

        params.width = width

        view.layoutParams = params

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val displayStream: DisplayStream = displayStreams[position]

        var name: String = displayStream.name

        holder.textView.text = name
        Picasso.get().load(displayStream.cover).into(holder.imageView)

        val watchlistDao = MainActivity.database().watchlistDao().getWatchlist()

        val id = watchlistDao.size

        val roomDisplayStream = if (id == 0) {
            RoomDisplayStream(0, displayStream.name, displayStream.cover, displayStream.url)
        } else {
            RoomDisplayStream(id, displayStream.name, displayStream.cover, displayStream.url)
        }

        holder.itemView.setOnClickListener {

            val contains: Boolean =
                watchlistDao.none { it.name.contains(displayStream.name, true) }

            if(contains){
                MainActivity.database().watchlistDao().insertWatchlist(roomDisplayStream)
            }

            GlobalScope.launch(Dispatchers.Main) {
                var stream: Stream? = StreamFetcher.loadStream(
                    displayStream.url,
                    displayStream.name,
                    displayStream.cover
                )

                val intent = Intent(context, StreamView::class.java).putExtra("stream", stream)

                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return displayStreams.size
    }
}
