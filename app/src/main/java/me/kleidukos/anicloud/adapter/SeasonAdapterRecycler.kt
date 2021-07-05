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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import me.kleidukos.anicloud.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.kleidukos.anicloud.enums.Language
import me.kleidukos.anicloud.models.DisplayStream
import me.kleidukos.anicloud.models.Episode
import me.kleidukos.anicloud.models.Season
import me.kleidukos.anicloud.models.Stream
import me.kleidukos.anicloud.scraping.SeasonFetcher
import me.kleidukos.anicloud.scraping.StreamFetcher
import me.kleidukos.anicloud.ui.stream.StreamView
import me.kleidukos.anicloud.ui.videoplayer.StreamPlayer

class SeasonAdapterRecycler(private val context: Context, private val season: Season,private val  stream: Stream) :
    RecyclerView.Adapter<SeasonAdapterRecycler.Holder>() {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.episode_title)
        val description: TextView = view.findViewById(R.id.episode_description)
        val languageContent: RelativeLayout = view.findViewById(R.id.language_selection)
        val german: ImageButton = view.findViewById(R.id.language_german)
        val japaneseGerman: ImageButton = view.findViewById(R.id.language_japanese_german)
        val japaneseEnglish: ImageButton = view.findViewById(R.id.language_japanese_english)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val service = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = service.inflate(R.layout.season_listitem, parent, false)

        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val episode: Episode = season.episodes[position]

        val sdk_version = android.os.Build.VERSION.SDK_INT;
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

        Log.d("Epsisode", episode.titleGerman + " " + episode.titleEnglish + " " + position + " " + episode.seen)

        GlobalScope.launch(Dispatchers.Unconfined) {
            withContext(Dispatchers.Main) {
                var name: String? = null

                if(episode.titleGerman?.isNotEmpty()!!){
                    name = episode.titleGerman
                }else if (episode.titleEnglish?.isNotEmpty()!!){
                    name = episode.titleEnglish
                }

                val description = episode.description

                holder.title.text = name
                holder.description.text = description
            }
        }

        if(episode.hoster.isNotEmpty()){
            if(episode.hoster.first().streams.containsKey(Language.GERMAN)){
                holder.german.visibility = View.VISIBLE
            }

            if(episode.hoster.first().streams.containsKey(Language.JAPANESE_GERMAN)){
                holder.japaneseGerman.visibility = View.VISIBLE
            }

            if(episode.hoster.first().streams.containsKey(Language.JAPANESE_ENGLISH)){
                holder.japaneseEnglish.visibility = View.VISIBLE
            }

            holder.german.setOnClickListener {
                openVideoPlayerWithLanguage(stream, Language.GERMAN, position)
            }

            holder.japaneseGerman.setOnClickListener {
                openVideoPlayerWithLanguage(stream, Language.JAPANESE_GERMAN, position)
            }

            holder.japaneseEnglish.setOnClickListener {
                openVideoPlayerWithLanguage(stream, Language.JAPANESE_ENGLISH, position)
            }
        }else{

        }

        holder.itemView.setOnClickListener {
            if(holder.languageContent.visibility == View.GONE){
                holder.languageContent.visibility = View.VISIBLE
            }else{
                holder.languageContent.visibility = View.GONE
            }
        }
    }

    private fun openVideoPlayerWithLanguage(stream: Stream, language: Language, episode: Int){
        val intent = Intent(context, StreamPlayer::class.java).putExtra("season", season).putExtra("episode", episode).putExtra("language", language).putExtra("stream", stream).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }

    override fun getItemCount(): Int {
        return season.episodes.size
    }
}
