package com.example.myshopeeukotlin.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.adapter.AddressListAdapter
import com.example.myshopeeukotlin.databinding.ActivityAddressListBinding
import com.example.myshopeeukotlin.databinding.ActivityCartListBinding
import com.example.myshopeeukotlin.databinding.ActivitySettingBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.Address
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.SwipeToDeleteCallback
import com.example.myshopeeukotlin.utils.SwipeToEditCallback
import java.util.*
import kotlin.collections.ArrayList

class AddressListActivity : BaseActivity() {
    private lateinit var binding: ActivityAddressListBinding
    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)
        binding = ActivityAddressListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress =
                intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        setupActionBar()

        if (mSelectAddress) {
            binding.tvTitleAddressList.text = resources.getString(R.string.title_select_address)
        }

        binding.tvAddAddress.setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        getAddressList()


    }



    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressList(this)


    }


    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()
        // for testing Purpose
        // for (i in addressList) {
        // Log.i("Name and Address", "${i.name}::${i.address}")
//
//
//
//    }

        if (addressList.size > 0) {
            binding.rvAddressList.visibility = View.VISIBLE
            binding.tvAddressFound.visibility = View.GONE
            binding.rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            binding.rvAddressList.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this, addressList, mSelectAddress)
            binding.rvAddressList.adapter = addressAdapter

            if (!mSelectAddress) {

                val editSwipeHandler = object : SwipeToEditCallback(this@AddressListActivity) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = binding.rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this@AddressListActivity) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )

                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(binding.rvAddressList)

            }

        } else {
            binding.rvAddressList.visibility = View.GONE
            binding.tvAddressFound.visibility = View.VISIBLE

        }


    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbarAddressListActivity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_arrow_24)
        }
        binding.toolbarAddressListActivity.setNavigationOnClickListener { onBackPressed() }
    }



    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {

                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "To add the address.")
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun deleteAddressSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully), Toast.LENGTH_LONG
        ).show()
        getAddressList()
    }
}