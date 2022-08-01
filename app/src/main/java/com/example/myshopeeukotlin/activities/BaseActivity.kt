package com.example.myshopeeukotlin.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityBaseBinding
import com.example.myshopeeukotlin.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text as Text1

open class BaseActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    private var doubleBackToExitPressedOnces = false


    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity, R.color.colorSnackBarError
                )
            )
        } else {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity, R.color.colorSnackBarSuccess
                )
            )
        }
        snackbar.show()
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this)
        /*  Set the screen Content from a layout resources.
        The resource will be inflated, adding all top-level view to the screen */
        mProgressDialog.setContentView(R.layout.dialog_progress_bar)
        mProgressDialog.findViewById<TextView>(R.id.tv_progress_text).text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        // start the dialog and display it on screen
        mProgressDialog.show()

    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit() {
        if (doubleBackToExitPressedOnces) {
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnces = true
        Toast.makeText(
            this, resources.getString(R.string.please_click_back_again_to_exit),
            Toast.LENGTH_SHORT
        ).show()
        @Suppress("DEPRECATION")
        Handler().postDelayed({ doubleBackToExitPressedOnces = false }, 2000)
    }
}