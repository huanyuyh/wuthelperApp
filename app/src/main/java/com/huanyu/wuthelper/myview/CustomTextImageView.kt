package com.huanyu.wuthelper.myview

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.huanyu.wuthelper.R

class CustomTextImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr){
    private val imageView: AppCompatImageView
    private val textView: TextView
    private var isSelectedCustom: Boolean = false
    init {
        LayoutInflater.from(context).inflate(R.layout.mytextimageview, this, true)
        imageView = findViewById(R.id.viewimage)
        textView = findViewById(R.id.viewtext)

        // Read custom attributes
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CustomTextImageView, 0, 0)
            val text = typedArray.getString(R.styleable.CustomTextImageView_TextImageText)
            val imageResId = typedArray.getResourceId(R.styleable.CustomTextImageView_TextImageImageSrc, -1)
            isSelectedCustom = typedArray.getBoolean(R.styleable.CustomTextImageView_TextImageSelected, false)
            setText(text)
            if (imageResId != -1) {
                setImageResource(imageResId)
            }
            typedArray.recycle()
            // 添加波纹效果作为foreground
            foreground = ContextCompat.getDrawable(context, R.drawable.my_text_image_ripple_effect)

        }
        updateBackground()

    }
    fun setSelectedCustom(selected: Boolean) {
        isSelectedCustom = selected
        updateBackground()
    }

    override fun isSelected(): Boolean {
        return isSelectedCustom
    }
    override fun setSelected(selected: Boolean) {
        super.setSelected(selected)
        setSelectedCustom(selected)
    }

    private fun updateBackground() {
//        if (isSelectedCustom) {
//            textView.setTextColor(resources.getColor(R.color.naviSelectPink,context.theme))
//        } else {
//            textView.setTextColor(resources.getColor(R.color.naviNoSelectPink,context.theme))
//        }
    }


    fun setImageResource(resId: Int) {
        imageView.setImageResource(resId)
    }
    fun setText(text: String?) {
        textView.text = text
    }
}