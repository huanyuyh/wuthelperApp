package com.huanyu.wuthelper.fragment

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.activity.DianFeeActivity
import com.huanyu.wuthelper.activity.DianFeiWebActivity
import com.huanyu.wuthelper.activity.MoocWebActivity
import com.huanyu.wuthelper.activity.TaskActivity
import com.huanyu.wuthelper.activity.ToolWebActivity
import com.huanyu.wuthelper.activity.XiaoYaWebActivity
import com.huanyu.wuthelper.databinding.FragmentMyBinding
import com.huanyu.wuthelper.utils.FileUtil.Companion.getFilesInDirectory
import com.huanyu.wuthelper.utils.SPTools.Companion.getisXiaoLiHave
import com.huanyu.wuthelper.utils.SPTools.Companion.putisXiaoLiHave
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File

class MyFragment : Fragment() {
    companion object {
        private const val LOG_MyFragment = "MyFragment:"
        fun newInstance() = MyFragment()
    }

    private val viewModel: MyViewModel by viewModels()
    lateinit var _binding: FragmentMyBinding

    lateinit var wifiName:String
    lateinit var wifiPass:String
    private lateinit var wifiNasId:String
    var areaSelectPos = 0
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        Log.d( LOG_MyFragment+"ActivityResult","ActivityResult")
        if (result.resultCode == Activity.RESULT_OK) {
            // 处理返回结果
            val data: Intent? = result.data
            val returnValue = data?.getStringExtra("key")
            Log.d( LOG_MyFragment+"difeiReturn",returnValue?:"null")
            if(returnValue=="finish") {
                Log.d(LOG_MyFragment+"difeiReturn", returnValue )
                viewModel.dianFeiReturn(areaSelectPos)
            }
            // 使用data中的信息
        }
    }



    override fun onResume() {
        Log.d( LOG_MyFragment+"onResume","onResume")
        viewModel.getWifiUser()
        viewModel.initDianFei()
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d( LOG_MyFragment+"onCreate","onCreate")
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d( LOG_MyFragment+"onCreateView","onCreateView")
        _binding = FragmentMyBinding.inflate(layoutInflater, container, false)

        viewModel.areas.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"areas.observe","areas.observe")
            val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,it)
            _binding.AreaSelect.adapter = adapter
        }
        viewModel.builds.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"builds.observe","builds.observe")
            val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,it)
            _binding.BuildSelect.adapter = adapter
        }
        viewModel.floors.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"floors.observe","floors.observe")
            val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,it)
            _binding.FloorSelect.adapter = adapter
        }
        viewModel.rooms.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"rooms.observe","rooms.observe")
            val adapter = ArrayAdapter(requireContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,it)
            _binding.RoomSelect.adapter = adapter
        }
        viewModel.dFStr.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"DFStr.observe","DFStr.observe")
            _binding.dianfeiTitle.text = it
            _binding.dianfeiTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20F)
        }
        _binding.AreaSelect.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d( LOG_MyFragment+"AreaSelect","onItemSelected")
                areaSelectPos = position
                viewModel.getBuildList(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        _binding.BuildSelect.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d( LOG_MyFragment+"BuildSelect","onItemSelected")
                viewModel.getFloorList(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        _binding.FloorSelect.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d( LOG_MyFragment+"FloorSelect","onItemSelected")
                viewModel.getRoomList(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
        _binding.RoomSelect.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d( LOG_MyFragment+"RoomSelect","onItemSelected")
                viewModel.getDianFeiInfo(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }


        setWifiLogin()
        _binding.dianfeiBtn.setOnClickListener {
            Log.d( LOG_MyFragment+"dianfeiBtn.setOnClick","dianfeiBtn.setOnClick")
            val intent = Intent(requireContext(), DianFeiWebActivity::class.java)
            getResult.launch(intent)
        }
        _binding.savedianfeiBtn.setOnClickListener {
            Log.d( LOG_MyFragment+"savedianfeiBtn.setOnClick","savedianfeiBtn.setOnClick")
            viewModel.saveDFInfo(requireActivity(),_binding.AreaSelect.selectedItemPosition,
                _binding.BuildSelect.selectedItemPosition,_binding.FloorSelect.selectedItemPosition,
                _binding.RoomSelect.selectedItemPosition
            )
        }
        _binding.showdianfeiBtn.setOnClickListener {
            val intent = Intent(requireContext(), DianFeeActivity::class.java)
            startActivity(intent)
        }
        viewModel.areaselect.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"areaselect.observe","areaselect.observe")
            _binding.AreaSelect.setSelection(it)
        }
        viewModel.buildselect.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"buildselect.observe","buildselect.observe")
            _binding.BuildSelect.setSelection(it)
        }
        viewModel.floorselect.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"floorselect.observe","floorselect.observe")
            _binding.FloorSelect.setSelection(it)
        }
        viewModel.roomselect.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"roomselect.observe","roomselect.observe")
            _binding.RoomSelect.setSelection(it)
        }

        viewModel.wifiName.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"wifiName.observe","wifiName.observe")
            _binding.editWifiName.setText(it)
        }
        viewModel.wifiPass.observe(viewLifecycleOwner){
            Log.d( LOG_MyFragment+"wifiPass.observe","wifiPass.observe")
            _binding.editWifiPass.setText(it)
        }

        setXiaoli()

        _binding.showTaskbutton.setOnClickListener {
            Log.d( LOG_MyFragment+"showTaskbutton.setOnClick","showTaskbutton.setOnClick")
            val intent = Intent(requireContext(),TaskActivity::class.java)
            startActivity(intent)
        }
        _binding.xiaoyatestbutton.setOnClickListener {
            Log.d( LOG_MyFragment+"showTaskbutton.setOnClick","showTaskbutton.setOnClick")
            val intent = Intent(requireContext(),XiaoYaWebActivity::class.java)
            startActivity(intent)
        }
        _binding.mooctestbutton.setOnClickListener {
            Log.d( LOG_MyFragment+"showTaskbutton.setOnClick","showTaskbutton.setOnClick")
            val intent = Intent(requireContext(),MoocWebActivity::class.java)
            startActivity(intent)
        }
        return _binding.root
    }

    private fun setXiaoli() {
        Log.d( LOG_MyFragment+"setXiaoli","setXiaoli")
        val isXiaoLiHave = getisXiaoLiHave(requireContext())
        if(isXiaoLiHave){
            Log.d(LOG_MyFragment+"isXiaoLiHave","show")
            val fileList: List<File>? = requireContext().externalCacheDir?.path?.let {
                getFilesInDirectory(
                    it
                )
            }
            fileList?.forEach {
                if(it.name.contains("校历")){
                    val file = it
                    _binding.xiaoliIv.setOnClickListener {
                        showPhotoPop(file)
                    }
                    val width = resources.displayMetrics.widthPixels
                    Glide.with(requireContext())
                        .load(it)
                        .override(width) // 使用 override 设置宽度为原始大小，高度为 ImageView 的高度
                        .placeholder(R.drawable.baseline_downloading_24)//图片加载出来前，显示的图片
                        .error(R.drawable.baseline_error_outline_24)//图片加载失败后，显示的图片
                        .into(_binding.xiaoliIv)
                }
            }
        }else{
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d(LOG_MyFragment+"setXiaoliElse","ds")
                viewModel.downloadXiaoli(requireContext(), onSuccess = {
                    val file = it
                    Log.d(LOG_MyFragment+"setXiaoliElse",file.name)
                    putisXiaoLiHave(requireContext(),true)
                    val width = resources.displayMetrics.widthPixels
                    lifecycleScope.launch{
                        _binding.xiaoliIv.setOnClickListener {
                            showPhotoPop(file)
                            Log.d(LOG_MyFragment+"xiaoliIv.setOnClick","show")
                        }
                        Glide.with(requireContext())
                            .load(it)
                            .override(width) // 使用 override 设置宽度为原始大小，高度为 ImageView 的高度
                            .placeholder(R.drawable.baseline_downloading_24)//图片加载出来前，显示的图片
                            .error(R.drawable.baseline_error_outline_24)//图片加载失败后，显示的图片
                            .into(_binding.xiaoliIv)
                    }
                })

            }
        }
        _binding.xzxiaoliTv.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                Log.d(LOG_MyFragment+"xzxiaoliTv.setOnClick","ds")
                viewModel.downloadXiaoli(requireContext(), onSuccess = {
                    val file = it
                    Log.d(LOG_MyFragment+"xzxiaoliTv.setOnClick",file.name)
                    putisXiaoLiHave(requireContext(),true)
                    val width = resources.displayMetrics.widthPixels
                    lifecycleScope.launch{
                        _binding.xiaoliIv.setOnClickListener {
                            showPhotoPop(file)
                            Log.d(LOG_MyFragment+"xzxiaoliTv.setOnClick","xiaoliIv.setOnClickshow")
                        }
                        Glide.with(requireContext())
                            .load(it)
                            .override(width) // 使用 override 设置宽度为原始大小，高度为 ImageView 的高度
                            .placeholder(R.drawable.baseline_downloading_24)//图片加载出来前，显示的图片
                            .error(R.drawable.baseline_error_outline_24)//图片加载失败后，显示的图片
                            .into(_binding.xiaoliIv)
                    }
                })

            }
        }
    }
    private fun showPhotoPop(file: File) {
        Log.d(LOG_MyFragment+"sshowPhotoPop","showPhotoPop")
        // 初始化PopupWindow
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_photo_see, null)
        val btnclose: ImageButton = popupView.findViewById(R.id.photoClose)
        val photo: PhotoView = popupView.findViewById(R.id.popPhotoView)
        val textView: TextView = popupView.findViewById(R.id.photoTitle)
        textView.text = file.name
        val popupWindow = PopupWindow(popupView,
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT,true)
        photo.setOnClickListener {
            Log.d(LOG_MyFragment+"sshowPhotoPop","photo.setOnClick")
            popupWindow.dismiss()
        }
        btnclose.setOnClickListener {
            Log.d(LOG_MyFragment+"sshowPhotoPop","btnclose.setOnClick")
            popupWindow.dismiss()
        }
// 显示PopupWindow
        val width = resources.displayMetrics.widthPixels
        Glide.with(requireContext())
            .load(file)
            .override(width)
            .fitCenter()
            .placeholder(R.drawable.baseline_downloading_24)//图片加载出来前，显示的图片
            .error(R.drawable.baseline_error_outline_24)//图片加载失败后，显示的图片
            .into(photo)
        val rootview = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_home, null)
        dimBackground(0.5f) // 0.5f 表示半透明
        popupWindow.showAtLocation(rootview, Gravity.CENTER,0,0) // anchorView是触发PopupWindow显示的视图
        popupWindow.setOnDismissListener {
            dimBackground(1.0f)
        }

    }
    private fun dimBackground(dimAmount: Float){
        Log.d(LOG_MyFragment+"dimBackground",dimAmount.toString())
        val lp: WindowManager.LayoutParams = requireActivity().window.attributes
        lp.alpha = dimAmount
        requireActivity().window.attributes = lp
    }
    private fun setWifiLogin(){
        Log.d(LOG_MyFragment+"setWifiLogin","setWifiLogin")
        _binding.editWifiName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wifiName = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        _binding.editWifiPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wifiPass = s.toString()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        _binding.webloginBtn.setOnClickListener {
            Log.d(LOG_MyFragment+"webloginBtn.setOnClick","webloginBtn.setOnClick")
            val intent = Intent(requireContext(), ToolWebActivity::class.java)
            intent.putExtra("wifiUser",wifiName)
            Log.d("wifilogin",wifiName+wifiPass)
            intent.putExtra("wifiPass",wifiPass)

            intent.putExtra("wifiUrl","http://1.1.1.1")
            requireContext().startActivity(intent)
        }

        _binding.savewifiBtn.setOnClickListener {
            Log.d(LOG_MyFragment+"savewifiBtn.setOnClick","savewifiBtn.setOnClick")
            viewModel.saveWifiUser(wifiName,wifiPass)
        }

        _binding.loginBtn.setOnClickListener {
            Log.d(LOG_MyFragment+"loginBtn.setOnClick","loginBtn.setOnClick")
            viewModel.getWifiNasId (onSuccess = {
                wifiNasId = it
                viewModel.handleLogin(wifiName,wifiPass,it, onSuccess = { data ->
                    // 更新UI，显示数据
                    Log.d(LOG_MyFragment+"wifi",data)
                    val jsonObject = JSONObject(data)
                    val msg = jsonObject.getString("authMsg")
                    Log.d(LOG_MyFragment+"wifimsg",msg)
                    lifecycleScope.launch {
                        Toast.makeText(requireContext(),msg, Toast.LENGTH_SHORT).show()
                    }
                }, onError = {
                    // 显示错误信息
                    lifecycleScope.launch {
                    }
                })
            }, onInternet = {
                lifecycleScope.launch {
                    Toast.makeText(requireContext(),"网络通畅", Toast.LENGTH_SHORT).show()
                }
            }, onError = {
                lifecycleScope.launch {
                    Toast.makeText(requireContext(),"貌似没有链接wifi(或需关闭代理服务)", Toast.LENGTH_SHORT).show()
                }
            })
//
        }
    }

}