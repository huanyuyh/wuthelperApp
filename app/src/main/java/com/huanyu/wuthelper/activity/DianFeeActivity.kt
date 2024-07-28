package com.huanyu.wuthelper.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.huanyu.wuthelper.activity.DianFeeViewModel
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.adapter.DianFeeListAdapter
import com.huanyu.wuthelper.databinding.ActivityDianFeeBinding

class DianFeeActivity : AppCompatActivity() {
    private lateinit var _binding:ActivityDianFeeBinding
    private lateinit var dianFeeViewModel: DianFeeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        _binding = ActivityDianFeeBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        dianFeeViewModel = ViewModelProvider(this)[DianFeeViewModel::class.java]
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dianFeeViewModel.getDianfeeList()

        _binding.dianfeeRecyclerView.layoutManager = LinearLayoutManager(this)
        var adpter = DianFeeListAdapter(this,null)
        _binding.dianfeeRecyclerView.adapter = adpter
        dianFeeViewModel.dianfeeList.observe(this){
            adpter.updateData(it)
        }
        dianFeeViewModel.dianfeeSpeed.observe(this){
            _binding.percentFeeText.text = String.format("%.2f",it.electricFeeSpeed )+"元/天"
            _binding.percentDueText.text = String.format("%.2f", it.electricityUsageSpeed)+"度/天"
        }
    }
}