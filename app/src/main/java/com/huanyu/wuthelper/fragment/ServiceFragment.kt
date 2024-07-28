package com.huanyu.wuthelper.fragment

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import com.huanyu.wuthelper.MyApplication
import com.huanyu.wuthelper.activity.UsersActivity
import com.huanyu.wuthelper.adapterr.ServiceListAdapter
import com.huanyu.wuthelper.databinding.FragmentServiceBinding
import com.huanyu.wuthelper.entity.User

class ServiceFragment : Fragment() {
    companion object {
        private const val LOG_ServiceFragment = "ServiceFragment:"
        fun newInstance() = ServiceFragment()
    }

    lateinit var _binding: FragmentServiceBinding
    private val serviceViewModel: ServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(layoutInflater, container, false)
        serviceViewModel.getPlatforms()
        _binding.serviceGird.stretchMode = GridView.STRETCH_COLUMN_WIDTH
        serviceViewModel.platformList.observe(viewLifecycleOwner){
            _binding.serviceGird.adapter = ServiceListAdapter(mContext = requireContext(), it)

        }


        _binding.btnUserList.setOnClickListener {
            var intent = Intent(requireContext(),UsersActivity::class.java)
            requireContext().startActivity(intent)
        }

        return _binding.root
    }
}