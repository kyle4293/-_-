package com.example.familyalbum.group

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.MainActivity
import com.example.familyalbum.chat.ChatFragment
import com.example.familyalbum.databinding.FragmentGroupListBinding
import com.example.familyalbum.home.HomeFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GroupListFragment : Fragment() {
    private lateinit var binding: FragmentGroupListBinding
    private lateinit var groupAdapter: GroupAdapter
    private lateinit var viewModel: CreateGroupViewModel
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

        // 어댑터 초기화
        groupAdapter = GroupAdapter(emptyList())

        // 리사이클러뷰에 어댑터 설정
        binding.groupRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = groupAdapter
        }

        init()
    }

    private fun init() {
        viewModel = ViewModelProvider(this).get(CreateGroupViewModel::class.java)

        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            viewModel.fetchUserGroups(currentUserUid)
        }

        groupAdapter.setOnGroupClickListener { group ->
            val mActivity = activity as MainActivity
            mActivity.changeFragmentWithGroup(group.groupId, group.groupName)
        }

        viewModel.userGroups.observe(viewLifecycleOwner, Observer { groups ->
            viewLifecycleOwner.lifecycleScope.launch {
                val groupListWithNames = groups.map { group ->
                    val groupId = group.groupId
                    val groupName = getGroupNameFromFirestore(groupId)
                    Group(groupId, groupName)
                }

                // 어댑터에 데이터 설정
                groupAdapter.setGroupList(groupListWithNames)
            }
        })


        viewModel.groupJoinResult.observe(viewLifecycleOwner, Observer { isSuccess ->
            if (isSuccess) {
                Toast.makeText(requireContext(), "Joined group successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to join group", Toast.LENGTH_SHORT).show()
            }
        })

        binding.btnSearchGroup.setOnClickListener {
            val intent = Intent(requireContext(), SearchGroupActivity::class.java)
            startActivity(intent)
        }

        binding.btnCreateGroup.setOnClickListener {
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            (activity as MainActivity).onBackPressed()
        }
    }


    private suspend fun getGroupNameFromFirestore(groupId: String): String {
        val groupDocRef = firestore.collection("groups").document(groupId)

        return try {
            val document = groupDocRef.get().await()
            if (document.exists()) {
                val groupName = document.getString("groupName")
                groupName ?: ""
            } else {
                ""
            }
        } catch (e: Exception) {
            ""
        }
    }
}
