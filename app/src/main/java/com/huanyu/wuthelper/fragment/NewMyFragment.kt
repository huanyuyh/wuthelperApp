package com.huanyu.wuthelper.fragment

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.activity.AboutUsActivity
import com.huanyu.wuthelper.activity.CheckUpdateActivity
import com.huanyu.wuthelper.activity.CourseListActivity
import com.huanyu.wuthelper.activity.CourseSettingsActivity
import com.huanyu.wuthelper.activity.DebugActivity
import com.huanyu.wuthelper.activity.DianFeeActivity
import com.huanyu.wuthelper.activity.NaviActivity
import com.huanyu.wuthelper.activity.OneWordActivity
import com.huanyu.wuthelper.activity.TaskActivity
import com.huanyu.wuthelper.activity.UserGuideActivity
import com.huanyu.wuthelper.activity.UsersActivity
import com.huanyu.wuthelper.databinding.FragmentNewCourseBinding
import com.huanyu.wuthelper.databinding.FragmentNewMyBinding

class NewMyFragment : Fragment() {

    companion object {
        fun newInstance() = NewMyFragment()
    }
    private lateinit var _binding:FragmentNewMyBinding
    private val viewModel: NewMyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewMyBinding.inflate(layoutInflater, container, false)

        _binding.allCourse.setOnClickListener {
            val intent = Intent(requireContext(),CourseListActivity::class.java)
            startActivity(intent)
        }
        _binding.allTask.setOnClickListener {
            val intent = Intent(requireContext(),TaskActivity::class.java)
            startActivity(intent)
        }
        _binding.allUser.setOnClickListener {
            val intent = Intent(requireContext(),UsersActivity::class.java)
            startActivity(intent)
        }
        _binding.allDianfee.setOnClickListener {
            val intent = Intent(requireContext(), DianFeeActivity::class.java)
            startActivity(intent)
        }

        _binding.allOneWord.setOnClickListener {
            val intent = Intent(requireContext(), OneWordActivity::class.java)
            startActivity(intent)
        }
        _binding.toNavi.setOnClickListener {
            val intent = Intent(requireContext(), NaviActivity::class.java)
            startActivity(intent)
        }
        _binding.toCourseSetting.setOnClickListener {
            val intent = Intent(requireContext(),CourseSettingsActivity::class.java)
            startActivity(intent)
        }
        _binding.debugTool.setOnClickListener {
            val intent = Intent(requireContext(),DebugActivity::class.java)
            startActivity(intent)
        }
        _binding.useGuide.setOnClickListener {
            val intent = Intent(requireContext(),UserGuideActivity::class.java)
            startActivity(intent)
        }
        _binding.checkUpdate.setOnClickListener {
            val intent = Intent(requireContext(),CheckUpdateActivity::class.java)
            startActivity(intent)
        }
        _binding.aboutAPP.setOnClickListener {
            val intent = Intent(requireContext(),AboutUsActivity::class.java)
            startActivity(intent)
        }
        _binding.debugUs.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.qq.com/sheet/DR1REY0d6cFJObHN3?tab=BB08J2"))
            startActivity(intent)
        }
        return _binding.root
    }
}