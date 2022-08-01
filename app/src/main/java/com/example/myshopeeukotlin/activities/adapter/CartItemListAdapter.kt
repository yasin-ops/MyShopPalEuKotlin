package com.example.myshopeeukotlin.activities.adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myshopeeukotlin.R
import com.example.myshopeeukotlin.activities.CartListActivity
import com.example.myshopeeukotlin.firestore.FirestoreClass
import com.example.myshopeeukotlin.model.CartItem
import com.example.myshopeeukotlin.utils.Constants
import com.example.myshopeeukotlin.utils.GlideLoader

open class CartItemListAdapter(
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItem: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(
                    R.layout.item_card_layout,
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
                holder.itemView.findViewById<ImageView>(R.id.iv_cart_item_image)
            )
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_title).text = model.title
            holder.itemView.findViewById<TextView>(R.id.tv_cart_item_price).text = model.price
            holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text = model.cart_quantity
            if (model.cart_quantity == "0") {
                holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                    View.GONE
                holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                    View.GONE
                if (updateCartItem) {
                    holder.itemView.findViewById<ImageView>(R.id.ib_cart_delete_product).visibility =
                        View.VISIBLE
                } else {
                    holder.itemView.findViewById<ImageView>(R.id.ib_cart_delete_product).visibility =
                        View.GONE
                }
                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context, R.color.colorSnackBarError
                    )
                )

            } else {
                if (updateCartItem) {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                        View.VISIBLE
                    holder.itemView.findViewById<ImageView>(R.id.ib_cart_delete_product).visibility =
                        View.VISIBLE
                } else {
                    holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).visibility =
                        View.GONE
                    holder.itemView.findViewById<ImageView>(R.id.ib_cart_delete_product).visibility =
                        View.GONE
                }



                holder.itemView.findViewById<TextView>(R.id.tv_cart_quantity).setTextColor(
                    ContextCompat.getColor(
                        context, R.color.ColorSecondaryText
                    )
                )
            }
            holder.itemView.findViewById<ImageView>(R.id.ib_cart_delete_product)
                .setOnClickListener {
                    when (context) {
                        is CartListActivity -> {
                            context.showProgressDialog(context.resources.getString(R.string.please_wait))


                        }
                    }
                    FirestoreClass().removeItemFromCart(context, model.id)

                }


            holder.itemView.findViewById<ImageButton>(R.id.ib_remove_cart_item).setOnClickListener {
                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {
                    val cartQuantity: Int = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()
                    // show the progress Dialog
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.getString(R.string.please_wait))
                    }
                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }


            }
            holder.itemView.findViewById<ImageButton>(R.id.ib_add_cart_item).setOnClickListener {

                val cartQuantity: Int = model.cart_quantity.toInt()
                if (cartQuantity < model.stock_quantity.toInt()) {
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.getString(R.string.please_wait))

                    }
                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)

                } else if (context is CartListActivity) {
                    context.showErrorSnackBar(
                        context.getString(
                            R.string.msg_for_avaiable_stock,
                            model.stock_quantity
                        ), true
                    )
                }
            }

        }


    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)


}



