package com.example.myshopeeukotlin.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.adapter.CartItemListAdapter
import com.example.myshopeeukotlin.databinding.ActivityCartListBinding
import com.example.myshopeeukotlin.databinding.ActivityRegisterBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.CartItem
import com.example.myshopeeukotlin.model.Product
import com.example.myshopeeukotlin.utils.Constants

class CartListActivity : BaseActivity() {
    private lateinit var binding: ActivityCartListBinding
    private lateinit var mproductList: ArrayList<Product>
    private lateinit var mCartListItem: ArrayList<CartItem>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar()
        binding.btnCheckout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }

    }

    fun successCartItemList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()
        // just testing purpose
//        for (i in cartList) {
//            Log.i("Cart item Title", "successCartItemList: " + i.title)
//        }

        for (product in mproductList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity
                    if (product.stock_quantity.toInt() == 0) {
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }

        }
        mCartListItem = cartList

        if (mCartListItem.size > 0) {
            binding.rvCartItemsList.visibility = View.VISIBLE
            binding.llCheckout.visibility = View.VISIBLE
            binding.tvNoCartItemFound.visibility = View.GONE

            binding.rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            binding.rvCartItemsList.setHasFixedSize(true)

            val cartListAdapter = CartItemListAdapter(this@CartListActivity, mCartListItem,true)
            binding.rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItem) {
                val availableQuantity = item.stock_quantity.toInt()
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }

            }
            binding.tvSubTotal.text = "$$subTotal"
            binding.tvShipingcharge.text = "$$10.0" //TODO - Change shipping charge

            if (subTotal > 0) {
                binding.llCheckout.visibility = View.VISIBLE
                val total = subTotal + 10 //TODO - Change Logic here
                binding.tvTotalAmount.text = "$$total"

            } else {

                binding.llCheckout.visibility = View.INVISIBLE


            }


        } else {
            binding.rvCartItemsList.visibility = View.GONE
            binding.llCheckout.visibility = View.GONE
            binding.tvNoCartItemFound.visibility = View.VISIBLE

        }

    }


    fun successProductListFromFireStore(productList: ArrayList<Product>) {
        hideProgressDialog()
        mproductList = productList
        getCartItemsList()

    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }


    private fun getCartItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)

    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    fun itemRemoveSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_LONG
        )
            .show()
        getCartItemsList()

    }

    override fun onResume() {
        super.onResume()
        // getCartItemsList()
        getProductList()
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding.toolbarCartListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarCartListActivity.setNavigationOnClickListener {
            onBackPressed()
        }
    }

}