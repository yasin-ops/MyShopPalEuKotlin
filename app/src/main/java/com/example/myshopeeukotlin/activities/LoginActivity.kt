package com.example.myshopeeukotlin.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Telephony
import android.text.TextUtils
import android.util.Log
import android.util.Log.*
import android.view.View

import android.view.WindowInsets
import android.view.WindowManager
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityLoginBinding

import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.User
import com.example.myshopeeukotlin.utils.Constants
import com.google.firebase.auth.FirebaseAuth


class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: com.example.myshopeeukotlin.databinding.ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {

            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
            )


        }

        // Click event assigned to forgot Passwords
        binding.tvForgotPassword.setOnClickListener(this)
        // Click event assigned to Login Button
        binding.btnLogin.setOnClickListener(this)
        // Click event assigned to Register Button
        binding.tvRegister.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        if (view != null)
            when (view.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)

                }
                R.id.btn_login -> {
                    // TODO STEP 6: CALL THE VALIDATE FUNCTION
                    //START
                    loginRegisterUser()
                    //END
                }

                R.id.tv_register -> {
                    // Launch the register screen when user clicks on the text
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }

            }
    }


    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            else -> {
                //showErrorSnackBar("your detail are valid", false)
                true
            }
        }


    }

    private fun loginRegisterUser() {
        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))
            // Get the text from EditText and Trim Space
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }
            // Log In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    // hide the Progress Dialog
                    // hideProgressDialog()
                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                        // TODO Send user To MainActivity
                        // showErrorSnackBar("you are Logged in successfully ", true)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }

        }

    }


    fun userLoggedInSuccess(user: User) {
        // Hide the Progress the Dialog
        hideProgressDialog()
        // Print the User detail in Log cat
        Log.i("first Name: ", user.firstName)
        Log.i("last Name: ", user.lastName)
        Log.i("Email: ", user.email)
        if (user.profileCompleted == 0) {
            val intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
            startActivity(intent)

        } else {
            startActivity(Intent(this@LoginActivity, DashBoardActivity::class.java))
        }

        finish()

    }

}