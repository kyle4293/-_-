package com.example.familyalbum.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentFolderBinding

class FolderFragment(var folderName: String) : Fragment() {

    private lateinit var binding: FragmentFolderBinding
    private var galleryList: ArrayList<Gallery> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFolderBinding.inflate(inflater, container, false)
        binding!!.folderName.text = folderName
        binding!!.gridGallery.adapter = GridRecyclerViewAdapter(galleryList)
        binding!!.gridGallery.layoutManager = GridLayoutManager(activity, 3)

        return binding!!.root
    }


    inner class GridRecyclerViewAdapter(val galleryList: ArrayList<Gallery>) : RecyclerView.Adapter<GridRecyclerViewAdapter.CustomViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.gallery_grid_view, parent, false)
            var width = resources.displayMetrics.widthPixels / 3
            view.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return galleryList.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val photo = galleryList[position]
            val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)

            Glide.with(holder.itemView.context)
                .load(photo.imgsrc)
                .placeholder(R.drawable.baseline_arrow_drop_down_24) // Add a placeholder drawable
                .apply(RequestOptions().centerCrop())
                .error(R.drawable.baseline_camera_24)
                .into(imageView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        }
    }

}