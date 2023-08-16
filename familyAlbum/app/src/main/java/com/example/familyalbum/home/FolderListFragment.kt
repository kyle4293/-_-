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
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FolderListViewBinding
import com.example.familyalbum.databinding.FragmentFolderListBinding
import com.google.firebase.firestore.FirebaseFirestore

class FolderListFragment(val groupId: String, val groupName: String) : Fragment() {

    private lateinit var folderAdapter: FolderListAdapter
    private lateinit var binding: FragmentFolderListBinding
    private var folderList: ArrayList<Folder> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFolderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        folderAdapter = FolderListAdapter(this, folderList)
        initLayout()
    }

    private fun initLayout() {
        binding.folderList.layoutManager = LinearLayoutManager(requireContext())
        binding.folderList.adapter = folderAdapter

        if (groupId != "NO_GROUP") {
            loadAndDisplayGroupFolders(groupId)
        }
    }

    private fun loadAndDisplayGroupFolders(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val groupFoldersRef = firestore.collection("groups").document(groupId)
            .collection("folders")

        groupFoldersRef.get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val folderName = document.getString("name")
                    val folderImages = document.get("images") as? List<String>
                    val folderDescription = document.getString("description")
                    if (folderName != null && folderImages != null && folderDescription!=null) {
                        folderList.add(Folder(document.id, folderName, folderDescription, folderImages))
                    }
                }
                folderAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    inner class FolderListAdapter(val fragment: Fragment, val folderList: ArrayList<Folder>) : RecyclerView.Adapter<FolderListAdapter.ViewHolder>() {
        inner class ViewHolder(val binding: FolderListViewBinding) : RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val selectedFolder = folderList[position]
                        val mActivity = activity as MainActivity
                        val fragment = FolderGalleryFragment(groupId, groupName, selectedFolder.id, selectedFolder.name)

                        val args = Bundle()
                        args.putString("groupId", groupId)
                        args.putString("groupName", groupName)
                        args.putString("folderId", selectedFolder.id)
                        args.putString("folderName", selectedFolder.name)
                        args.putString("folderDescription", selectedFolder.description)
                        fragment.arguments = args

                        mActivity.changeFragment(fragment)
                    }
                }
            }
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
            holder.binding.folderTitle.text = folder.name
            holder.binding.folderDescription.text = folder.description

            // Load the first image of the folder as its thumbnail
            if (folder.images.isNotEmpty()) {
                Glide.with(holder.itemView.context)
                    .load(folder.images[0])
                    .into(holder.binding.folderPhoto)
            }
        }
    }
}
