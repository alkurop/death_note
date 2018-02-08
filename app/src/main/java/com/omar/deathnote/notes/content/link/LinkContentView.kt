package com.omar.deathnote.notes.content.link

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.omar.deathnote.R

class LinkContentView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        style: Int = 0
) : LinearLayout(context, attributeSet, style) {
    init {

        if (isInEditMode.not()) {
            orientation = LinearLayout.HORIZONTAL
            weightSum = 3f
            LayoutInflater.from(context).inflate(R.layout.link_content, this, true)
        }
    }

    val imagePost by lazy { findViewById<ImageView>(R.id.image_post) }
    val title by lazy { findViewById<TextView>(R.id.title) }
    val url by lazy { findViewById<TextView>(R.id.url) }
    val description by lazy { findViewById<TextView>(R.id.description) }
}
