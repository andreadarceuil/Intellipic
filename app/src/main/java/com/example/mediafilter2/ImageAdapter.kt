package com.example.mediafilter2

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition

class ImageAdapter(private val context: Context, private val dataset: List<String>): RecyclerView.Adapter<ImageAdapter.ImageViewHolder>()  {

    class ImageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView = view.findViewById(R.id.item_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
                .inflate(R.layout.image_list_item, parent, false)
        adapterLayout.layoutParams = AbsListView.LayoutParams(300,300)
        //adapterLayout.S= (ImageView.ScaleType.CENTER_CROP)
        return ImageViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = dataset[position]
        //holder.imageView.setImageResource(item.id.toInt())
        Glide.with(context).load(item).into(holder.imageView)

    }

    override fun getItemCount()= dataset.size


}