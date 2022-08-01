package com.example.myshopeeukotlin.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat

object Constants {

    // Collections in Cloud Firestore
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"


    // Start
    // Using SharedPreference To Store limited amount of Data
    const val MYSHOPPAL_PREFERENCES: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    //End
    const val EXTRA_USER_DETAILS: String = "extra_user_detail"

    const val READ_STORAGE_PERMISSION_CODE = 2
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val MALE: String = "male"
    const val FEMALE: String = "female"

    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val PRODUCT_IMAGE: String = "Product_Image"
    const val USER_ID: String = "user_id"

    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_ITEM: String = "cart_Item"
    const val PRODUCT_ID: String = "product_id"

    const val CART_QUANTITY: String = "cart_quantity"

    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Others"

    const val ADDRESSES: String = "addresses"

    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"

    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    const val Orders: String = "orders"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"

    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"

    const val STOCK_QUANTITY: String = "stock_quantity"

    const val EXTRA_MY_ORDER_DETAILS:String="extra_MY_Order_Details"


    /*
        fun showImageChooser(activity: Activity) {

    //    // An intent for launching the image selection of phone storage
    //        val galleryIntent = Intent()
    //        galleryIntent.setType("image/*");
    //        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
    //        // Launches the image selection of phone storage using the constant code
          // activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)

            val galleryIntent=Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            // Launches the image selection of phone storage using the constant code
            activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)


        }*/

     */
    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        /*
        MimeTypeMap: two way that maps Mime-types to file extension and vice versa
        getSingleton():Get the singleton instance of MimeTypeMap
        getExtensionFromMimeType:Return the registered extension for the given Mime type
        contentResolver.getType:Return the Mime Type of the given content URL
*/

        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }


}