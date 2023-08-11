package com.example.familyalbum.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.familyalbum.databinding.FragmentLinearGalleryBinding
import com.example.familyalbum.databinding.GallerylistviewBinding

class LinearGalleryFragment : Fragment() {
    private var binding: FragmentLinearGalleryBinding? = null
    //    ---dummy data for test---
    //    private var galleryList: ArrayList<Gallery> = arrayListOf(Gallery("content://com.android.providers.media.documents/document/image%3A17","hi"), Gallery("","hi2"), Gallery("","hi3"))
    private var galleryList: ArrayList<Gallery> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLinearGalleryBinding.inflate(inflater, container, false)
        binding!!.linearGallery.adapter = LinearRecyclerViewAdapter(galleryList)
        binding!!.linearGallery.layoutManager = LinearLayoutManager(activity)

        return binding!!.root
    }

    inner class LinearRecyclerViewAdapter(val galleryList: ArrayList<Gallery>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val itemBinding =
                GallerylistviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CustomViewHolder(itemBinding)

        }

        inner class CustomViewHolder(val itemBinding: GallerylistviewBinding) :
            RecyclerView.ViewHolder(itemBinding.root)

        override fun getItemCount(): Int {
            return galleryList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val photo = galleryList[position]
            val customViewHolder = holder as CustomViewHolder
            customViewHolder.itemBinding.photoDate.text = photo.date

            Glide.with(holder.itemView.context)
                .load(photo.imgsrc)
                .into(holder.itemBinding.photoSrc)
        }
    }

}