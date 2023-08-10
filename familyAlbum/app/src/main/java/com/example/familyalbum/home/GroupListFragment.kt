package com.example.familyalbum.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.FragmentGroupListBinding

class GroupListFragment : Fragment() {

    private lateinit var binding: FragmentGroupListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        binding.btnBack.setOnClickListener {
            //back to home fragement
            val mActivity = activity as MainActivity
            mActivity.changeFragment(1)
        }

        binding.btnCreateGroup.setOnClickListener {
            //group create
        }

    }
}