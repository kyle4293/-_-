package com.example.familyalbum.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.GallerylistviewBinding

class GalleryAdapter(val galleryList: ArrayList<Gallery>): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: GallerylistviewBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GallerylistviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return galleryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = galleryList[position]
        holder.binding.photoDate.text = photo.date
        holder.binding.photoSrc.setImageResource(photo.imgsrc!!.toInt())
    }
}