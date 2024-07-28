package com.huanyu.wuthelper.adapterr

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.huanyu.wuthelper.R
import com.huanyu.wuthelper.activity.WebActivity
import com.huanyu.wuthelper.entity.Platform
import com.huanyu.wuthelper.entity.User


class ServiceListAdapter(private var mContext: Context, private var mPlatList: List<Platform>): BaseAdapter() {
    override fun getCount(): Int {
        return mPlatList.size

    }

    override fun getItem(position: Int): Platform {
        return mPlatList.get(position)

    }

    override fun getItemId(position: Int): Long {
        return mPlatList.get(position)._id.toLong()

    }
    fun getTextColorForBackground(backgroundColor: Int): Int {
        val brightness = ((Color.red(backgroundColor) * 299) + (Color.green(backgroundColor) * 587) + (Color.blue(backgroundColor) * 114)) / 1000

        return if (brightness >= 128) {
            Color.BLACK // 背景较亮，选择黑色文字
        } else {
            Color.WHITE // 背景较暗，选择白色文字
        }
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(mContext).inflate(R.layout.item_service, parent, false)
        val cardView: CardView = view.findViewById(R.id.passcardView)
        val textView: TextView = view.findViewById(R.id.platformTv)
        textView.text = mPlatList.get(position).platName
        // 获取原始的可绘制对象
        val originalDrawable: Drawable? = ContextCompat.getDrawable(mContext, R.drawable.cardbg)

        // 设置颜色过滤器
        val color = Color.parseColor(mPlatList.get(position).color) // 你想要的颜色
        originalDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        cardView.setBackgroundDrawable(originalDrawable)
        val textColor = getTextColorForBackground(color)
        textView.setTextColor(textColor)
        cardView.setOnClickListener {
            val intent = Intent(mContext, WebActivity::class.java)
            val bundle = Bundle()
            bundle.putString("platform",mPlatList.get(position).platName?:"智慧理工大")
            bundle.putString("action","open")
            intent.putExtra("bundle",bundle)
            mContext.startActivity(intent)
        }
        return view
    }
    fun updateData(items: List<Platform>?) {
        this.mPlatList = items!!
        notifyDataSetChanged()
    }

}