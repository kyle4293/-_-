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
import com.google.firebase.auth.FirebaseAuth
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
        val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserUid != null) {
            viewModel.fetchUserGroups(currentUserUid) // Initial fetching
        }

        viewModel.userGroups.observe(viewLifecycleOwner, Observer { groups ->
            // 가져온 그룹 정보로 어댑터 설정
            groupAdapter = GroupAdapter(groups)
            binding.groupRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = groupAdapter
            }
        })

        binding.btnCreateGroup.setOnClickListener {
            val intent = Intent(requireContext(), CreateGroupActivity::class.java)
            startActivity(intent)
        }

        binding.btnSearchGroup.setOnClickListener {
            val intent = Intent(requireContext(), SearchGroupActivity::class.java)
            startActivity(intent)
        }

        binding.btnBack.setOnClickListener {
            requireActivity().finish()
        }

    }

}

