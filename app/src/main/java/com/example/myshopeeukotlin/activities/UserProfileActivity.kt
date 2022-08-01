package com.example.myshopeeukotlin.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityUserProfileBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.User
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader
import com.squareup.picasso.Picasso
import java.io.IOException
import java.util.HashMap


class UserProfileActivity : BaseActivity(), View.OnClickListener {
    var mUserDetail: User = User()
    var mSelectedImageFileUri: Uri? = null
    var mUserProfileImageURL: String = ""

    private lateinit var binding: ActivityUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)




        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            // Get the user details from intent as a ParcelableExtra. !! Because its nullable parcelize
            mUserDetail = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!

        }
        binding.etFirstName.setText(mUserDetail.firstName)
        binding.etLastName.setText(mUserDetail.lastName)

        binding.etEmail.isEnabled = false
        binding.etEmail.setText(mUserDetail.email)

        if (mUserDetail.profileCompleted == 0) {
            binding.tvTitle.text = resources.getString(R.string.title_complete_profile)
            binding.etFirstName.isEnabled = false

            binding.etLastName.isEnabled = false


        } else {
            setupActionBar()
            binding.tvTitle.text = resources.getString(R.string.title_edit_profile)
            GlideLoader(this@UserProfileActivity).loadUserPicture(
                mUserDetail.image,
                binding.ivUserPhoto
            )
            if (mUserDetail.mobile != 0L) {
                binding.etMobileNumber.setText(mUserDetail.mobile.toString())
            }
            if (mUserDetail.gender == Constants.MALE) {
                binding.rgMale.isChecked = true
            } else {
                binding.rgFemale.isChecked = true
            }

        }


// Handle click Event Function
        binding.ivUserPhoto.setOnClickListener(this)
        binding.btnSubmit.setOnClickListener(this)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galeriIntext =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntext, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
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
                if (Build.VERSION.SDK_INT >= 28) {


                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivUserPhoto)
                } else {

                    GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!, binding.ivUserPhoto)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    pickPhoto()
                }
                R.id.btn_submit -> {

                    if (validateUserProfileDetail()) {
                        showProgressDialog(resources.getString(R.string.please_wait))
                        if (mSelectedImageFileUri != null) {
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri,Constants.USER_PROFILE_IMAGE)

                        } else {
                            updateUserProfileDetails()
                        }


                        //   showErrorSnackBar("your detail are Valid. You can update them", false)
                    } else {
                        showErrorSnackBar("your detail are not Valid.", true)
                    }

                }
            }
        }
    }

    private fun updateUserProfileDetails() {
        var userHashMap: HashMap<String, Any> = HashMap<String, Any>()
        // val userHashMap: HashMap<String,Any>
        val firstName = binding.etFirstName.text.toString().trim { it <= ' ' }
        if (firstName != mUserDetail.firstName) {
            userHashMap[Constants.FIRST_NAME] = firstName

        }

        val lastName = binding.etLastName.text.toString().trim { it <= ' ' }
        if (lastName != mUserDetail.lastName) {
            userHashMap[Constants.LAST_NAME] = lastName

        }

        val mobileNumber = binding.etMobileNumber.text.toString().trim { it <= ' ' }

        val gender = if (binding.rgMale.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }
        if (mUserProfileImageURL.isNotEmpty()) {
            userHashMap[Constants.IMAGE] = mUserProfileImageURL
        }
        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetail.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if (gender.isNotEmpty() && gender != mUserDetail.gender) {
            userHashMap[Constants.GENDER] = gender
        }
        // key : gender value:male
        // gender:male
        userHashMap[Constants.GENDER] = gender
        userHashMap[Constants.COMPLETE_PROFILE] = 1

        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this, userHashMap)

    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_LONG
        ).show()
        startActivity(Intent(this@UserProfileActivity, DashBoardActivity::class.java))
        finish()
    }

    private fun validateUserProfileDetail(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number), true)
                false
            }
            else -> {
                true
            }
        }

    }

    fun imageUploadSuccess(imageURL: String) {
        // hideProgressDialog()
//        Toast.makeText(
//            this@UserProfileActivity,
//            "Your image uploaded successfully. The Image url is " +
//                    "$imageURL", Toast.LENGTH_LONG
//        ).show()

        mUserProfileImageURL = imageURL
        updateUserProfileDetails()

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarUserProfileActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarUserProfileActivity.setNavigationOnClickListener { onBackPressed() }
    }

}













