package me.kleidukos.anicloud.adapter

import android.content.Context
import android.content.Intent
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
import me.kleidukos.anicloud.models.anicloud.SimpleStream
import me.kleidukos.anicloud.room.series.RoomDisplayStream
import me.kleidukos.anicloud.ui.videoplayer.StreamVideoPlayer

class SeasonAdapterRecycler(
    private val context: Context,
    private val episodes: List<Episode>,
    private val stream: SimpleStream
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
        val episode = episodes[position]
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

            var name: String? = if (episode.title_german?.isNotEmpty()!!) {
                episode.title_german
            } else {
                episode.title_english
            }

            val description = episode.description

            holder.title.text = name
            holder.description.text = description
        }

        if (episode.languages != null && episode.languages.isNotEmpty()) {
            if (episode.languages.contains(Language.GERMAN)) {
                holder.german.visibility = View.VISIBLE
            }else{
                holder.german.visibility = View.GONE
            }

            if (episode.languages.contains(Language.JAPANESE_GERMAN)) {
                holder.japaneseGerman.visibility = View.VISIBLE
            }else{
                holder.japaneseGerman.visibility = View.GONE
            }


            if (episode.languages.contains(Language.JAPANESE_ENGLISH)) {
                holder.japaneseEnglish.visibility = View.VISIBLE
            }else{
                holder.japaneseEnglish.visibility = View.GONE
            }

            holder.german.setOnClickListener {
                openVideoPlayerWithLanguage(
                    episode,
                    Language.GERMAN,
                    episode.season
                )
            }

            holder.japaneseGerman.setOnClickListener {
                openVideoPlayerWithLanguage(
                    episode,
                    Language.JAPANESE_GERMAN,
                    episode.season
                )
            }

            holder.japaneseEnglish.setOnClickListener {
                openVideoPlayerWithLanguage(
                    episode,
                    Language.JAPANESE_ENGLISH,
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

    private fun openVideoPlayerWithLanguage(
        episode: Episode,
        language: Language,
        season: Int
    ) {
        val intent = Intent(context, StreamVideoPlayer::class.java)
            .putExtra("stream", stream.title)
            .putExtra("episode", episode)
            .putExtra("season", season)
            .putExtra("language", language)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return episodes.size
    }
}
