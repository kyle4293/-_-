package com.example.familyalbum.home

import android.Manifest
import android.Manifest.permission_group.STORAGE
import android.animation.ObjectAnimator
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
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


    companion object {
        private const val ARG_GROUP_ID = "group_id"
        private const val ARG_GROUP_NAME = "group_name"

        fun newInstance(groupId: String, groupName: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_GROUP_ID, groupId)
            args.putString(ARG_GROUP_NAME, groupName)
            fragment.arguments = args
            return fragment
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val groupId = arguments?.getString(ARG_GROUP_ID)
        val groupName = arguments?.getString(ARG_GROUP_NAME)
        binding.groupName.text = groupName

        if (!groupId.isNullOrEmpty()) {
            loadAndDisplayGroupImages(groupId)
            loadAndDisplayGroupUsers(groupId) // 그룹에 속한 유저들의 목록 가져와서 표시
        }

        init()
    }

    private fun loadAndDisplayGroupUsers(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val groupRef = firestore.collection("groups").document(groupId)

        groupRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val members = documentSnapshot.get("members") as? List<String>
                    members?.let {
                        fetchAndDisplayUserNames(it)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching group data: $exception")
            }
    }



    private fun fetchAndDisplayUserNames(userIds: List<String>) {
        val firestore = FirebaseFirestore.getInstance()

        val usersIntro = mutableListOf<String>()

        for (userId in userIds) {

            val userDocRef = firestore.collection("users").document(userId)
            userDocRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val userName = documentSnapshot.getString("name")
                        if (!userName.isNullOrEmpty()) {
                            usersIntro.add(userName)
                            if (usersIntro.size == userIds.size) {
                                val groupIntroText = usersIntro.joinToString(", ")
                                binding.groupIntro.text = groupIntroText
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // 실패 시 처리 로직
                }
        }
    }

    private fun loadAndDisplayGroupImages(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val imagesRef = firestore.collection("groups").document(groupId).collection("images")

        imagesRef.get()
            .addOnSuccessListener { documents ->
                val images = documents.mapNotNull { document ->
                    document.getString("imageUrl")
                }
                galleryAdapter.setGalleryList(images)
            }
            .addOnFailureListener { exception ->
                // 실패 시 처리 로직
            }
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

        isFabOpen= !isFabOpen
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
