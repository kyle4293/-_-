package com.example.familyalbum

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.TipBinding

class TipAdapter(val tipList: ArrayList<Tip>): RecyclerView.Adapter<TipAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: TipBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TipAdapter.ViewHolder {
        val binding = TipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TipAdapter.ViewHolder, position: Int) {
        val tip = tipList[position]
        holder.binding.tipTitle.text = tip.title
        holder.binding.tipContent.text = tip.content
        holder.binding.heartcount.text = tip.heart.toString()
    }

    override fun getItemCount(): Int {
        return tipList.size
    }

}