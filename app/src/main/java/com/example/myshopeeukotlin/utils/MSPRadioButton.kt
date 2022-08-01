package com.example.myshopeeukotlin.utils

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatRadioButton

class MSPRadioButton(context: Context,attrs: AttributeSet) :AppCompatRadioButton(context,attrs) {
    init {
        applyFont()
    }
    private fun applyFont() {

        //  This is used to get the file from the assest folder and set it to the title textView
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "MachaelaDemoRegular.ttf")
        setTypeface(typeface)

    }

}