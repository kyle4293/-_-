package com.example.familyalbum.home

import android.Manifest
import android.Manifest.permission_group.STORAGE
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var galleryList: ArrayList<Gallery>
    private var isFabOpen = false


    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            uploadPhoto(uri) // 이미지 업로드 및 갤러리 리스트에 추가
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        galleryList = ArrayList()
        galleryAdapter = GalleryAdapter(galleryList)
        initLayout()
    }

    private fun initLayout() {
        //년, 월, 일인 경우로 일단 가정.
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.homeRecyclerView.adapter = galleryAdapter

        binding.btnAddPhoto.setOnClickListener {
            clickUpload()
        }

        binding.btnCamera.setOnClickListener {
            Toast.makeText(requireContext(), "open camera", Toast.LENGTH_SHORT).show()
            cameraAction()
        }

        binding.btnGroupSelect.setOnClickListener {
            val intent = Intent(requireContext(), GroupListActivity::class.java)
            startActivity(intent)
        }

        binding.btnGallery.setOnClickListener {
            imagePickerLauncher.launch("image/*")
            Toast.makeText(requireContext(), "open gallery", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cameraAction() {
        val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(itt)
    }


    private fun clickUpload() {
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.btnCamera, "translationX", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnGallery, "translationX", 0f).apply { start() }
            binding.btnAddPhoto.setImageResource(R.drawable.baseline_camera_24)
        } else {
            ObjectAnimator.ofFloat(binding.btnCamera, "translationX", 200f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnGallery, "translationX", 400f).apply { start() }
            binding.btnAddPhoto.setImageResource(R.drawable.baseline_clear_24)
        }
    }

    private fun uploadPhoto(selectedImageUri: Uri) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val currentDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val newPhoto = Gallery(downloadUrl, currentDate)
                    galleryList.add(newPhoto)
                    galleryAdapter.notifyItemInserted(galleryList.size - 1)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Image upload failed.", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
