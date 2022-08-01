package com.example.myshopeeukotlin.activities.adapter

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.ProductDetailActivity
import com.example.myshopeeukotlin.model.Product
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader

open class DashboardItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            GlideLoader(context)
                .loadProductPicture(
                    model.image,
                    holder.itemView.findViewById(R.id.iv_dashboard_item_image)
                )
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_dashboard_item_price).text =
                "$${model.price}"

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID,model.user_id)
                context.startActivity(intent)

            }


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}





