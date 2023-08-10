package com.example.familyalbum.home
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.FragmentGroupListBinding
import com.google.firebase.firestore.FirebaseFirestore

class GroupListFragment : Fragment() {

    private lateinit var binding: FragmentGroupListBinding
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var viewModel: CreateGroupViewModel // Add this line
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

        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java) // Initialize viewModel


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

        binding.btnCreateGroup.setOnClickListener {
            // 그룹 추가 화면으로 이동
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            startActivity(intent)
        }

        // 그룹 생성이 성공한 경우 관찰하여 그룹 리스트 화면으로 이동
        viewModel.groupCreationSuccess.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                // 그룹 생성 성공 시 추가 동작
                // 예를 들어 그룹 리스트 갱신 등
            } else {
                // 그룹 생성 실패 시 추가 동작
            }
        })
    }
}
