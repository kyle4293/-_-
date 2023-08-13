package com.example.familyalbum.home


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
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.familyalbum.group.GroupListFragment
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
            val groupId = arguments?.getString(ARG_GROUP_ID)
            if (!groupId.isNullOrEmpty()) {
                uploadPhoto(uri, groupId)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedGroupName = arguments?.getString(ARG_GROUP_NAME)
        binding.groupName.text = selectedGroupName

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

    override fun onResume() {
        super.onResume()

        val groupId = (activity as MainActivity).selectedGroupId
        val groupName = (activity as MainActivity).selectedGroupName

        // 갱신된 그룹 이름을 UI에 표시
        binding.groupName.text = groupName

        if (!groupId.isNullOrEmpty()) {
            loadAndDisplayGroupImages(groupId)
        }

        init()
    }



    private fun loadAndDisplayGroupImages(groupId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val imagesRef = firestore.collection("groups").document(groupId)

        imagesRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val images = document.get("images") as? List<String>
                    if (images != null) {
                        galleryAdapter.setGalleryList(images)
                    }
                }
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
        val viewPager = binding?.viewPager
        viewPager?.adapter = AlbumPagerAdapter(requireActivity())

        val tabTitles = listOf<String>("year","month","day","total")

        if (viewPager != null) {
            TabLayoutMediator(binding!!.tabLayout, viewPager) { tab, position ->
                tab.setText(tabTitles[position])
            }.attach()
        }


        binding.btnAddPhoto.setOnClickListener {
            clickUpload()
        }

        binding.btnCamera.setOnClickListener {
            Toast.makeText(requireContext(), "open camera", Toast.LENGTH_SHORT).show()
            cameraAction()
        }

        binding.btnGroupSelect.setOnClickListener {
            val mActivity = activity as MainActivity
            mActivity.changeFragment(GroupListFragment())
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


    private fun uploadPhoto(selectedImageUri: Uri, groupId: String) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

        imageRef.putFile(selectedImageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    // 업로드된 사진 정보를 그룹 정보에 저장
                    updateGroupWithImageInfo(groupId, downloadUrl, currentDate)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Image upload failed.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateGroupWithImageInfo(groupId: String, imageUrl: String, uploadDate: String) {
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

}
