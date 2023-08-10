package com.example.familyalbum.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.GroupItemBinding

class GroupAdapter(private val groupList: List<Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: GroupItemBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(group: Group) {
//            binding.group = group // 데이터 바인딩을 통해 레이아웃과 데이터 바인딩
//            binding.executePendingBindings() // 데이터 바인딩 갱신
//        }
        init{

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groupList[position]
//        holder.binding.groupImg ~~
        holder.binding.groupName.text = group.groupName
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
