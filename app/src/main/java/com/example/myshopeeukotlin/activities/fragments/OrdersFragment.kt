package com.example.myshopeeukotlin.activities.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.adapter.MyOrdersListAdapter


import com.example.myshopeeukotlin.databinding.FragmentOrdersBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.Order

class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        hideProgressDialog()
        if (ordersList.size > 0) {
            binding.rvMyOrderItem.visibility = View.VISIBLE
            binding.tvNoOrderFound.visibility = View.GONE

            binding.rvMyOrderItem.layoutManager = LinearLayoutManager(activity)
            binding.rvMyOrderItem.setHasFixedSize(true)
            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            binding.rvMyOrderItem.adapter = myOrdersAdapter

        } else {
            binding.rvMyOrderItem.visibility = View.GONE
            binding.tvNoOrderFound.visibility = View.VISIBLE
        }


    }

    private fun getMyOrderList() {
        // show the progress dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this@OrdersFragment)

    }

    override fun onResume() {
        super.onResume()
        getMyOrderList()
    }

}