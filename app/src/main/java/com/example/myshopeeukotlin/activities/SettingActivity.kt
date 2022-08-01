package com.example.myshopeeukotlin.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivitySettingBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.User
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader
import com.google.firebase.auth.FirebaseAuth

class SettingActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar()

        // Click Event
        binding.btnLogout.setOnClickListener(this)
        binding.tvEdit.setOnClickListener(this)
        binding.llAddress.setOnClickListener(this)

    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolBarSettingActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolBarSettingActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this@SettingActivity)
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        hideProgressDialog()
        if (user.image != null) {
            if (Build.VERSION.SDK_INT >= 28) {

                GlideLoader(this@SettingActivity).loadUserPicture(user.image, binding.ivUserPhoto)
                Log.d("rrr", "userDetailsSuccess: " + user.image)

            } else {

                GlideLoader(this@SettingActivity).loadUserPicture(user.image, binding.ivUserPhoto)
                Log.d("rrr", "userDetailsSuccess: " + user.image)
            }
        }

        binding.tvName.text = "${user.firstName} ${user.lastName}"
        binding.tvGender.text = user.gender
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"

    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingActivity, AddressListActivity::class.java)
                    startActivity(intent)

                }
            }
        }

    }

}