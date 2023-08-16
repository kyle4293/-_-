package com.example.familyalbum.tip

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.TipEditActivity
import com.example.familyalbum.databinding.TipBinding
import com.google.firebase.firestore.FirebaseFirestore

class TipAdapter(private val currentGroupId: String, private val currentGroupName: String, private var tipList: List<Tip>): RecyclerView.Adapter<TipAdapter.ViewHolder>() {
    inner class ViewHolder( val binding: TipBinding): RecyclerView.ViewHolder(binding.root){
        init{

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = TipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TipAdapter.ViewHolder, position: Int) {
        val tip = tipList[position]
        holder.binding.editbutton.setOnClickListener {
            showPopupMenu(it,position)
        }
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

    private fun showPopupMenu(view: View,position: Int) {
        var firestore = FirebaseFirestore.getInstance()


        val popup = PopupMenu(view.context, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.tip_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_edit -> {
                    // Handle edit action
                    val context = view.context
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
                    true
                }
                R.id.action_delete -> {
                    // Handle delete action
                    val tip = tipList[position]
                    val query = firestore.collection("tips")
                        .whereEqualTo("title", tip.title)
                        .whereEqualTo("content", tip.content)
                        .whereEqualTo("tag", tip.tag)

                    lateinit var tipId: String
                    query.addSnapshotListener { querySnapshot, _ ->
                        for (document in querySnapshot!!.documents) {
                            tipId = document.id
                        }
                    }

                    val builder = AlertDialog.Builder(view.context)
                    builder.setTitle("잔소리 삭제")
                    builder.setMessage("삭제버튼 누르면 모든 가족들에게도 삭제됩니다. 정말로 삭제하시겠습니까?")
                    builder.setPositiveButton("삭제") { dialog, which ->
                        if (tipId != null) {
                            // tipId가 초기화된 경우에만 삭제 로직 실행
                            val tipDocRef = firestore.collection("tips").document(tipId)
                            tipDocRef.delete()
                                .addOnSuccessListener {
                                    val intent = Intent(view.context, MainActivity::class.java)
                                    intent.putExtra("fromTipEdit", "fromTipEdit")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    intent.putExtra("groupId", currentGroupId) // 그룹 정보 전달
                                    intent.putExtra("groupName", currentGroupName) // 그룹 이름 전달
                                    view.context.startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    // 삭제 실패 시 처리
                                    Log.e(ContentValues.TAG, "Error deleting document", e)
                                }
                        } else {
                            Log.e(ContentValues.TAG, "tipId is not initialized")
                        }
                        // 원래의 tip정보로 db를 찾은다음, 그 db삭제

                    }
                    builder.setNegativeButton("취소") { dialog, which ->
                        // "취소" 버튼 클릭 시 처리
                    }
                    val dialog = builder.create()
                    dialog.show()

                    true
                }
                else -> false
            }
        }
        popup.show()
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