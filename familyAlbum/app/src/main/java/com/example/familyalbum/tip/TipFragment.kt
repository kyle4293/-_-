package com.example.familyalbum.tip

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
                val contents = document.get("contents") as? List<HashMap<String, String>> ?: emptyList()

                if (title.isNotEmpty() && tag.isNotEmpty()) {
                    val contentList = contents.mapNotNull { contentMap ->
                        val content = contentMap["content"]
                        if (content != null) {
                            Content(content)
                        } else {
                            null
                        }
                    }
                    Tip(title, tag, contentList)
                } else {
                    null
                }
            }
            tipAdapter.updateData(tipList)
        }
    }
}
