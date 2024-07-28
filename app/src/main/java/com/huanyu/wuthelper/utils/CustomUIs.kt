package com.huanyu.wuthelper.utils

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.database.CoursesDatabase
import com.huanyu.wuthelper.entity.Course

class CustomUIs {
    companion object{
        fun myCourseAlertDialog(context: Context,time:String,course: Course){
            val dialogView = LayoutInflater.from(context).inflate(R.layout.course_info_dialog, null)
            val textTitle = dialogView.findViewById<TextView>(R.id.tvTitle)
            val textTeacher = dialogView.findViewById<TextView>(R.id.tvTeacher)
            val textPosition = dialogView.findViewById<TextView>(R.id.tvPosition)
            val textTime = dialogView.findViewById<TextView>(R.id.tvTime)
            val textNote = dialogView.findViewById<TextView>(R.id.tvNote)

            textTime.text = "时间:${time}"
            textTitle.text = course.name
            textTeacher.text = "老师:${course.teacher}"
            textPosition.text = "地点:${course.room}"
            textNote.text = "学分: ${course.credit} \n备注:${course.note}"

            val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnClose).setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
        fun myAlertDialog(context: Context,title:String,msg:String,onClick:()->Unit = {}){
            val customLayout = LinearLayout.inflate(context,R.layout.custom_alert_dialog, null)
            val customTitle = customLayout.findViewById<TextView>(R.id.custom_alert_title)
            val customMessage = customLayout.findViewById<TextView>(R.id.custom_alert_message)
            val customButtonOk = customLayout.findViewById<Button>(R.id.custom_alert_button_ok)
            val customButtonCancel = customLayout.findViewById<Button>(R.id.custom_alert_button_cancel)
            customTitle.text = title
            customMessage.text = msg
            val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setView(customLayout)
                .create()
            customButtonOk.setOnClickListener {
                dialog.dismiss()
                onClick()
            }
            customButtonCancel.visibility = View.GONE

            dialog.show()
        }
        fun myAlertDialogWithTwoBtn(context: Context,title:String,msg:String,btnOk:String,btnCancel:String,onOkClick:()->Unit = {},onCancelClick: () -> Unit = {}){
            val customLayout = LinearLayout.inflate(context,R.layout.custom_alert_dialog, null)
            val customTitle = customLayout.findViewById<TextView>(R.id.custom_alert_title)
            val customMessage = customLayout.findViewById<TextView>(R.id.custom_alert_message)
            val customButtonOk = customLayout.findViewById<Button>(R.id.custom_alert_button_ok)
            val customButtonCancel = customLayout.findViewById<Button>(R.id.custom_alert_button_cancel)
            customTitle.text = title
            customMessage.text = msg
            customButtonOk.text = btnOk
            customButtonCancel.text = btnCancel
            val dialog = AlertDialog.Builder(context, R.style.CustomAlertDialog)
                .setView(customLayout)
                .create()
            customButtonOk.setOnClickListener {
                dialog.dismiss()
                onOkClick()
            }
            customButtonCancel.setOnClickListener {
                dialog.dismiss()
                onCancelClick()
            }

            dialog.show()
        }
        private const val CHANNEL_ID = "wuthelper"
        private const val CHANNEL_NAME = "wuthelper"
        fun myNotification(context: Context, title: String, msg: String) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
                Notification.Builder(context, CHANNEL_ID)
            } else {
                Notification.Builder(context)
            }

            builder.setSmallIcon(R.drawable.appicon) // 设置通知的小图标，这里需要替换成你的图标资源
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(Notification.PRIORITY_DEFAULT)

            notificationManager.notify(1, builder.build())
        }
        fun showNotificationWithActivity(context: Context, title: String, msg: String, targetActivity: Class<*>) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val intent = Intent(context, targetActivity)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
                Notification.Builder(context, CHANNEL_ID)
            } else {
                Notification.Builder(context)
            }

            builder.setSmallIcon(R.drawable.appicon) // 设置通知的小图标，这里需要替换成你的图标资源
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true) // 点击通知后自动取消

            notificationManager.notify(1, builder.build())
        }
    }
}