package com.example.mediafilter2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardAdapter(
    private val context: Context, private val dataset: List<DashboardItem>,
    private val listener: OnItemClickListener
    ): RecyclerView.Adapter<DashboardAdapter.DashboardViewHolder>()
{
    inner class DashboardViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
    View.OnClickListener{
        val textView1: TextView = view.findViewById(R.id.titleTV)
        val textView2: TextView = view.findViewById(R.id.descriptionTV)
        val imageView: ImageView = view.findViewById(R.id.imageIV)
        init{
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position:Int = adapterPosition
            if(position != RecyclerView.NO_POSITION){
                listener.onItemClick(position)
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.dashboard_item, parent, false)
        return DashboardViewHolder((adapterLayout))
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val item = dataset[position]
        holder.textView1.text = context.resources.getString(item.stringResourceId1)
        holder.textView2.text = context.resources.getString(item.stringResourceId2)
        holder.imageView.setImageResource(item.imageResourceId)

    }

    override fun getItemCount(): Int = dataset.size

    interface OnItemClickListener {
        fun onItemClick(position:Int)
    }
}