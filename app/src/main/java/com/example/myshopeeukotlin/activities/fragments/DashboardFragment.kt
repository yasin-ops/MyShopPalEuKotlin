package com.example.myshopeeukotlin.activities.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.CartListActivity
import com.example.myshopeeukotlin.activities.SettingActivity
import com.example.myshopeeukotlin.activities.adapter.DashboardItemsListAdapter

import com.example.myshopeeukotlin.databinding.FragmentDashboardBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.Product
import java.util.logging.Logger

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null


    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_setting -> {
                startActivity(Intent(activity, SettingActivity::class.java))
                return true
            }
            R.id.action_cart->{
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemList: ArrayList<Product>) {
        hideProgressDialog()
        // for testing purpose
//        for (i in dashboardItemList) {
//            Log.i("item title", i.title)
//        }

        if (dashboardItemList.size > 0) {
            binding.rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemFound.visibility = View.GONE
            binding.rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            binding.rvDashboardItems.setHasFixedSize(true)
            // Initialize the Adapter Class
            val adapter = DashboardItemsListAdapter(requireActivity(), dashboardItemList)
            binding.rvDashboardItems.adapter = adapter


        } else {
            binding.rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemFound.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemList() {
        // Show the Progress Dialog
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemList()

    }

}