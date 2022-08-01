package com.example.myshopeeukotlin.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityForgotPasswordBinding
import com.example.myshopeeukotlin.databinding.ActivityLoginBinding
import com.example.myshopeeukotlin.databinding.ActivityMainBinding
import com.example.myshopeeukotlin.utils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences =
            getSharedPreferences(Constants.MYSHOPPAL_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        binding.tvMain.text = "The logged in user i: $username"


    }
}