package com.example.familyalbum.home
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.FragmentGroupListBinding
import com.google.firebase.firestore.FirebaseFirestore

class GroupListFragment : Fragment() {

    private lateinit var binding: FragmentGroupListBinding
    private lateinit var groupAdapter: GroupAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGroupListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val dummyGroupList = ArrayList<Group>()

        // 파이어베이스에서 그룹 정보 가져오기
        firestore.collection("groups")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val groupId = document.id
                    val groupName = document.getString("groupName") ?: ""
                    val group = Group(groupId, groupName)
                    dummyGroupList.add(group)
                }

                // 가져온 그룹 정보로 어댑터 설정
                groupAdapter = GroupAdapter(dummyGroupList)
                binding.groupRecyclerView.apply {
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = groupAdapter
                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                Toast.makeText(requireContext(), "Failed to fetch group data", Toast.LENGTH_SHORT).show()
            }

        binding.btnAddGroup.setOnClickListener {
            // 그룹 추가 화면으로 이동
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            startActivity(intent)
        }
    }
}
