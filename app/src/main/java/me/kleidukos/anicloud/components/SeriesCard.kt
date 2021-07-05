package me.kleidukos.anicloud.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import me.kleidukos.anicloud.R

class SeriesCard: RelativeLayout {

    private val thumbnail: ImageView
    private val title: TextView

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.serie_card, this)

        thumbnail = findViewById(R.id.card_thumbnail)
        title = findViewById(R.id.card_title)
    }

    constructor(context: Context): super(context){
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        inflater.inflate(R.layout.serie_card, this)

        thumbnail = findViewById(R.id.card_thumbnail)
        title = findViewById(R.id.card_title)
    }

}