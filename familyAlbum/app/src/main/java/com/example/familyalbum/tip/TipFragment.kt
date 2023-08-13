package com.example.familyalbum.tip

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.FragmentTipBinding
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class TipFragment : Fragment() {
    private lateinit var tipAdapter: TipAdapter
    private lateinit var binding: FragmentTipBinding
    private var tipList: List<DocumentSnapshot> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTipBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        tipAdapter = TipAdapter(emptyList())

        binding.tipPlusButton.setOnClickListener {
            val intent = Intent(context, TipPlusActivity::class.java)
            startActivity(intent)
        }

        initLayout()
    }

    private fun initLayout() {
        binding.tipRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tipRecyclerView.adapter = tipAdapter

        //firestore에서 데이터 가져오기
        val db = FirebaseFirestore.getInstance()
        val tipsCollection = db.collection("tips")
        tipsCollection.get().addOnSuccessListener { documents ->
            val tipList = documents.documents.mapNotNull { document ->
                val title = document.getString("title") ?: ""
                val tag = document.getString("tag") ?: ""
                val content = document.getString("content") ?: ""

                Tip(title, tag, content)
            }
            tipAdapter.updateData(tipList)
        }





    }
}
