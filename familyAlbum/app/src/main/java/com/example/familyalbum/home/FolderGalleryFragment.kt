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
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentFolderGalleryBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*
import kotlin.collections.ArrayList

class FolderGalleryFragment(val groupId: String, val folderId: String) : Fragment() {
    private lateinit var binding: FragmentFolderGalleryBinding
    private var galleryList: ArrayList<String> = arrayListOf()
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


        if (folderId != null && groupId != null) {
            loadAndDisplayFolderImages(groupId, folderId)
            gridGalleryAdapter = GridRecyclerViewAdapter(galleryList)
            binding.gridGallery.layoutManager = GridLayoutManager(requireContext(), 3) // 3 items per row
            binding.gridGallery.adapter = gridGalleryAdapter
        }

        binding.btnAddImage.setOnClickListener {
            openImagePicker()
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
            val selectedImageUris = data?.clipData ?: return

            for (i in 0 until selectedImageUris.itemCount) {
                val imageUri = selectedImageUris.getItemAt(i).uri
                uploadImageToStorage(imageUri)
            }

            // After uploading all selected images, call the function to add them to the folder
            val selectedImageUrls = galleryList.toList() // Assuming you've already added the URLs during upload
            onAddImagesButtonClicked(selectedImageUrls)
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
