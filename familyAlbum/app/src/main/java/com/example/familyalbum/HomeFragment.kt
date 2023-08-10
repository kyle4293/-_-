package com.example.familyalbum

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.FragmentChatBinding
import com.example.familyalbum.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
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

        binding.btnAddPhoto.setOnClickListener {
            uploadPhoto()
        }

    }

    private fun uploadPhoto() {
        //camera or album으로 이동 후 사진 업로드.
    }
}