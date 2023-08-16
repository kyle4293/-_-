package com.example.familyalbum.tip

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.R
import com.example.familyalbum.TipEditActivity
import com.example.familyalbum.databinding.TipBinding

class TipAdapter(private val currentGroupId: String, private val currentGroupName: String, private var tipList: List<Tip>): RecyclerView.Adapter<TipAdapter.ViewHolder>() {
    inner class ViewHolder( val binding: TipBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.editbutton.setOnClickListener {
                val context = it.context
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tip = tipList[position]
                    val intent = Intent(context, TipEditActivity::class.java)

                    intent.putExtra("groupId", currentGroupId)
                    intent.putExtra("groupName", currentGroupName)
                    intent.putExtra("title", tip.title)
                    intent.putExtra("content", tip.content) // 이 역시 필요한 타입에 맞게 수정
                    intent.putExtra("tag", tip.tag)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TipAdapter.ViewHolder, position: Int) {
        val tip = tipList[position]

        holder.binding.tipTitle.text = tip.title
//        holder.binding.tag.text = tip.tag
        when (tip.tag) {
            "의" -> {
                holder.binding.tag.setBackgroundResource(R.drawable.tag1)
                holder.binding.tagName.text = "의"
                holder.binding.tagName.setTextColor(Color.parseColor("#94803e"))
                holder.binding.tagIcon.setImageResource(R.drawable.icon_tshirt)
            }
            "식" -> {
                holder.binding.tag.setBackgroundResource(R.drawable.tag2)
                holder.binding.tagName.text = "식"
                holder.binding.tagName.setTextColor(Color.parseColor("#856155"))
                holder.binding.tagIcon.setImageResource(R.drawable.icon_food)
            }
            "주" -> {
                holder.binding.tag.setBackgroundResource(R.drawable.tag3)
                holder.binding.tagName.text ="주"
                holder.binding.tagName.setTextColor(Color.parseColor("#6b5f58"))
                holder.binding.tagIcon.setImageResource(R.drawable.icon_home_filter)
            }
        }
        holder.binding.tipContent.text = tip.content

        Log.d("TipAdapter", "Contents: ${tip.content}")
    }


    override fun getItemCount(): Int {
        return tipList.size
    }

    // 이전 목록과 새 목록을 비교하여 변경 사항을 계산하는 DiffUtil 콜백 클래스
    private class TipDiffCallback(private val oldList: List<Tip>, private val newList: List<Tip>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].title == newList[newItemPosition].title
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
    fun updateData(newTipList: List<Tip>) {
        val filteredTipList = newTipList.filter { it.groupId == currentGroupId }
        val diffCallback = TipDiffCallback(tipList, filteredTipList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tipList = filteredTipList
        diffResult.dispatchUpdatesTo(this)
    }
}