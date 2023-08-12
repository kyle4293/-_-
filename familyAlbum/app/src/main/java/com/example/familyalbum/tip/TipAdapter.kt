package com.example.familyalbum.tip

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.R
import com.example.familyalbum.TipEditActivity
import com.example.familyalbum.databinding.TipBinding

class TipAdapter(private var tipList: List<Tip>): RecyclerView.Adapter<TipAdapter.ViewHolder>() {
    inner class ViewHolder( val binding: TipBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.editbutton.setOnClickListener {
                val context = it.context
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tip = tipList[position]
                    val intent = Intent(context, TipEditActivity::class.java)

                    intent.putExtra("title", tip.title)
                    intent.putExtra("contents", tip.contents?.toTypedArray()) // 이 역시 필요한 타입에 맞게 수정
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
        holder.binding.tag.text = tip.tag
        when (tip.tag) {
            "의" -> holder.binding.tag.setBackgroundResource(R.drawable.tag1)
            "식" -> holder.binding.tag.setBackgroundResource(R.drawable.tag2)
            "주" -> holder.binding.tag.setBackgroundResource(R.drawable.tag3)
        }

        val contentStringBuilder = StringBuilder()
        tip.contents?.forEach { content ->
            contentStringBuilder.append(content.content)
            contentStringBuilder.append("\n")
        }

        Log.d("TipAdapter", "Contents: ${tip.contents}")

        holder.binding.tipContent.text = contentStringBuilder.toString()
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
        val diffCallback = TipDiffCallback(tipList, newTipList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        tipList = newTipList
        diffResult.dispatchUpdatesTo(this)
    }
}