package com.huanyu.wuthelper.adapter

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.entity.CourseInfo
import com.huanyu.wuthelper.entity.LocationEntity
import com.huanyu.wuthelper.utils.CustomUIs.Companion.myAlertDialog


class CourseListAdapter(val context:Context, var itemList: List<CourseInfo>?):RecyclerView.Adapter<CourseListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseListAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_courselistitem, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList?.size?:0
    }

    override fun onBindViewHolder(holder: CourseListAdapter.ViewHolder, position: Int) {
        itemList?.let {
            var courseDetailList = it
            val item: CourseInfo = courseDetailList[position]
            holder.className.setText(item.name)
            holder.classTeacher.setText(item.teacher)
            holder.classTime.setText(item.time)
            holder.classInfo.setText("学分:"+item.credit + " "+item.note)
            holder.cardView.setOnClickListener {
                myAlertDialog(context,item.name,"老师：${item.teacher}\n地点：${item.room}\n时间：${item.time}\n学分：${item.credit}\n备注：${item.note}\n")
            }

        }

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var className: TextView = itemView.findViewById<TextView>(R.id.className)
        var classTeacher: TextView = itemView.findViewById<TextView>(R.id.classTeacher)
        var classTime: TextView = itemView.findViewById<TextView>(R.id.classTime)
        var classInfo: TextView = itemView.findViewById<TextView>(R.id.classInfo)
        var cardView:CardView = itemView.findViewById(R.id.card_class)
    }

    private fun showDialog() {
        // 创建AlertDialog
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Information")
            .setMessage("This is some important information. Please choose an option.")
            .setPositiveButton(
                "OK",
                DialogInterface.OnClickListener { dialog, which -> // 用户点击OK按钮后的处理
                    Toast.makeText(context, "You clicked OK", Toast.LENGTH_SHORT).show()
                })
            .setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which -> // 用户点击Cancel按钮后的处理
                    Toast.makeText(context, "You clicked Cancel", Toast.LENGTH_SHORT)
                        .show()
                })

        // 显示对话框
        builder.create().show()
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
        // 显示对话框
        builder.create().show()



    }
    fun updateData(items: List<CourseInfo>?) {
        this.itemList = items!!
        notifyDataSetChanged()
    }
}