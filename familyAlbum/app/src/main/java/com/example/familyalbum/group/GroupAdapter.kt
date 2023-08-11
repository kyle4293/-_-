package com.example.familyalbum.group

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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

            binding.btnInformGroup.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val group = groupList[position]
                    val dialog = GroupInfoDialog(group)
                    val fragmentManager = (binding.root.context as? AppCompatActivity)?.supportFragmentManager
                    fragmentManager?.let { manager ->
                        dialog.show(manager, "GroupDialog")
                    }
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

    }

    override fun getItemCount(): Int {
        return groupList.size
    }
}
