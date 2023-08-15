package com.example.familyalbum.home


import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.familyalbum.group.GroupListFragment
import java.io.IOException


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var isFabOpen = false

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val groupId = arguments?.getString(ARG_GROUP_ID)
            try {
                if (!groupId.isNullOrEmpty()) {
                    val uploadImageInfo = arrayListOf<String>(groupId, uri.toString())

                    //confirm Activity로 이동
                    val intent = Intent(requireContext(), PhotoConfirmActivity::class.java)
                    intent.putExtra("imageInfo", uploadImageInfo)
                    startActivity(intent)
                }
            } catch (e: IOException) {
//                e.printStackTrace()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedGroupName = arguments?.getString(ARG_GROUP_NAME)
        if(selectedGroupName != null){
            binding.textviewIntro.text = selectedGroupName+"의 추억"
            binding.textviewGroupName.text = selectedGroupName
        }else{
            binding.groupName.text = "어플이름"
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    companion object {
        private const val ARG_GROUP_ID = "group_id"
        private const val ARG_GROUP_NAME = "group_name"

        fun newInstance(groupId: String, groupName: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString(ARG_GROUP_ID, groupId)
            args.putString(ARG_GROUP_NAME, groupName)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onResume() {
        super.onResume()

        val groupId = (activity as MainActivity).selectedGroupId
        val groupName = (activity as MainActivity).selectedGroupName

        //공유 데이터 update
        if (groupName != null && groupId != null) {
            setData(groupId,groupName)
        }

        initLayout()
    }

    private fun setData(groupId: String,groupName: String) {
        // 공유 데이터 설정
        (activity as? MainActivity)?.sharedViewModel?.currentGroupID = groupId
        (activity as? MainActivity)?.sharedViewModel?.currentGroupName = groupName
    }

    private fun initLayout() {

        //viewPager adapter 연결, TapLayout 설정
        val viewPager = binding?.viewPager
        viewPager?.adapter = ViewPagerAdapter(requireActivity())

        val tabTitles = listOf<String>("전체 사진", "폴더 목록")
        val tabIcons = listOf(R.drawable.icon_gallery, R.drawable.baseline_create_new_folder_24)
        if (viewPager != null) {
            TabLayoutMediator(binding!!.tabLayout, viewPager) { tab, position ->
                tab.setText(tabTitles[position])
                tab.setIcon(tabIcons[position])
            }.attach()
        }

        binding.layoutGroupSelect.setOnClickListener {
            val mActivity = activity as MainActivity
            mActivity.changeFragment(GroupListFragment())
        }

        binding.layoutImgUpload.setOnClickListener {
            imagePickerLauncher.launch("image/*")
            Toast.makeText(requireContext(), "open gallery", Toast.LENGTH_SHORT).show()
        }

        //폴더 생성 화면으로 이동.
        binding.layoutFolderCreate.setOnClickListener {
            val groupId = arguments?.getString(ARG_GROUP_ID)
            val intent = Intent(requireContext(), FolderCreateActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }

    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
        //tab layout 선택에 따라 viewPager 부분에 다른 fragment binding

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            var groupId = arguments?.getString(ARG_GROUP_ID)

            return when (position) {
                0 -> {
                    // Fragment for 전체 사진 보기
                    if(groupId != null) TotalGalleryFragment(groupId)
                    else TotalGalleryFragment("NO_GROUP")
                }
                1 -> {
                    // Fragment for FolderList 보기
                    if(groupId != null) FolderListFragment(groupId)
                    else FolderListFragment("NO_GROUP")
                }
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }
    }
}
