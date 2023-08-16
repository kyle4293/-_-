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
import androidx.viewpager2.widget.ViewPager2
import com.example.familyalbum.MainActivity
import com.example.familyalbum.R
import com.example.familyalbum.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.example.familyalbum.group.GroupListFragment
import java.io.IOException


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var selectedGroupId: String? = null
    private var selectedGroupName: String? = null

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager2



    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val groupId = arguments?.getString(ARG_GROUP_ID)
            try {
                if (!groupId.isNullOrEmpty()) {
                    val uploadImageInfo = arrayListOf<String>(groupId, uri.toString())

                    //confirm Activity로 이동
                    val intent = Intent(requireContext(), PhotoConfirmActivity::class.java)
                    intent.putExtra("imageInfo", uploadImageInfo)
                    intent.putExtra("groupId", selectedGroupId)
                    intent.putExtra("groupName", selectedGroupName)
                    startActivity(intent)
                }
            } catch (e: IOException) {
//                e.printStackTrace()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = viewPagerAdapter

        selectedGroupId = (activity as MainActivity).selectedGroupId
        selectedGroupName = (activity as MainActivity).selectedGroupName

        if(selectedGroupName != null){
            binding.textviewIntro.text = selectedGroupName+"의 추억"
        }else{
//            binding.groupName.text = "어플이름"
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        initLayout()

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(ARG_GROUP_ID, selectedGroupId)
        outState.putString(ARG_GROUP_NAME, selectedGroupName)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        if (savedInstanceState != null) {
            selectedGroupId = savedInstanceState.getString(ARG_GROUP_ID)
            selectedGroupName = savedInstanceState.getString(ARG_GROUP_NAME)
            updateGroupInfoInView(selectedGroupName) // 화면 갱신 메서드 호출
        }
    }

    private fun updateGroupInfoInView(groupName: String?) {
        binding.textviewIntro.text = groupName + "의 추억"
        // 다른 UI 요소들도 업데이트
    }


    private fun initLayout() {

        viewPager = binding.viewPager // viewPager 초기화 추가
        viewPagerAdapter = ViewPagerAdapter(requireActivity())
        viewPager.adapter = viewPagerAdapter // 어댑터 설정


        binding.layoutGroupSelect.setOnClickListener {
            val mActivity = activity as MainActivity
            mActivity.changeFragment(GroupListFragment())
        }

        //폴더 생성 화면으로 이동.
        binding.layoutFolderCreate.setOnClickListener {
            val groupId = arguments?.getString(ARG_GROUP_ID)
            val groupName = arguments?.getString(ARG_GROUP_NAME)
            val intent = Intent(requireContext(), FolderCreateActivity::class.java)
            intent.putExtra("groupId", groupId)
            intent.putExtra("groupName", groupName)
            startActivity(intent)
        }
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity){
        override fun getItemCount(): Int {
            return 1
        }
        //tab layout 선택에 따라 viewPager 부분에 다른 fragment binding

        override fun createFragment(position: Int): Fragment {
            var groupId = selectedGroupId
            var groupName = selectedGroupName

            // Fragment for 전체 사진 보기
            if(groupId != null && groupName!=null)
                return FolderListFragment(groupId, groupName)
            else
                return FolderListFragment("NO_GROUP", "")
        }
    }
}