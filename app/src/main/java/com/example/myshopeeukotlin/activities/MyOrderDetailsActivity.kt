package com.example.myshopeeukotlin.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityAddEditAddressBinding
import com.example.myshopeeukotlin.databinding.ActivityMyOrderDetailsBinding
import com.example.myshopeeukotlin.model.Order
import com.example.myshopeeukotlin.utils.Constants
import java.text.SimpleDateFormat
import java.util.*

class MyOrderDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        var myOrderdetails: Order = Order()

        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderdetails = intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!

        }
        setupUI(myOrderdetails)


    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarMyOrderDetailsActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarMyOrderDetailsActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupUI(orderDetails: Order) {
        binding.tvOrderDetailsId.text = orderDetails.id
        val dateFormat = "dd MMM yyyy HH:mm"

        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime
        val orderDateTime = formatter.format(calendar.time)
        binding.tvOrderDetailsDate.text = orderDateTime


    }

}