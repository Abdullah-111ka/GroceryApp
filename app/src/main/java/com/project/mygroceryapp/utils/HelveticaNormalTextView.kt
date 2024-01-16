package com.project.mygroceryapp.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class TextView(context : Context, attrs : AttributeSet) : AppCompatTextView(context, attrs) {

    init{
        applyFont()
    }

    private fun applyFont() {
        val typeface = Typeface.createFromAsset(context.assets, "helvetica_normal.ttf")
        setTypeface(typeface)
    }
}