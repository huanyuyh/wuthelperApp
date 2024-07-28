package com.huanyu.wuthelper.myview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.huanyu.wuthelper.R

class CustomCellView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){
    private val imageView: AppCompatImageView
    private val textView: TextView
    init {
        LayoutInflater.from(context).inflate(R.layout.mylistcell, this, true)
        imageView = findViewById(R.id.cardimage)
        textView = findViewById(R.id.cardtext)

        // Read custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomCellView, 0, 0)
            val text = typedArray.getString(R.styleable.CustomCellView_text)
            val imageResId = typedArray.getResourceId(R.styleable.CustomCellView_imageSrc, -1)
            setText(text)
            if (imageResId != -1) {
                setImageResource(imageResId)
            }
            typedArray.recycle()

        }

    }


    fun setImageResource(resId: Int) {
        imageView.setImageResource(resId)
    }
    fun setText(text: String?) {
        textView.text = text
    }
}