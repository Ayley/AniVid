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
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.models.Season
import me.kleidukos.anicloud.models.Stream
import me.kleidukos.anicloud.scraping.SeasonFetcher
import me.kleidukos.anicloud.scraping.StreamFetcher
import me.kleidukos.anicloud.ui.stream.StreamView
import java.lang.Exception

class StreamAdapterRecycler(
    private val context: Context,
    private val displayStreams: List<DisplayStream>,
    private val margin: Int
) : RecyclerView.Adapter<StreamAdapterRecycler.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.card_title)
        val imageView: ImageView = view.findViewById(R.id.card_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.serie_card, parent, false)

        val params: RecyclerView.LayoutParams = view.layoutParams as RecyclerView.LayoutParams

        val width = (context.resources.displayMetrics.widthPixels / 3) - margin

        val height = (width * 1.9).toInt()

        params.width = width

        params.height = height

        view.layoutParams = params

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val displayStream: DisplayStream = displayStreams[position]

        var name: String = displayStream.name

        holder.textView.text = name
        Picasso.get().load(displayStream.cover).into(holder.imageView)

        holder.itemView.setOnClickListener {

            GlobalScope.launch(Dispatchers.Main) {
                var stream: Stream? = StreamFetcher.loadStream(
                    displayStream.url,
                    displayStream.name,
                    displayStream.cover
                )

                val intent = Intent(context, StreamView::class.java).putExtra("stream", stream)

                withContext(Dispatchers.Main) {
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return displayStreams.size
    }
}
