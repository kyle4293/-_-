package com.example.familyalbum.tip

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.TipAdapter
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
        tipAdapter = TipAdapter(tipList)
        initLayout()
    }

    private fun initLayout() {
        binding.tipRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tipRecyclerView.adapter = tipAdapter

        //db에서 받아
        val dummyContents1 = listOf(Content("asdf","내용1"))
        val dummyContents2 = listOf(Content("asdf","내용2"))
        val dummyContents3 = listOf(Content("asdf","내용3"))
        val dummyContents4 = listOf(Content("asdf","내용4"))

        tipList.add(Tip("제목1", "의", dummyContents1))
        tipList.add(Tip("제목2", "식", dummyContents2))
        tipList.add(Tip("제목3", "주", dummyContents3))
        tipList.add(Tip("제목4", "의", dummyContents4))

        tipAdapter.notifyDataSetChanged() // 어댑터에 데이터 변경을 알리기 위해 호출
        //notifi
    }
}