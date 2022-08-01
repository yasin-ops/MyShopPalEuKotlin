package com.example.myshopeeukotlin.activities

import android.content.Intent
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.databinding.ActivityDashBoardBinding
import com.example.myshopeeukotlin.databinding.ActivityProductDetailBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.CartItem
import com.example.myshopeeukotlin.model.Product
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader
import com.google.android.gms.common.GooglePlayServicesNotAvailableException

class ProductDetailActivity : BaseActivity(), View.OnClickListener {
    private lateinit var binding: ActivityProductDetailBinding
    private var mProductId: String = ""

    // object of data class
    private lateinit var mproductDetail: Product
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()

        binding.btnAddToCart.setOnClickListener(this)
        binding.btnGoToCart.setOnClickListener(this)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product ID", mProductId)

        }


        var productOwnerId: String = ""

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            productOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
            Log.i("Product Owner Id", productOwnerId)

        }
        if (FirestoreClass().getCurrentUserID() == productOwnerId) {
            binding.btnAddToCart.visibility = View.GONE
            binding.btnGoToCart.visibility = View.GONE
        } else {
            binding.btnAddToCart.visibility = View.VISIBLE
            binding.btnGoToCart.visibility = View.VISIBLE

        }
        getProductDetails()
    }


    private fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        mproductDetail = product
        // hideProgressDialog()
        GlideLoader(this@ProductDetailActivity)
            .loadProductPicture(product.image, binding.productDetailImage)

        binding.tvProductDetailsTitle.text = product.title
        binding.tvProductDetailsPrice.text = "$${product.price}"
        binding.tvProductDetailsDescription.text = product.description
        binding.tvProductDetailsAvailableStockQuantity.text = product.stock_quantity

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()
            binding.btnAddToCart.visibility = View.GONE
            binding.tvProductDetailsAvailableStockQuantity.text =
                resources.getString(R.string.lbl_out_of_stock)

            binding.tvProductDetailsAvailableStockQuantity.setTextColor(
                ContextCompat.getColor(
                    this@ProductDetailActivity, R.color.colorSnackBarError
                )
            )

        } else {

            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this, mProductId)
            }
        }


    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarProductDetailActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarProductDetailActivity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductId,
            mproductDetail.title,
            mproductDetail.price,
            mproductDetail.image,
            Constants.DEFAULT_CART_QUANTITY

        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this, cartItem)


    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_add_to_cart -> {

                    addToCart()
                }
                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailActivity, CartListActivity::class.java))

                }

            }
        }

    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailActivity, resources.getString(
                R.string.success_message_item_added_to_cart
            ), Toast.LENGTH_LONG
        ).show()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }

    fun productExistsInCart() {
        hideProgressDialog()
        binding.btnAddToCart.visibility = View.GONE
        binding.btnGoToCart.visibility = View.VISIBLE
    }
}