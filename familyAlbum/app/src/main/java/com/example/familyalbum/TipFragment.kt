package com.example.familyalbum

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.FragmentTipBinding


class TipFragment : Fragment() {
    private lateinit var tipAdapter: TipAdapter
    private lateinit var tipList: ArrayList<Tip>
    private lateinit var binding: FragmentTipBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        tipList = ArrayList()
        tipList.add(Tip("제목1","내용1",3))
        tipList.add(Tip("제목2","내용2",4))
        tipAdapter = TipAdapter(tipList)
        initLayout()
    }

    private fun initLayout() {
        binding.tipRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tipRecyclerView.adapter = tipAdapter

    }
}