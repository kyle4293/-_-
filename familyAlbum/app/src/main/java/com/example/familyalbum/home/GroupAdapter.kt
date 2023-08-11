package com.example.familyalbum.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.GroupItemBinding
class GroupAdapter(private var groupList: List<Group>) : RecyclerView.Adapter<GroupAdapter.ViewHolder>() {

    private var onGroupClickListener: ((Group) -> Unit)? = null

    fun setOnGroupClickListener(listener: (Group) -> Unit) {
        onGroupClickListener = listener
    }

    inner class ViewHolder(val binding: GroupItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val group = groupList[position]
                    onGroupClickListener?.invoke(group)
                }
            }
        }
    }

    fun setGroupList(groups: List<Group>) {
        groupList = groups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = GroupItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val group = groupList[position]
        holder.binding.groupName.text = group.groupName
        holder.binding.btnInformGroup.setOnClickListener {

        }
    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
