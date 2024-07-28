package com.huanyu.wuthelper.adapter

import android.app.Activity
import android.content.DialogInterface
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Poi
import com.amap.api.navi.AmapNaviPage
import com.amap.api.navi.AmapNaviParams
import com.amap.api.navi.AmapNaviType
import com.amap.api.navi.AmapPageType
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.entity.CourseTask
import com.huanyu.wuthelper.entity.LocationEntity
import com.huanyu.wuthelper.utils.CustomDataUtils.Companion.calculateTimeDifference


class TaskListAdapter(val context:Activity, var itemList: List<CourseTask>?):RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListAdapter.ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.course_taskcard, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList?.size?:0
    }

    override fun onBindViewHolder(holder: TaskListAdapter.ViewHolder, position: Int) {
        itemList?.let {courseTaskList->
            val item: CourseTask = courseTaskList[position]
            // 获取屏幕宽度
            val displayMetrics = DisplayMetrics()

            context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val screenWidth = displayMetrics.widthPixels
            // 计算maxWidth，例如屏幕宽度的80%
            val maxWidth = (screenWidth * 0.60).toInt()
            // 设置maxWidth
            holder.courseName.maxWidth = maxWidth
            holder.courseName.text = item.name
            holder.coursePlat.text =item.platform
            if (item.platform.contains("mooc")){
                holder.coursePlat.setBackgroundResource(R.drawable.taskcardbg_green)
            }
            holder.courseDesc.text =item.group_name + " " +item.end_time
            val duration = calculateTimeDifference(item.end_time)
            val hours = duration.toHours()
            if(hours>72){
                holder.courseIng.text = "大于三天"
                holder.courseIng.setBackgroundResource(R.drawable.taskcardbg_lightgreen)
            } else if(hours>24){
                holder.courseIng.text = "小于三天"
                holder.courseIng.setBackgroundResource(R.drawable.taskcardbg_lightyellow)
            }else if(hours>1) {
                holder.courseIng.text = hours.toString()+"小时"
                holder.courseIng.setTextColor(context.resources.getColor(R.color.white))
                holder.courseIng.setBackgroundResource(R.drawable.taskcardbg_lightred)
            }else if(hours>0){
                holder.courseIng.text = duration.toMinutes().toString()+"分钟"
                holder.courseIng.setBackgroundResource(R.drawable.taskcardbg_lightred)
            }else{
                holder.courseIng.text = "已截止"
                holder.courseIng.setBackgroundResource(R.drawable.taskcardbg_lightred)
            }
        }

    }
    class ViewHolder(cardView: View) : RecyclerView.ViewHolder(cardView) {
        var courseName = cardView.findViewById<TextView>(R.id.courseName)
        var coursePlat = cardView.findViewById<TextView>(R.id.coursePlat)
        var courseDesc = cardView.findViewById<TextView>(R.id.courseDesc)
        var courseIng = cardView.findViewById<TextView>(R.id.courseIng)
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

    private fun showCustomDialog(locationEntity: LocationEntity) {
        // 创建AlertDialog
        val builder = AlertDialog.Builder(context)

        // 获取自定义对话框布局
        val inflater: LayoutInflater =LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.pop_position, null)
        builder.setView(dialogView)

        // 获取TextView和Button
        val textViewTitle = dialogView.findViewById<TextView>(R.id.text_view_title)
        val textViewDesc = dialogView.findViewById<TextView>(R.id.text_view_desc)
        val buttonGo = dialogView.findViewById<Button>(R.id.button_go)
        val buttonClose = dialogView.findViewById<Button>(R.id.button_close)

        // 设置TextView的文本
        textViewTitle.text = locationEntity.name
        textViewDesc.text = locationEntity.aliases.toString()

        // 显示对话框
        val dialog = builder.create()
        // 设置Button的点击事件
        buttonGo.setOnClickListener {
            val latLng = LatLng(locationEntity.latitude,locationEntity.longitude)
            val end = Poi(null, latLng, null)
            // 组件参数配置
            val params = AmapNaviParams(null, null, end, AmapNaviType.WALK, AmapPageType.ROUTE)
            params.setMultipleRouteNaviMode(true)
            params.setShowVoiceSetings(true)
            params.setTrafficEnabled(true)
            val aMapNavi = AmapNaviPage.getInstance()
            // 启动组件
            aMapNavi.showRouteActivity(context, params, null)
            dialog.dismiss()
        }
        buttonClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
    fun updateData(items: List<CourseTask>?) {
        this.itemList = items!!
        notifyDataSetChanged()
    }
}