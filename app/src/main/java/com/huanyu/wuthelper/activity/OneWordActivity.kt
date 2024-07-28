package com.huanyu.wuthelper.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.adapter.DianFeeListAdapter
import com.huanyu.wuthelper.adapter.OneWordListAdapter
import com.huanyu.wuthelper.databinding.ActivityDianFeeBinding
import com.huanyu.wuthelper.databinding.ActivityOneWordBinding

class OneWordActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityOneWordBinding
    private lateinit var viewModel: OneWordViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOneWordBinding.inflate(layoutInflater)
        viewModel= ViewModelProvider(this)[OneWordViewModel::class.java]
        setContentView(_binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.getOneWordList()
        _binding.oneWordRecyclerView.layoutManager = LinearLayoutManager(this)
        var adpter = OneWordListAdapter(this,null)
        _binding.oneWordRecyclerView.adapter = adpter
        viewModel.oneWordList.observe(this){
            adpter.updateData(it)
        }
    }
}