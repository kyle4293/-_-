package com.example.familyalbum.home

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentFolderGalleryBinding
import com.google.firebase.firestore.FirebaseFirestore

class FolderGalleryFragment(val groupId: String, val folderId: String) : Fragment() {
    private lateinit var binding: FragmentFolderGalleryBinding
    private var galleryList: ArrayList<String> = arrayListOf()
    private lateinit var gridGalleryAdapter: GridRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val folderId = arguments?.getString("folderId")
        val groupId = arguments?.getString("groupId")
        Log.e(TAG, "0")


        if (folderId != null && groupId != null) {
            Log.e(TAG, "1")

            loadAndDisplayFolderImages(groupId, folderId)
            gridGalleryAdapter = GridRecyclerViewAdapter(galleryList)
            binding.gridGallery.layoutManager = GridLayoutManager(requireContext(), 3) // 3 items per row
            binding.gridGallery.adapter = gridGalleryAdapter
        }
    }



    private fun loadAndDisplayFolderImages(groupId: String, folderId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        folderRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val images = document.get("images") as? List<String>
                    if (images != null) {
                        galleryList.addAll(images)
                        Log.e(TAG, galleryList.toString())
                        gridGalleryAdapter.notifyDataSetChanged()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    inner class GridRecyclerViewAdapter(val galleryList: ArrayList<String>) : RecyclerView.Adapter<GridRecyclerViewAdapter.CustomViewHolder>() {

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
            val imageUrl = galleryList[position]
            val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_arrow_drop_down_24)
                .apply(RequestOptions().centerCrop())
                .error(R.drawable.baseline_camera_24)
                .into(imageView)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        }
    }
}
