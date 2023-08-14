package com.example.familyalbum.group

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.TipEditActivity
import com.example.familyalbum.databinding.MemberInfoBinding

class TimeTableGroupMemberAdapter(val memberList: ArrayList<MemberInfo>): RecyclerView.Adapter<TimeTableGroupMemberAdapter.ViewHolder>() {

    private var itemClickListener: ((Int) -> Unit)? = null

    // 아이템 클릭 리스너 설정
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        itemClickListener = listener
    }
    inner class ViewHolder(val binding: MemberInfoBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.member.setOnClickListener {
                // 클릭한 아이템의 위치를 리스너에 전달
                itemClickListener?.invoke(adapterPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = MemberInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val memberInfo = memberList[position]
        holder.binding.memberName.text = memberInfo.name
        Glide.with(holder.itemView.context)
            .load(memberInfo.img)
            .placeholder(R.drawable.default_profile_image) // Placeholder image while loading
            .error(R.drawable.default_profile_image) // Error image if loading fails
            .circleCrop()
            .into(holder.binding.memberImg)
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}