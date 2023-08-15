package com.example.familyalbum.tip

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.databinding.FragmentTipBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class TipFragment : Fragment() {
    private lateinit var tipAdapter: TipAdapter
    private lateinit var binding: FragmentTipBinding
    private var tipList: List<DocumentSnapshot> = emptyList()
    private var currentGroupId: String? = null
    private var currentGroupName: String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentGroupId =  (activity as MainActivity).selectedGroupId ?: ""
        currentGroupName = (activity as MainActivity).selectedGroupName ?: ""
        init()
    }

    private fun init() {
        binding.tipPlusButton.setOnClickListener {
            val intent = Intent(context, TipPlusActivity::class.java)
            intent.putExtra("groupId", currentGroupId)
            intent.putExtra("groupName", currentGroupName)
            startActivity(intent)
        }

        //현재 그룹 이름
        val currentGroupID =  (activity as MainActivity).selectedGroupId ?: ""
        val currentGroupName = (activity as MainActivity).selectedGroupName ?: ""

        tipAdapter = TipAdapter(currentGroupID, currentGroupName, emptyList())

        //********태그 필터*********
        binding.allbutton.setOnClickListener {
            //firestore에서 데이터 가져오기
            val db = FirebaseFirestore.getInstance()
            val tipsCollection = db.collection("tips")
                .whereEqualTo("groupId", currentGroupID)

            tipsCollection.get().addOnSuccessListener { documents ->
                val tipList = documents.documents.mapNotNull { document ->
                    val title = document.getString("title") ?: ""
                    val tag = document.getString("tag") ?: ""
                    val content = document.getString("content") ?: ""

                    Tip(title, tag, content, currentGroupID)
                }
                tipAdapter.updateData(tipList)
            }
        }

        binding.button9.setOnClickListener {
            //의 => 태그가 "의"인 것만 데이터 가져오기
            loadTipsByTag("의")
        }

        binding.button10.setOnClickListener {
            //식
            loadTipsByTag("식")

        }

        binding.button11.setOnClickListener {
            //주
            loadTipsByTag("주")

        }

        initLayout()
    }

    private fun initLayout() {
        binding.tipRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        //현재 그룹 이름
        val currentGroupID =  (activity as MainActivity).selectedGroupId ?: ""
        val currentGroupName = (activity as MainActivity).selectedGroupName ?: ""

        tipAdapter = TipAdapter(currentGroupID, currentGroupName, emptyList())
        binding.tipRecyclerView.adapter = tipAdapter

        //firestore에서 데이터 가져오기
        val db = FirebaseFirestore.getInstance()
        val tipsCollection = db.collection("tips")
            .whereEqualTo("groupId", currentGroupID)

        tipsCollection.get().addOnSuccessListener { documents ->
            val tipList = documents.documents.mapNotNull { document ->
                val title = document.getString("title") ?: ""
                val tag = document.getString("tag") ?: ""
                val content = document.getString("content") ?: ""

                Tip(title, tag, content, currentGroupID)
            }
            tipAdapter.updateData(tipList)
        }
    }

    private fun loadTipsByTag(tag: String) {
        //현재 그룹 이름
        val currentGroupID =  (activity as MainActivity).selectedGroupId ?: ""
        val currentGroupName = (activity as MainActivity).selectedGroupName ?: ""

        val db = FirebaseFirestore.getInstance()
        val tipsCollection = db.collection("tips")
            .whereEqualTo("tag", tag)
            .whereEqualTo("groupId", currentGroupID)

        tipsCollection.get().addOnSuccessListener { documents ->
            val tipList = documents.documents.mapNotNull { document ->
                val title = document.getString("title") ?: ""
                val tag = document.getString("tag") ?: ""
                val content = document.getString("content") ?: ""

                Tip(title, tag, content, currentGroupID)
            }
            tipAdapter.updateData(tipList)
        }
    }
}
