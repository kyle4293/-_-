package com.example.familyalbum.home

import android.os.Bundle
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
    private var galleryList: ArrayList<Gallery> = arrayListOf()
    private lateinit var gridGalleryAdapter: GridRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFolderGalleryBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        galleryList = ArrayList()
        gridGalleryAdapter = GridRecyclerViewAdapter(galleryList)
        initLayout()

    }

    private fun initLayout() {
        binding!!.gridGallery.adapter = gridGalleryAdapter
        binding!!.gridGallery.layoutManager = GridLayoutManager(activity, 3)

        if(folderId!= "NO_FOLDER"){
            binding.textviewNofolder.visibility = View.GONE
            loadAndDisplayGroupImages(groupId)
        }
    }

    private fun loadAndDisplayGroupImages(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val imagesRef = firestore.collection("groups").document(groupId)

        imagesRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val images = document.get("images") as? List<String>
                    if (images != null) {
                        gridGalleryAdapter.setGalleryList(images)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리 로직
            }
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

        fun setGalleryList(images: List<String>) {
            galleryList.clear()
            galleryList.addAll(images.map { Gallery(it, "") }) // 이미지 URL과 더미 날짜로 갤러리 객체 생성
            notifyDataSetChanged()
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        }
    }
}