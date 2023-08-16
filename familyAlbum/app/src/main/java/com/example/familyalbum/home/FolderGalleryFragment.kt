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
    private var galleryList: ArrayList<String> = arrayListOf()
    private lateinit var gridGalleryAdapter: GridRecyclerViewAdapter
    private val storageReference = FirebaseStorage.getInstance().reference

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
//            val groupId = arguments?.getString(HomeFragment.ARG_GROUP_ID)
            try {
                if (!groupId.isNullOrEmpty()) {
                    val uploadImageInfo = arrayListOf<String>(groupId, uri.toString())

                    //confirm Activity로 이동
                    val intent = Intent(requireContext(), PhotoConfirmActivity::class.java)
                    intent.putExtra("imageInfo", uploadImageInfo)
                    startActivity(intent)
                }
            } catch (e: IOException) {
//                e.printStackTrace()
            }
        }
    }


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
            gridGalleryAdapter = GridRecyclerViewAdapter(galleryList)
            binding.gridGallery.layoutManager = GridLayoutManager(requireContext(), 3) // 3 items per row
            binding.gridGallery.adapter = gridGalleryAdapter
            binding.textviewFolderName.text = folderName
            binding.textviewFolderInfo.text = folderDescription
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
                    uploadImageToStorage(imageUri)
                }
            } else {
                val imageUri = data?.data
                if (imageUri != null) {
                    uploadImageToStorage(imageUri)
                }
            }
        }
    }


    private fun uploadImageToStorage(imageUri: Uri) {
        val imageName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$imageName")
        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // 이미지 업로드가 완료되면 해당 이미지 URL을 파이어베이스 데이터베이스에 추가
                    addImageToFolder(imageUrl.toString())
                    updateGroupWithImageInfo(groupId, imageUrl.toString())
                }
            }
            .addOnFailureListener { exception ->
                // 이미지 업로드 실패 시 처리
            }
    }

    private fun addImageToFolder(imageUrl: String) {
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        folderRef.update("images", FieldValue.arrayUnion(imageUrl))
            .addOnSuccessListener {
                galleryList.add(imageUrl)
                gridGalleryAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 데이터베이스 업데이트 실패 시 처리
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
            init{
                itemView.setOnClickListener {
                    val uploadImageInfo = galleryList[position]

                    val intent = Intent(requireContext(), PhotoActivity::class.java)
                    intent.putExtra("imageInfo", uploadImageInfo)
                    intent.putExtra("folderId", folderId)
                    intent.putExtra("groupId", groupId)
                    intent.putExtra("groupName", groupName)
                    startActivity(intent)
                }
                itemView.setPadding(20, 20, 20, 20)
            }
        }
    }

    // This function is called when the user adds images to the folder
    private fun addImagesToFolder(imageUrls: List<String>) {
        // Update the folder's images in Firebase
        val firestore = FirebaseFirestore.getInstance()
        val folderRef = firestore.collection("groups").document(groupId)
            .collection("folders").document(folderId)

        // Update the images field with the new image URLs
        folderRef.update("images", imageUrls)
            .addOnSuccessListener {
                // Successfully updated the images
                galleryList.clear()
                galleryList.addAll(imageUrls)
                gridGalleryAdapter.notifyDataSetChanged()

                onAddImagesButtonClicked(galleryList.toList())
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    // Example usage when the user adds images to the folder
    private fun onAddImagesButtonClicked(selectedImageUrls: List<String>) {
        // Get the current images in the folder
        val currentImages = galleryList.toList()

        // Add the selected images to the current images list
        val newImages = currentImages + selectedImageUrls

        // Update the folder with the new image list
        addImagesToFolder(newImages)
    }
}
