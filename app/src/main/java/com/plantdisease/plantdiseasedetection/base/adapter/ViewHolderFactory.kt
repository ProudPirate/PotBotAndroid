package com.plantdisease.plantdiseasedetection.base.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolderFactory<T : RecyclerView.ViewHolder>(@LayoutRes val layoutRes: Int) {
    fun create(parent: ViewGroup): T {
        val itemView = LayoutInflater.from(parent.context).inflate(
            layoutRes,
            parent,
            false
        )
        return createView(itemView, parent)
    }

    abstract fun createView(itemView: View, parent: ViewGroup): T
}
