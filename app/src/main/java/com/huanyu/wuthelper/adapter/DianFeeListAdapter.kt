package com.huanyu.wuthelper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.DianFee

class DianFeeListAdapter(val context:Context, var itemList: List<DianFee>?):RecyclerView.Adapter<DianFeeListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DianFeeListAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.dianfee_card, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList?.size?:0
    }

    override fun onBindViewHolder(holder: DianFeeListAdapter.ViewHolder, position: Int) {
        itemList?.let {courseDetailList->
            val item: DianFee = courseDetailList[position]
            holder.areaName.text = item.areaName
            holder.dianfee.text = item.dianfee+"元"
            holder.dianfeeTime.text = item.time
            holder.dianfeeDue.text = item.due+"度"

        }

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var areaName: TextView = itemView.findViewById<TextView>(R.id.areaName)
        var dianfee: TextView = itemView.findViewById<TextView>(R.id.dianfee)
        var dianfeeTime: TextView = itemView.findViewById<TextView>(R.id.dianfeeTime)
        var dianfeeDue: TextView = itemView.findViewById<TextView>(R.id.dianfeeDue)
        var cardView:ConstraintLayout = itemView.findViewById(R.id.dianfeeCardLayout)
    }



    private fun showCustomDialog(courseDetail: CourseInfo) {
        // 创建AlertDialog
        val builder = AlertDialog.Builder(context)

        // 获取自定义对话框布局
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.pop_position, null)
        builder.setView(dialogView)

        // 获取TextView和Button
        val textViewTitle = dialogView.findViewById<TextView>(R.id.text_view_title)
        val textViewDesc = dialogView.findViewById<TextView>(R.id.text_view_desc)
        val buttonGo = dialogView.findViewById<Button>(R.id.button_go)
        val buttonClose = dialogView.findViewById<Button>(R.id.button_close)

        // 设置TextView的文本
        textViewTitle.text = courseDetail.name
        textViewDesc.text =
            courseDetail.teacher + "\n" + courseDetail.room + "\n" + courseDetail.time + "\n" + courseDetail.credit + "\n" + courseDetail.note



    }
    fun updateData(items: List<DianFee>?) {
        this.itemList = items!!
        notifyDataSetChanged()
    }
}