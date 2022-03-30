package com.example.storage.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storage.R
import com.example.storage.model.Photo
import com.google.android.material.imageview.ShapeableImageView
import com.squareup.picasso.Picasso

class ImagesAdapter  (var context: Context, var list: List<Any>) :
    RecyclerView.Adapter<ImagesAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(R.layout.item_images_view, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.setData(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class VH(inflate: View) : RecyclerView.ViewHolder(inflate) {
        private val ivPhoto: ImageView = inflate.findViewById(R.id.iv_pin)
        fun setData(item: Any) {
            Glide.with(context).load(item).into(ivPhoto)
        }
    }

}