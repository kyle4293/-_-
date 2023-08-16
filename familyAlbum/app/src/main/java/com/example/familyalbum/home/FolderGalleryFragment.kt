package com.example.familyalbum.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentFolderGalleryBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class FolderGalleryFragment(val groupId: String, val groupName: String, val folderId: String, val folderName: String) : Fragment() {
    private lateinit var binding: FragmentFolderGalleryBinding
    private val galleryMap: MutableMap<String, String> = mutableMapOf()
    private lateinit var gridGalleryAdapter: GridRecyclerViewAdapter
    private val storageReference = FirebaseStorage.getInstance().reference


    companion object {
        private const val REQUEST_IMAGE_PICK = 100
    }

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
        val groupName = arguments?.getString("groupName")
        val folderName = arguments?.getString("folderName")
        val folderDescription = arguments?.getString("folderDescription")


        if (folderId != null && groupId != null) {
            loadAndDisplayFolderImages(groupId, folderId)
            gridGalleryAdapter = GridRecyclerViewAdapter(emptyList()) // 초기에는 빈 리스트를 사용
            binding.gridGallery.layoutManager =
                GridLayoutManager(requireContext(), 3) // 3 items per row
            binding.gridGallery.adapter = gridGalleryAdapter
            binding.textviewFolderName.text = folderName
        }


        binding.btnAddImage.setOnClickListener {
            openImagePicker()
//            imagePickerLauncher.launch("image/*")
//            Toast.makeText(requireContext(), "open gallery", Toast.LENGTH_SHORT).show()
        }

        binding.btnFolderModify.setOnClickListener {
            val intent = Intent(requireContext(), FolderModifyActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            intent.putExtra("folderId", folderId)
            startActivity(intent)
        }

    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            val selectedImageUris = data?.clipData

            if (selectedImageUris != null) {
                for (i in 0 until selectedImageUris.itemCount) {
                    val imageUri = selectedImageUris.getItemAt(i).uri
                    uploadImageToStorage(imageUri, "")
                }
            } else {
                val imageUri = data?.data
                if (imageUri != null) {
                    uploadImageToStorage(imageUri, "")
                }
            }
        }
    }


    private fun uploadImageToStorage(imageUri: Uri, description: String) {
        val imageName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$imageName")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // 이미지 업로드가 완료되면 해당 이미지 정보를 파이어베이스 데이터베이스에 추가
                    addImageToFolder(imageUrl.toString(), description)
                    updateGroupWithImageInfo(groupId, imageUrl.toString())
                }
            }
            .addOnFailureListener { exception ->
                // 이미지 업로드 실패 시 처리
            }
    }

    private fun addImageToFolder(imageUrl: String, description: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        folderRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val images = document.get("images") as? List<Map<String, String>>

                    val newImage = mapOf(
                        "url" to imageUrl,
                        "description" to description
                    )

                    val updatedImages = images?.toMutableList()?.apply { add(newImage) }
                        ?: mutableListOf(newImage)

                    folderRef.update("images", updatedImages)
                        .addOnSuccessListener {
                            gridGalleryAdapter.addImage(newImage) // 어댑터에 이미지 추가
                        }
                        .addOnFailureListener { exception ->
                            // 데이터베이스 업데이트 실패 시 처리
                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    private fun updateGroupWithImageInfo(groupId: String, imageUrl: String) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("groups").document(groupId)

        groupRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val images = documentSnapshot.get("images") as? List<String> ?: emptyList()
                    images.toMutableList().apply {
                        add(imageUrl)
                    }.let { updatedImages ->
                        // 업로드된 사진 URL을 그룹 정보에 저장
                        groupRef.update("images", updatedImages)
                            .addOnSuccessListener {
                                val intent = Intent(requireContext(), MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                intent.putExtra("groupId", groupId) // 그룹 정보 전달
                                intent.putExtra("groupName", groupName) // 그룹 이름 전달
                                startActivity(intent)
                                // 업데이트 성공 처리
                            }
                            .addOnFailureListener { exception ->
                                // 업데이트 실패 처리
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                // 문서 가져오기 실패 처리
            }
    }


    private fun loadAndDisplayFolderImages(groupId: String, folderId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        folderRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val images = document.get("images") as? List<Map<String, String>>

                    if (images != null) {
                        val imageMapList = images.map { imageMap ->
                            val imageUrl = imageMap["url"] ?: ""
                            val description = imageMap["description"] ?: ""
                            mapOf("url" to imageUrl, "description" to description)
                        }

                        gridGalleryAdapter.setImageList(imageMapList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }


    inner class GridRecyclerViewAdapter(private var imageList: List<Map<String, String>>) :
        RecyclerView.Adapter<GridRecyclerViewAdapter.CustomViewHolder>() {

        fun setImageList(images: List<Map<String, String>>) {
            imageList = images
            notifyDataSetChanged()
        }

        fun addImage(image: Map<String, String>) {
            imageList = imageList.toMutableList().apply { add(image) }
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.gallery_grid_view, parent, false)
            var width = resources.displayMetrics.widthPixels / 3
            view.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return CustomViewHolder(view)
        }

        override fun getItemCount(): Int {
            return imageList.size
        }

        override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
            val imageUrl = imageList[position]["url"]
            val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)
            val description = imageList[position]["description"] ?: ""

            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.baseline_arrow_drop_down_24)
                .apply(RequestOptions().centerCrop())
                .error(R.drawable.baseline_camera_24)
                .into(imageView)

            holder.itemView.setOnClickListener {
                val intent = Intent(requireContext(), PhotoActivity::class.java)
                intent.putExtra("imageInfo", imageUrl)
                intent.putExtra("folderId", folderId)
                intent.putExtra("groupId", groupId)
                intent.putExtra("groupName", groupName)
                intent.putExtra("description", description) // 설명도 전달
                startActivity(intent)
            }

            holder.itemView.setPadding(20, 20, 20, 20)
        }

        inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}
