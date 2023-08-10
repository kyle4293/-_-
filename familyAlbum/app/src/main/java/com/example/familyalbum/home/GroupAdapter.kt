package com.example.familyalbum.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.GroupItemBinding

class GroupAdapter(private val groupList: List<Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: GroupItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: Group) {
            binding.group = group // 데이터 바인딩을 통해 레이아웃과 데이터 바인딩
            binding.executePendingBindings() // 데이터 바인딩 갱신
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(groupList[position]) // bind 메서드를 호출하여 데이터를 레이아웃에 바인딩
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
