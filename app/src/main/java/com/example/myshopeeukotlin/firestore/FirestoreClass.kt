package com.example.myshopeeukotlin.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.myshopeeukotlin.activities.*
import com.example.myshopeeukotlin.activities.fragments.DashboardFragment
import com.example.myshopeeukotlin.activities.fragments.OrdersFragment
import com.example.myshopeeukotlin.activities.fragments.ProductsFragment
import com.example.myshopeeukotlin.model.*
import com.example.myshopeeukotlin.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        // The 'users' is collection name. if the collection is already created then it will not create the same
        mFireStore.collection(Constants.USERS)
            // Document ID for users field. Here the document it is the Users ID.
            .document(userInfo.id)
            // Here the userInfo are Field and the setOption is set to merge. It is for if we want to merge later on instead of replacing the field
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transfering the result to it
                activity.userRegistrationSuccess()

            }.addOnFailureListener { exc ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user .", exc)
            }

    }

    fun getCurrentUserID(): String {
        // An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

// A variable to assign the currentUserId if it is not null or else it will be balnk
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        // Here we pass the collection name from which we want the data
        mFireStore.collection(Constants.USERS)
            // the document id to get the fields of user.
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())
                // Here we received the document snapshot which is converted in to the User Data model Object
                val user = document.toObject(User::class.java)!!

                val sharePreferences =
                    activity.getSharedPreferences(
                        Constants.MYSHOPPAL_PREFERENCES,
                        // data present inside the application
                        Context.MODE_PRIVATE
                    )
                val editor: SharedPreferences.Editor = sharePreferences.edit()
                editor.putString(
                    //Key : Constants.LOGGED_IN_USERNAME
                    // Value :  first and Last Name
                    Constants.LOGGED_IN_USERNAME,
                    "${user.firstName} ${user.lastName}"
                )
                editor.apply()


                //TODO step 6: Pass the result to login Activity.
                //Start
                when (activity) {
                    is LoginActivity -> {
                        // Call a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)

                    }
                    is SettingActivity -> {
                        activity.userDetailsSuccess(user)
                    }
                }
                //End


            }

    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }

                }
            }
            .addOnFailureListener { exc ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()

                    }
                    is SettingActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(activity.javaClass.simpleName, "Error while updating the user detail", exc)

            }

    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileUri: Uri?, imageType: String) {
        // Storage Reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference
            .child(
                imageType
                        + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(
                    activity, imageFileUri

                )

            )
        // Put file online
        sRef.putFile(imageFileUri!!)
            .addOnSuccessListener { taskSnapshot ->
                // The Image upload is Success

                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl!!.toString()
                )
                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl!!.addOnSuccessListener { uri ->

//                    Log.e("DownLoadable Image URL", uri.toString())
                    when (activity) {
                        is UserProfileActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                        is AddProductActivity -> {
                            activity.imageUploadSuccess(uri.toString())
                        }
                    }

                }
                    .addOnFailureListener { exception ->
                        when (activity) {
                            is UserProfileActivity -> {
                                activity.hideProgressDialog()
                            }
                            is AddProductActivity -> {
                                activity.hideProgressDialog()
                            }
                        }
                        Log.e(
                            activity.javaClass.simpleName,
                            exception.message.toString(),
                            exception
                        )

                    }

            }

    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFireStore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here Call a function of base activity for transferring the result to it
                activity.productUploadSuccess()

            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                // for testing purpose
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the Product details.",
                    e

                )
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productID: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productID)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()

            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the Products",
                    e
                )

            }

    }

    fun getProductsList(fragment: Fragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())
                // val productsList: ArrayList<Product>()
                //Multiple Products displaying through loop
                val productsList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFireStore(productsList)
                    }
                }

            }.addOnFailureListener {

            }

    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e(fragment.javaClass.simpleName, document.documents.toString())
                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productList.add(product)

                }
                fragment.successDashboardItemsList(productList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard item list", e)


            }
    }

    fun getProductDetails(activity: ProductDetailActivity, productId: String) {
        mFireStore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }

            }.addOnFailureListener { e ->
                // Hide the Progress dialog if there is an Error
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the Product Details . ",
                    e
                )

            }
    }

    fun addCartItems(activity: ProductDetailActivity, addToCart: CartItem) {
        mFireStore.collection(Constants.CART_ITEM)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "addCartItems: ", e)
            }


    }

    fun checkIfItemExistInCart(activity: ProductDetailActivity, productId: String) {
        mFireStore.collection(Constants.CART_ITEM)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                Log.i("Yes", "checkIfItemExistInCart: " + document.documents.toString())
                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }


            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "addCartItems: ", e)
            }
    }

    fun getAllProductsList(activity: Activity) {
        mFireStore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Product List", document.documents.toString())

                val productList: ArrayList<Product> = ArrayList()
                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }
                when (activity) {
                    is CartListActivity -> {

                        activity.successProductListFromFireStore(productList)
                    }
                    is CheckoutActivity -> {
                        activity.successProductListFromFireStore(productList)
                    }

                }


            }.addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()

                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e("Get Product List", "Error while getting all Product List", e)
            }

    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFireStore.collection(Constants.CART_ITEM)
            .document(cart_id) // cart _id
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemoveSuccess()
                    }
                }


            }
            .addOnFailureListener { e ->
                // Hide the Progress Dialog if there is any error
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()

                    }

                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list ",
                    e
                )

            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFireStore.collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {

                activity.addUpdateAddressSuccess()
            }.addOnFailureListener { e ->
                when (activity) {
                    is AddEditAddressActivity -> {
                        activity.hideProgressDialog()

                    }

                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while removing the item from the cart list ",
                    e
                )


            }

    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()

            }.addOnFailureListener { e ->

                when (activity) {
                    is AddressListActivity -> {
                        activity.hideProgressDialog()

                    }

                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the Address ",
                    e
                )
            }
    }


    fun updateAddress(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFireStore.collection(Constants.ADDRESSES)
            .document(addressId)
            // Here the userInfo are failed and the setOption is set to merge. it is for if we want to merge
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()

            }.addOnFailureListener { e ->

                when (activity) {
                    is AddEditAddressActivity -> {
                        activity.hideProgressDialog()

                    }

                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the Address Information ",
                    e
                )

            }

    }

    fun getAddressList(activity: AddressListActivity) {
        mFireStore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val addressList: ArrayList<Address> = ArrayList()
                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }
                // SuccessLoading Address
                activity.successAddressListFromFirestore(addressList)


            }.addOnFailureListener { e ->
                when (activity) {
                    is AddressListActivity -> {
                        activity.hideProgressDialog()

                    }

                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the address list ",
                    e
                )

            }

    }


    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.CART_ITEM)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {

                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }

            }.addOnFailureListener { e ->
                // Hide the Progress Dialog if there is any error
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()

                    }

                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while updtaing the item from the cart list ",
                    e
                )


            }
    }


    fun getCartList(activity: Activity) {
        mFireStore.collection(Constants.CART_ITEM)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->

                Log.e(activity.javaClass.simpleName, document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()
                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!

                    cartItem.id = i.id
                    list.add(cartItem)

                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemList(list)

                    }
                    is CheckoutActivity -> {
                        activity.successCartItemsList(list)
                    }

                }


            }.addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting the cart list item", e)

            }

    }

    fun placeOrder(activity: CheckoutActivity, order: Order) {
        mFireStore.collection(Constants.Orders)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlaceSuccess()

            }.addOnFailureListener { e ->
                when (activity) {
                    is CheckoutActivity -> {
                        activity.hideProgressDialog()

                    }

                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the place Order ",
                    e
                )

            }
    }

    fun updateAllDetails(activity: CheckoutActivity, cartList: ArrayList<CartItem>) {

        val writeBatch = mFireStore.batch()

//        // Here we will update the product stock in the products collection based to cart quantity.
//        for (cart in cartList) {
//
//            val productHashMap = HashMap<String, Any>()
//
//            productHashMap[Constants.STOCK_QUANTITY] =
//                (cart.stock_quantity.toInt() - cart.cart_quantity.toInt()).toString()
//
//            val documentReference = mFireStore.collection(Constants.PRODUCTS)
//                .document(cart.product_id)
//
//            writeBatch.update(documentReference, productHashMap)
//        }

        // Delete the list of cart items
        for (cart in cartList) {

            val documentReference = mFireStore.collection(Constants.CART_ITEM)
                .document(cart.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit().addOnSuccessListener {

            // TODO Step 4: Finally after performing all the operation notify the user with the success result.
            // START
            activity.allDetailsUpdatedSuccessfully()
            // END

        }.addOnFailureListener { e ->
            // Here call a function of base activity for transferring the result to it.
            activity.hideProgressDialog()

            Log.e(
                activity.javaClass.simpleName,
                "Error while updating all the details after order placed.",
                e
            )
        }
    }


    fun getMyOrdersList(fragment: OrdersFragment) {
        mFireStore.collection(Constants.Orders)
            .whereEqualTo(Constants.USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { documnet ->
                val list: ArrayList<Order> = ArrayList()
                for (i in documnet.documents) {
                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id = i.id
                    list.add(orderItem)
                }
                fragment.populateOrdersListInUI(list)

            }.addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting the order list", e)

            }
    }


}