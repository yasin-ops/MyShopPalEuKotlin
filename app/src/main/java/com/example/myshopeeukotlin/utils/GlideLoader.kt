package com.example.myshopeeukotlin.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.myshopeeukotlin.R
import java.io.IOException

class GlideLoader(val context:Context) {
    //imageUri:Uri
    fun loadUserPicture(image:Any ,imageView: ImageView){
        try{
            // Load the User image in the ImageView
            Glide
                .with(context)
                .load(Uri.parse(image.toString())) // URI of the Image
                .centerCrop() // Scale Type of the Image
                .placeholder(R.drawable.shop ) // A default place holder if the image is failed to Load
                .into(imageView) // the view in which the image will be loaded
        }
        catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image:Any ,imageView: ImageView){
        try{
            // Load the User image in the ImageView
            Glide
                .with(context)
                .load(Uri.parse(image.toString()))
                .centerCrop()
               // .placeholder(R.drawable.shop )
                .into(imageView)
        }
        catch (e:IOException){
            e.printStackTrace()
        }
    }
}