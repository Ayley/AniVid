package me.kleidukos.anicloud.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.kleidukos.anicloud.R
import me.kleidukos.anicloud.models.anicloud.Episode
import me.kleidukos.anicloud.models.anicloud.Language
import me.kleidukos.anicloud.models.anicloud.Stream
import me.kleidukos.anicloud.ui.videoplayer.StreamPlayer

class SeasonAdapterRecycler(
    private val context: Context,
    private val episodes: List<Episode>,
    private val stream: Stream
) :
    RecyclerView.Adapter<SeasonAdapterRecycler.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val episode = view.findViewById<RelativeLayout>(R.id.episode)
        val thumbnail: ImageView = view.findViewById(R.id.episode_thumbnail)
        val title: TextView = view.findViewById(R.id.episode_title)
        val description: TextView = view.findViewById(R.id.episode_description)
        val languageContent: RelativeLayout = view.findViewById(R.id.language_selection)
        val german: ImageButton = view.findViewById(R.id.language_german)
        val japaneseGerman: ImageButton = view.findViewById(R.id.language_japanese_german)
        val japaneseEnglish: ImageButton = view.findViewById(R.id.language_japanese_english)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.season_list_item, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val episode: Episode = episodes[position]

        val sdk_version = android.os.Build.VERSION.SDK_INT
        if(episode.seen) {
            if (sdk_version < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.itemView.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shape_seen
                    )
                )
                holder.languageContent.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shape_seen
                    )
                )
            } else {
                holder.itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.shape_seen)
                holder.languageContent.background =
                    ContextCompat.getDrawable(context, R.drawable.shape_seen)
            }
        }else{
            if (sdk_version < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.itemView.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shape
                    )
                )
                holder.languageContent.setBackgroundDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.shape
                    )
                )
            } else {
                holder.itemView.background =
                    ContextCompat.getDrawable(context, R.drawable.shape)
                holder.languageContent.background =
                    ContextCompat.getDrawable(context, R.drawable.shape)
            }
        }

        if (episode.poster != null && episode.poster.isNotEmpty()) {
            Picasso.get().load(episode.poster).into(holder.thumbnail)
        } else {
            holder.thumbnail.visibility = View.GONE
            holder.itemView.findViewById<CardView>(R.id.episode_card).visibility = View.GONE
        }

        GlobalScope.launch(Dispatchers.Main) {

            var name: String? = if (episode.titleGerman?.isNotEmpty()!!) {
                episode.titleGerman
            } else {
                episode.titleEnglish
            }

            val description = episode.description

            holder.title.text = name
            holder.description.text = description
        }

        Log.d("StreamView", episode.season.toString())

        if (episode.providers.isNotEmpty()) {
            if (containsProvider(Language.GERMAN, episode)) {
                holder.german.visibility = View.VISIBLE
            }

            if (containsProvider(Language.JAPANESE_GERMAN, episode)) {
                holder.japaneseGerman.visibility = View.VISIBLE
            }


            if (containsProvider(Language.JAPANESE_ENGLISH, episode)) {
                holder.japaneseEnglish.visibility = View.VISIBLE
            }

            holder.german.setOnClickListener {
                openVideoPlayerWithLanguage(stream.title, Language.GERMAN, position, episode.season)
            }

            holder.japaneseGerman.setOnClickListener {
                openVideoPlayerWithLanguage(
                    stream.title,
                    Language.JAPANESE_GERMAN,
                    position,
                    episode.season
                )
            }

            holder.japaneseEnglish.setOnClickListener {
                openVideoPlayerWithLanguage(
                    stream.title,
                    Language.JAPANESE_ENGLISH,
                    position,
                    episode.season
                )
            }
        }else{

        }

        holder.itemView.setOnClickListener {
            if (holder.languageContent.visibility == View.GONE) {
                holder.episode.visibility = View.GONE
                holder.languageContent.visibility = View.VISIBLE
            } else {
                holder.episode.visibility = View.VISIBLE
                holder.languageContent.visibility = View.GONE
            }
        }
    }

    fun containsProvider(language: Language, episode: Episode): Boolean {
        for (provider in episode.providers) {
            if (provider.language == language) {
                return true
            }
        }
        return false
    }

    private fun openVideoPlayerWithLanguage(
        stream: String,
        language: Language,
        episode: Int,
        season: Int
    ) {
        val intent = Intent(context, StreamPlayer::class.java).putExtra("episode", episode)
            .putExtra("language", language).putExtra("stream", stream).putExtra("season", season)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }
}
