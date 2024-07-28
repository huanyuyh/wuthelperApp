package com.huanyu.wuthelper.fragment

import android.Manifest
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.amap.api.services.core.ServiceSettings
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.activity.SearchActivity
import com.huanyu.wuthelper.databinding.FragmentHomeBinding
import com.huanyu.wuthelper.databinding.FragmentNaviBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class NaviFragment : Fragment() {

    companion object {
        private const val LOG_NaviFragment = "NaviFragment:"
        fun newInstance() = NaviFragment()
        //请求权限码
        private const val REQUEST_PERMISSIONS = 9527
    }

    private val viewModel: NaviViewModel by viewModels()
    lateinit var binding: FragmentNaviBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("navifragment","onCreateView")
        binding = FragmentNaviBinding.inflate(layoutInflater, container, false)
        //隐私合规效验
        ServiceSettings.updatePrivacyShow(requireContext(), true, true)
        ServiceSettings.updatePrivacyAgree(requireContext(), true)
        binding.mapView?.onCreate(savedInstanceState)
        binding.fabShow.setOnClickListener {
            Log.d("buttonclick","fab")
            viewModel.showWutMarker()
        }
        binding.fabMyposition.setOnClickListener{
            viewModel.seeMyPosition()
        }
        binding.fabSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }
        viewModel.prepareDate()
        viewModel.initLocation()
        checkingAndroidVersion()
        viewModel.initMap(mapView = binding.mapView, context = requireContext())

        viewModel.getAddress().observe(viewLifecycleOwner) {

            binding.nowPos.text = it
        }
//        viewModel.getWeather().observe(viewLifecycleOwner){
//            binding.tvTop.text = it
//        }
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        Log.d("navifragment","onStart")
    }
    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
        Log.d("navifragment","onResume")
        viewModel.mLocationClient?.startLocation() //启动定位
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
        Log.d("navifragment","onPause")
        viewModel.mLocationClient?.stopLocation()

    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView?.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }
    /**
     * 检查Android版本
     */
    private fun checkingAndroidVersion() {
        //Android6.0及以上先获取权限再定位
        requestPermission()

    }

    /**
     * 动态请求权限
     */
    @AfterPermissionGranted(REQUEST_PERMISSIONS)
    private fun requestPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (EasyPermissions.hasPermissions(requireContext(), *permissions)) {
            //true 有权限 开始定位
            viewModel.mLocationClient?.let {
                showMsg("已获得权限，可以定位啦！")
                it.startLocation()
            }

        } else {
            //false 无权限
            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, *permissions)
        }
    }

    /**
     * 请求权限结果
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //设置权限请求结果
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    /**
     * Toast提示
     * @param msg 提示内容
     */
    private fun showMsg(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

}