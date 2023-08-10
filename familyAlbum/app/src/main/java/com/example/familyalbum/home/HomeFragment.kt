package com.example.familyalbum.home

import android.Manifest
import android.Manifest.permission_group.STORAGE
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() {

    private var isFabOpen = false
    private lateinit var binding: FragmentHomeBinding
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var galleryList: ArrayList<Gallery>

    val CAMERA = arrayOf(Manifest.permission.CAMERA)
    val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val CAMERA_CODE = 98
    val STORAGE_CODE = 99

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val mActivity = activity as MainActivity
        binding.btnGroupSelect.setOnClickListener {
            mActivity.changeFragment(2)
        }

        binding.btnAddPhoto.setOnClickListener {
            uploadPhoto()
        }

        binding.btnCamera.setOnClickListener{
            Toast.makeText(requireContext(), "open camera", Toast.LENGTH_SHORT).show()
            cameraAction()
        }

        binding.btnGallery.setOnClickListener {
            Toast.makeText(requireContext(), "open gallery", Toast.LENGTH_SHORT).show()
        }

    }

    private fun cameraAction() {
        val itt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(itt, CAMERA_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //camera로 호출해서 받은거,
        //gallery로 호출해서 받은거 다르게 처리하기.
    }

    private fun addImage(uri: Uri) {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val picture = Gallery(uri,currentTime )

        galleryList.add(picture)
    }


    private fun uploadPhoto() {
        if(isFabOpen){
            ObjectAnimator.ofFloat(binding.btnCamera, "translationX", 0f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnGallery, "translationX", 0f).apply { start() }
            binding.btnAddPhoto.setImageResource(R.drawable.baseline_camera_24)
        }else{
            ObjectAnimator.ofFloat(binding.btnCamera, "translationX", 200f).apply { start() }
            ObjectAnimator.ofFloat(binding.btnGallery, "translationX", 400f).apply { start() }
            binding.btnAddPhoto.setImageResource(R.drawable.baseline_clear_24)
        }

        isFabOpen= !isFabOpen
    }
}