package com.example.myshopeeukotlin.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityAddProductBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.Product
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityAddProductBinding
    var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()
        binding.ivAddUpdateProduct.setOnClickListener(this)
        binding.btnSubmitAddProduct.setOnClickListener(this)


    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddProductActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)

        }
        binding.toolbarAddProductActivity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_update_product -> {
                    pickPhoto()

                }
                R.id.btn_submit_add_product -> {
                    if (validateProductsDetails()) {
                        // showErrorSnackBar("The product details are valid ", false)
                        uploadProductImage()
                    }
                }
            }
        }
    }

    private fun uploadProductImage() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(
            this,
            mSelectedImageFileUri,
            Constants.PRODUCT_IMAGE
        )
    }


    fun pickPhoto() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) { // izin alınmadıysa
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE
            )
        } else {
            val galeriIntext =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galeriIntext, Constants.PICK_IMAGE_REQUEST_CODE)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            mSelectedImageFileUri = data.data
            if (mSelectedImageFileUri != null) {
                binding.ivAddUpdateProduct.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_vector_edit_
                    )
                )
                if (Build.VERSION.SDK_INT >= 28) {


                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivProduct)
                } else {

                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivProduct)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun validateProductsDetails(): Boolean {
        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_product_image), true)
                false

            }
            TextUtils.isEmpty(binding.etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }
            TextUtils.isEmpty(binding.etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(binding.etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_description),
                    true
                )
                false
            }
            TextUtils.isEmpty(binding.etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_product_quantity),
                    true
                )
                false
            }

            else -> {
                return true
            }
        }
    }

    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
        //showErrorSnackBar("Product Image upload Successfully . Image URL : $imageURL", false)
        mProductImageURL = imageURL

        //TODO uploadProductDetails()
        uploadProductDetails()
    }

    fun productUploadSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_upload_successfully),
            Toast.LENGTH_LONG
        ).show()

        finish()
    }

    private fun uploadProductDetails() {

        val username = this.getSharedPreferences(
            Constants.MYSHOPPAL_PREFERENCES,
            Context.MODE_PRIVATE
        ).getString(Constants.LOGGED_IN_USERNAME, "")!!
        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            binding.etProductTitle.text.toString().trim { it <= ' ' },
            binding.etProductPrice.text.toString().trim { it <= ' ' },
            binding.etProductDescription.text.toString().trim { it <= ' ' },
            binding.etProductQuantity.text.toString().trim { it <= ' ' },
            mProductImageURL

        )
        FirestoreClass().uploadProductDetails(this, product)


    }


}