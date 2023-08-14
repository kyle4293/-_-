package com.example.familyalbum.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.FolderListViewBinding
import com.example.familyalbum.databinding.FragmentFolderListBinding
import com.google.firebase.firestore.FirebaseFirestore


class FolderListFragment(val groupId: String) : Fragment() {

    private lateinit var folderAdapter: FolderListAdapter
    private lateinit var binding: FragmentFolderListBinding
    private var folderList: ArrayList<Gallery> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFolderListBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        folderList = ArrayList()
        folderAdapter = FolderListAdapter(this, folderList)

        initLayout()
    }

    private fun initLayout() {
        binding.folderList.layoutManager = LinearLayoutManager(requireContext())
        binding.folderList.adapter = folderAdapter

        if(groupId != "NO_GROUP"){
            binding.textviewNogroup.visibility = View.GONE
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
                        folderAdapter.setGalleryList(images)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리 로직
            }
    }

    inner class FolderListAdapter(val fragment: Fragment, val folderList: ArrayList<Gallery>): RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: FolderListViewBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // 클릭 이벤트 처리
                        val mActivity = fragment.activity as MainActivity
                        mActivity.changeFragment(FolderGalleryFragment(groupId, "NO_FOLDER"))
                    }
                }
            }
        }

        fun setGalleryList(images: List<String>) {
            folderList.clear()
            folderList.addAll(images.map { Gallery(it, "") }) // 이미지 URL과 더미 날짜로 갤러리 객체 생성
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = FolderListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return folderList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val folder = folderList[position]
            //folder title, folder 대표이미지, folder 설명 binding 예정.

            Glide.with(holder.itemView.context)
                .load(folder.imgsrc)
                .into(holder.binding.folderPhoto)
        }
    }

}