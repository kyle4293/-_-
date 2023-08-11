package com.example.familyalbum.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentGridGalleryBinding



class GridGalleryFragment : Fragment() {
    private var binding: FragmentGridGalleryBinding? = null
    private var galleryList: ArrayList<Gallery> = arrayListOf(Gallery("","hi"), Gallery("","hi2"), Gallery("","hi3"))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGridGalleryBinding.inflate(inflater, container, false)
        binding!!.gridGallery.adapter =
            GridRecyclerViewAdapter(galleryList)
        binding!!.gridGallery.layoutManager = GridLayoutManager(activity, 3)
        return binding!!.root
    }

    inner class GridRecyclerViewAdapter(val galleryList: ArrayList<Gallery>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var width = resources.displayMetrics.widthPixels / 3
            var imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(imageView)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val photo = galleryList[position]
            val imageView = (holder as CustomViewHolder).imageView
            Glide.with(holder.itemView.context)
                .load(photo.imgsrc)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
        }

        inner class CustomViewHolder(var imageView: ImageView) :
            RecyclerView.ViewHolder(imageView) {
        }

        override fun getItemCount(): Int {
            return galleryList.size
        }

    }

}