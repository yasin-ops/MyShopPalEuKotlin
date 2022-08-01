package com.example.myshopeeukotlin.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityLoginBinding
import com.example.myshopeeukotlin.databinding.ActivityRegisterBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class RegisterActivity : BaseActivity() {
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        setUpActionBar()
        binding.btnRegister.setOnClickListener {
            registerUser()

        }

        binding.tvLogin.setOnClickListener {
            // val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            // startActivity(intent)
            onBackPressed()
        }

    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarRegisterActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarRegisterActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateRegisterDetail(): Boolean {
        return when {
            TextUtils.isEmpty(binding.etFirstName.text.toString().trim() {
                it <= ' '
            }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(binding.etLastName.text.toString().trim() {
                it <= ' '
            }) || binding.etLastName.length() <= 3
            -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(binding.etEmail.text.toString().trim() {
                it <= ' '
            }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim() {
                it <= ' '
            }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(binding.etConfirmPassword.text.toString().trim() {
                it <= ' '
            }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }
            binding.etPassword.text.toString()
                .trim() { it <= ' ' } != binding.etConfirmPassword.text.toString().trim() {
                it <= ' '
            } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password_mismatch),
                    true
                )
                false
            }
            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }


            else -> {
                //showErrorSnackBar(resources.getString(R.string.registery_successfully), false)
                true
            }
        }
    }

    private fun registerUser() {
        if (validateRegisterDetail()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        // Create New Firebase User
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        // Initalize the Model User Class
                        val user = User(
                            firebaseUser.uid,
                            binding.etFirstName.text.toString().trim { it <= ' ' },
                            binding.etLastName.text.toString().trim { it <= ' ' },
                            binding.etEmail.text.toString().trim { it <= ' ' })

                        // Save the Users data in Cloud
                        FirestoreClass().registerUser(this@RegisterActivity, user)


                        // after successfull Register the User automatic to login Screen
                       // FirebaseAuth.getInstance().signOut()
                       // finish()
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }

                }


        }
    }

    fun userRegistrationSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity, resources.getString(R.string.register_success),
            Toast.LENGTH_LONG
        ).show()


    }
}