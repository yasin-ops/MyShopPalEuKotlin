package com.example.myshopeeukotlin

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopeeukotlin.activities.ProductDetailActivity
import com.example.myshopeeukotlin.activities.fragments.ProductsFragment
import com.example.myshopeeukotlin.model.Product
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader

open class MyProductListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private var fragment: ProductsFragment
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.image,
                holder.itemView.findViewById<ImageView>(R.id.iv_item_image)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_item_name).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_item_price).text = "$${model.price}"
            holder.itemView.findViewById<ImageView>(R.id.ib_delete_product).setOnClickListener {
                // Todo Delete the Product
                fragment.deleteProduct(model.product_id)
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)

            }

        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)




