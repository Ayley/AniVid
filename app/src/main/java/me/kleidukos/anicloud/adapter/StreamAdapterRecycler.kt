package me.kleidukos.anicloud.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.room.watchlist.RoomWatchlist
import me.kleidukos.anicloud.tmdb.TMDB
import me.kleidukos.anicloud.ui.main.MainActivity
import me.kleidukos.anicloud.ui.stream.StreamView
import me.kleidukos.anicloud.util.StreamConverter


class StreamAdapterRecycler(
    private val context: Context,
    private val displayStreams: List<RoomDisplayStream>
) : RecyclerView.Adapter<StreamAdapterRecycler.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.card_title)
        val imageView: ImageView = view.findViewById(R.id.card_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.serie_card, parent, false)

        val params: RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams

        val width = (context.resources.displayMetrics.widthPixels / 3) - 16

        params.width = width

        view.layoutParams = params

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val displayStream = StreamConverter.convert(displayStreams[position])

            holder.textView.text = displayStream.title

            displayStream.setData(holder.imageView, null)

            val watchlistDao = MainActivity.database().watchlistDao().getWatchlist()

            val id = watchlistDao.size

            val roomWatchlist = RoomWatchlist(
                id,
                displayStream.title,
                displayStream.poster
            )

            holder.itemView.setOnClickListener {

                val contains: Boolean =
                    watchlistDao.none { it.name.contains(displayStream.title, true) }

                if (contains) {
                    MainActivity.database().watchlistDao().insertWatchlist(roomWatchlist)
                }

                GlobalScope.launch(Dispatchers.Main) {


                    val intent = Intent(context, StreamView::class.java).putExtra("title", displayStream.title)
                    context.startActivity(intent)

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return displayStreams.size
    }
}
