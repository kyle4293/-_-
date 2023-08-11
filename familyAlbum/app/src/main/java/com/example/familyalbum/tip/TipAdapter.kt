package com.example.familyalbum

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.databinding.TipBinding
import com.example.familyalbum.tip.Tip
import com.google.firebase.firestore.DocumentSnapshot

class TipAdapter(private val tipList: List<DocumentSnapshot>): RecyclerView.Adapter<TipAdapter.ViewHolder>() {
    inner class ViewHolder( val binding: TipBinding): RecyclerView.ViewHolder(binding.root){
        init{
            binding.editbutton.setOnClickListener {
                val context = it.context
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val tipSnapshot = tipList[position]
                    val title = tipSnapshot.getString("title")
                    val contents = tipSnapshot.get("contents") as? List<String> // 이건 필요한 타입에 맞게 수정
                    val tag = tipSnapshot.getString("tag")

                    val intent = Intent(context, TipEditActivity::class.java)
                    intent.putExtra("title", title)
                    intent.putExtra("contents", contents?.toTypedArray()) // 이 역시 필요한 타입에 맞게 수정
                    intent.putExtra("tag", tag)
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
        val tipSnapshot = tipList[position]
        val tip = tipSnapshot.toObject(Tip::class.java)

        if (tip != null) {
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
            holder.binding.tipContent.text = contentStringBuilder.toString()
        }
    }

    override fun getItemCount(): Int {
        return tipList.size
    }

}