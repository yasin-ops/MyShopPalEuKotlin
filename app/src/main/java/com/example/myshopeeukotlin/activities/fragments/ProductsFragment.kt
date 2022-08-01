package com.example.myshopeeukotlin.activities.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myshopeeukotlin.MyProductListAdapter
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.AddProductActivity
import com.example.myshopeeukotlin.databinding.FragmentProductsBinding
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.Product

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // if we want to use the option menu in fragment we need to add it
        setHasOptionsMenu(true)
    }

    fun deleteProduct(productId: String) {
        showAlertDialogDeleteProduct(productId)
//
//        Toast.makeText(
//            requireActivity(),
//            "you can now delete the product . ${productId}",
//            Toast.LENGTH_LONG
//        )
//            .show()
        getProductListFromFireStore()
    }

    fun productDeleteSuccess() {
        hideProgressDialog()

        //  showAlertDialogDeleteProduct(productId)
//for testing purpose
        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_LONG
        ).show()


        getProductListFromFireStore()

    }


    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()

        /* For Testing Purpose
            for (i in productsList) {
                Log.i("Product name ", i.title)
            }

           */
        if (productsList.size > 0) {
            binding.ryMyProductItems.visibility = View.VISIBLE
            binding.tvNoProductsFound.visibility = View.GONE
            binding.ryMyProductItems.layoutManager = LinearLayoutManager(activity)
            binding.ryMyProductItems.setHasFixedSize(true)
            val adapterProducts = MyProductListAdapter(requireActivity(), productsList, this)
            binding.ryMyProductItems.adapter = adapterProducts

        } else {
            binding.ryMyProductItems.visibility = View.GONE
            binding.tvNoProductsFound.visibility = View.VISIBLE

        }


    }

    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    private fun showAlertDialogDeleteProduct(productId: String) {
        val builder = AlertDialog.Builder(requireActivity())
        // set title for alert dialog
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        // set message for alert dialog
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        // set icon for alert dialog
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        // perform positive action
        builder.setPositiveButton(resources.getString(R.string.yes)) { dialogInterface, _ ->
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().deleteProduct(this, productId)
            dialogInterface.dismiss()
        }
        // perform negative action
        builder.setNegativeButton(resources.getString(R.string.no)) { dialogInterface, _ ->
            dialogInterface.dismiss()

        }
        // Create the alert dialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()

    }


}