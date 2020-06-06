package com.plantdisease.plantdiseasedetection.imageloader

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.load.engine.DiskCacheStrategy

class GlideLoader constructor(private val context: Context) {

    fun loadImage(url: String, holder: ImageView) {
        GlideApp.with(context)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(holder)
    }


    private fun clear(holder: ImageView) {
        holder.setImageDrawable(null)
    }
}
