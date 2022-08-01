package com.example.myshopeeukotlin.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.adapter.CartItemListAdapter
import com.example.myshopeeukotlin.databinding.ActivityCheckOutBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.Address
import com.example.myshopeeukotlin.model.CartItem
import com.example.myshopeeukotlin.model.Order

import com.example.myshopeeukotlin.model.Product
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.Constants.Orders

class CheckoutActivity : BaseActivity() {
    private lateinit var binding: ActivityCheckOutBinding
    private var mAddressDetails: Address? = null
    private lateinit var mProductList: ArrayList<Product>
    private lateinit var mCartItemList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
            // Toast.makeText(this, "Hello", Toast.LENGTH_LONG).show()
        }

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }
        if (mAddressDetails != null) {

            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text =
                "${mAddressDetails?.address},${mAddressDetails?.zipCode}"
            binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber

        }

        getProductList()
    }

    fun successProductListFromFireStore(productList: ArrayList<Product>) {
        mProductList = productList
        getCartItemList()

    }


    fun getProductList() {
        // Show the Progress Dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCheckoutActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarCheckoutActivity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun getCartItemList() {
        FirestoreClass().getCartList(this@CheckoutActivity)

    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        for (product in mProductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.id) {
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemList = cartList
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)
        val cartListAdapter = CartItemListAdapter(this@CheckoutActivity, mCartItemList, false)
        binding.rvCartListItems.adapter = cartListAdapter

        for (item in mCartItemList) {
            val price = item.price.toDouble()
            val quantity = item.cart_quantity.toInt()
            mSubTotal += (price * quantity)


        }
        binding.tvCheckoutSubTotal.text = "$$mSubTotal"
        binding.tvCheckoutShippingCharge.text = "$10.0"
        mTotalAmount = mSubTotal + 10

        binding.tvCheckoutTotalAmount.text = mTotalAmount.toString()
        Log.i("hello", mTotalAmount.toString())


    }

    fun allDetailsUpdatedSuccessfully() {

        hideProgressDialog()
        Toast.makeText(
            this@CheckoutActivity, "Your order was placed Successfully",
            Toast.LENGTH_LONG
        )
            .show()
        val intent = Intent(this@CheckoutActivity, DashBoardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlaceSuccess() {

        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemList)
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mAddressDetails != null) {
            val orders = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()

            )
            FirestoreClass().placeOrder(this@CheckoutActivity, orders)
        }

    }


}