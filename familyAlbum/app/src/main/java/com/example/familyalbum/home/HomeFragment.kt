package com.example.familyalbum.home

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private var isFabOpen = false
    private lateinit var binding: FragmentHomeBinding
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var galleryList: ArrayList<Gallery>

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
        }

        binding.btnGallery.setOnClickListener {
            Toast.makeText(requireContext(), "open gallery", Toast.LENGTH_SHORT).show()
        }

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