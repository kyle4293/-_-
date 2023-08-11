package com.example.familyalbum.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.familyalbum.R
import com.example.familyalbum.databinding.GallerylistviewBinding

class GalleryAdapter(val galleryList: ArrayList<Gallery>): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: GallerylistviewBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 클릭 이벤트 처리
                }
            }
        }

    }

    fun setGalleryList(images: List<String>) {
        galleryList.clear()
        galleryList.addAll(images.map { Gallery(it, "") }) // 이미지 URL과 더미 날짜로 갤러리 객체 생성
        notifyDataSetChanged()
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

        Glide.with(holder.itemView.context)
            .load(photo.imgsrc)
            .into(holder.binding.photoSrc)
    }
}