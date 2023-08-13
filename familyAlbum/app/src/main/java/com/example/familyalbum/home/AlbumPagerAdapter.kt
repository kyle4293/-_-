package com.example.familyalbum.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter


class AlbumPagerAdapter(fragmentActivity: FragmentActivity, photos: ArrayList<String>) : FragmentStateAdapter(fragmentActivity){

    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0,1,2 -> LinearGalleryFragment() // Fragment for LinearLayout
            3 -> GridGalleryFragment() // Fragment for GridLayout
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }


}
