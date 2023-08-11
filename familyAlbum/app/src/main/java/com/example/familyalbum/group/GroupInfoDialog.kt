package com.example.familyalbum.group

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.familyalbum.databinding.GroupInfoDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class GroupInfoDialog(private val group: Group) : DialogFragment(){

    private var binding: GroupInfoDialogBinding?= null
    private lateinit var memberAdapter: GroupMemberAdapter
    private lateinit var memberList: ArrayList<MemberInfo>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = GroupInfoDialogBinding.inflate(layoutInflater)
        memberList = ArrayList()
        memberAdapter = GroupMemberAdapter(memberList)

        initLayout()

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(binding?.root)
            .setPositiveButton("확인", null)

        return dialogBuilder.create()

    }

    private fun initLayout() {
        binding?.groupId?.text = group.groupId
        binding?.memberRecyclerview?.layoutManager = LinearLayoutManager(requireContext())
        binding?.memberRecyclerview?.adapter = memberAdapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}