package com.example.familyalbum.home


import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.familyalbum.group.GroupListFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
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

        //공유 데이터 update
        if (groupName != null && groupId != null) {
            setData(groupId,groupName)
        }

        if (!groupId.isNullOrEmpty()) {
            loadAndDisplayGroupImages(groupId)
        }

        init()
    }

    private fun setData(groupId: String,groupName: String) {
        // 공유 데이터 설정
        (activity as? MainActivity)?.sharedViewModel?.currentGroupID = groupId
        (activity as? MainActivity)?.sharedViewModel?.currentGroupName = groupName
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
        galleryAdapter = GalleryAdapter(this, galleryList)

        initLayout()
    }

    private fun initLayout() {

        binding.folderRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.folderRecyclerview.adapter = galleryAdapter
        binding.btnAddPhoto.setOnClickListener {
            clickUpload()
        }


        binding.btnGroupSelect.setOnClickListener {
            val mActivity = activity as MainActivity
            mActivity.changeFragment(GroupListFragment())
        }

        binding.btnGallery.setOnClickListener {
            imagePickerLauncher.launch("image/*")
            Toast.makeText(requireContext(), "open gallery", Toast.LENGTH_SHORT).show()
        }

        binding.btnFolderCreate.setOnClickListener {
            val groupId = arguments?.getString(ARG_GROUP_ID)

            val intent = Intent(requireContext(), FolderCreateActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

    }


    private fun clickUpload() {
        if (isFabOpen) {
            ObjectAnimator.ofFloat(binding.btnFolderCreate, "translationX", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnGallery, "translationX", 0f).apply { start() }
            binding.btnAddPhoto.setImageResource(R.drawable.baseline_camera_24)
        } else {
            ObjectAnimator.ofFloat(binding.btnFolderCreate, "translationX", 200f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnGallery, "translationX", 400f).apply { start() }
            binding.btnAddPhoto.setImageResource(R.drawable.baseline_clear_24)
        }

        isFabOpen= !isFabOpen
    }
}
