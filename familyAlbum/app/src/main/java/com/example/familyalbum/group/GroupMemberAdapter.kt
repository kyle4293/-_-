package com.example.familyalbum.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.MemberInfoBinding

class GroupMemberAdapter(val memberList: ArrayList<MemberInfo>): RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: MemberInfoBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MemberInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memberInfo = memberList[position]
        holder.binding.memberName.text = memberInfo.name
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}