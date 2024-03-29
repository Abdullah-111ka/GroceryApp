package com.project.mygroceryapp.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.project.mygroceryapp.fragments.PurchaseFragment
import com.project.mygroceryapp.fragments.ViewOrderFragment
import com.google.android.material.navigation.NavigationView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.mygroceryapp.R
import com.project.mygroceryapp.databinding.ActivityMainBinding
import com.project.mygroceryapp.utils.fetchUserData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private var suggestions = mutableListOf<String>()
    private val productsCollectionRef = Firebase.firestore.collection("products")
    private val usersRef = Firebase.firestore.collection("users")
    private lateinit var userID: String
    private lateinit var listenerRegistration: ListenerRegistration

    override fun onBackPressed() {
        super.onBackPressed()
        val alertDialog = AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle("Exit Application")
            .setMessage("Are you sure you want to leave this application?")
            .setPositiveButton("YES", DialogInterface.OnClickListener { _, _
                ->
                finish()
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, _
                ->
                dialog.dismiss()
            })

        alertDialog.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getRealTimeUpdates()

        //get user ID
        userID = FirebaseAuth.getInstance().currentUser!!.uid

        manageImageSlider()
        manageToolbar()
        manageSideNavigation()
        manageBottomNavigation()

        //open drawer
        binding.toolbar.navDrawerToolbar.setNavigationOnClickListener {
            binding.drawer.openDrawer(GravityCompat.START)
        }

        //sign out
        binding.tvSignOut.setOnClickListener {

            Intent(this, LoginActivity::class.java).also {
                listenerRegistration.remove()
                FirebaseAuth.getInstance().signOut()
                startActivity(it)
                finish()
            }
        }
    }

    private fun getRealTimeUpdates() {
        listenerRegistration = productsCollectionRef.addSnapshotListener { snapshot, error ->
            error?.let {
                Log.e("collection ref error: ", error.message.toString())
                return@addSnapshotListener
            }

            snapshot?.let {
                for (document in it) {
                    suggestions.add(document["name"].toString())
                }
            }
        }
    }

    private fun manageBottomNavigation() {
        //bottom navigation

        val purchaseFragment = PurchaseFragment()
        val viewOrderFragment = ViewOrderFragment()

        replaceFragment(purchaseFragment)
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.btm_nav_purchase -> replaceFragment(purchaseFragment)
                R.id.btm_nav_view_orders -> replaceFragment(viewOrderFragment)
            }
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun manageSideNavigation() {

        val headerView = binding.sideNavBar.getHeaderView(0)
        val tvAPARname = headerView.findViewById<TextView>(R.id.tv_apar_name)
        val tvAPARno = headerView.findViewById<TextView>(R.id.tv_apar_no)
        val tvStoreCode = headerView.findViewById<TextView>(R.id.tv_store_code)
        val tvRegion = headerView.findViewById<TextView>(R.id.tv_region)
        val tvClusterNo = headerView.findViewById<TextView>(R.id.tv_cluster_no)

        var userModel = fetchUserData(this)

        tvAPARname.text =
            "${userModel.lastName}, ${userModel.firstName}".uppercase()
        tvAPARno.text = userModel.email.toString().uppercase()
        tvRegion.text = userModel.lastName.toString().uppercase()
        tvStoreCode.text = userModel.lastName.toString().uppercase()
        tvClusterNo.text = userModel.lastName.toString().uppercase()



    //side navigation
    val sideNav = NavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.side_bar_delete_item -> {
                Intent(this, DeleteItemActivity::class.java).also {
                    startActivity(it)
                    finish()
                }

                return@OnNavigationItemSelectedListener true
            }


            R.id.side_bar_add_item -> {
                Intent(this, AddNewItemActivity::class.java).also {
                    startActivity(it)
                    finish()
                }

                return@OnNavigationItemSelectedListener true
            }

            R.id.side_bar_update_item -> {
                Intent(this, UpdateItemActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    //set sideNav = navigation for sidebar
    binding.sideNavBar.setNavigationItemSelectedListener(sideNav)

}

private fun manageToolbar() {
    val searchView = binding.toolbar.searchView
    val autoCompleteTextView =
        searchView.findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)
    val searchViewAdapter =
        ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, suggestions)

    autoCompleteTextView.setAdapter(searchViewAdapter)
    autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->

        val currentProduct = searchViewAdapter.getItem(position)

        //display data of current product using dialog
        val showItemDialog = Dialog(this, R.style.CustomDialog)
        showItemDialog.setContentView(R.layout.dialog_search)
        val dialogProdName = showItemDialog.findViewById<TextView>(R.id.tv_search_product_name)
        val dialogProductCategory =
            showItemDialog.findViewById<TextView>(R.id.tv_search_product_category)
        val dialogProductPrice =
            showItemDialog.findViewById<TextView>(R.id.tv_search_product_price)
        var productID: String? = null

        productsCollectionRef.whereEqualTo("name", currentProduct!!.lowercase()).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    dialogProdName.text = document["name"].toString()
                    dialogProductCategory.text = document["category"].toString()
                    dialogProductPrice.text = document["price"].toString()
                    productID = document.id
                }
            }
            .addOnFailureListener { exception ->
                Log.e("manage toolbar", exception.message.toString())
            }

        //delete item
        val tvDelete = showItemDialog.findViewById<TextView>(R.id.tv_delete)
        tvDelete.setOnClickListener {
            showItemDialog.dismiss()
            val deleteDialog = Dialog(this, R.style.CustomDialog)
            deleteDialog.setCancelable(false)
            deleteDialog.setContentView(R.layout.dialog_delete)

            //product name to be deleted
            val tvTobeDeletedProdName =
                deleteDialog.findViewById<TextView>(R.id.tv_delete_product_name)
            tvTobeDeletedProdName.text = dialogProdName.text.toString()

            //cancel delete
            val tvCancel = deleteDialog.findViewById<TextView>(R.id.tv_delete_cancel)
            tvCancel.setOnClickListener {
                deleteDialog.dismiss()
                searchView.onActionViewCollapsed()
            }

            //delete proper
            val tvConfirmDelete = deleteDialog.findViewById<TextView>(R.id.tv_delete_confirm)
            tvConfirmDelete.setOnClickListener {
                Toast.makeText(
                    this,
                    "Product with name: ${
                        dialogProdName.text.toString().uppercase()
                    } was successfully deleted.",
                    Toast.LENGTH_LONG
                ).show()
                productsCollectionRef.document(productID!!).delete()
                deleteDialog.dismiss()
                listenerRegistration.remove()
                finish()
                startActivity(intent)
                overridePendingTransition(0, 0)
            }


            deleteDialog.show()
        }


        //update item
        val tvUpdate = showItemDialog.findViewById<TextView>(R.id.tv_update)
        tvUpdate.setOnClickListener {
            //close search dialog
            showItemDialog.dismiss()
            suggestions.remove(dialogProdName.text.toString())

            val updateDialog = Dialog(this, R.style.CustomDialog)
            updateDialog.setContentView(R.layout.dialog_update)
            updateDialog.setCancelable(false)

            //spinner settings
            val spinner = updateDialog.findViewById<Spinner>(R.id.sp_update)
            populateSpinner(spinner, dialogProductCategory.text.toString())

            //set default values of update dialog fields == the current product's properties(name, category, price)
            val etProductName =
                updateDialog.findViewById<TextInputEditText>(R.id.et_update_product_name)
            val etProductPrice =
                updateDialog.findViewById<TextInputEditText>(R.id.et_update_product_price)

            etProductName.setText(dialogProdName.text.toString())
            etProductPrice.setText(dialogProductPrice.text.toString())

            //update item
            val tvUpdateDoc = updateDialog.findViewById<TextView>(R.id.tv_update_item)

            //allow user to update an item with the same name provided that it should only exist once
            // and must be unique

            tvUpdateDoc.setOnClickListener {
                //allow user to reuse the product name when updating
                val newProductName = etProductName.text.toString()
                val newProductCategory = spinner.selectedItem.toString().uppercase()
                val newProductPrice = etProductPrice.text.toString().toDouble()

                if (validateProduct(newProductName, newProductCategory, newProductPrice)) {
                    //update the doc in firebase
                    productsCollectionRef.document(productID!!).update(
                        mapOf(
                            "name" to newProductName,
                            "category" to newProductCategory.lowercase(),
                            "price" to newProductPrice
                        )
                    )
                    Toast.makeText(this, "Product updated!", Toast.LENGTH_SHORT).show()

                    updateDialog.dismiss()
                    listenerRegistration.remove()
                    finish()
                    startActivity(intent)
                    overridePendingTransition(0, 0)

                }
            }
            //dismiss/cancel update dialog
            val tvCancelUpdate = updateDialog.findViewById<TextView>(R.id.tv_cancel_update)
            tvCancelUpdate.setOnClickListener {
                searchView.onActionViewCollapsed()
                updateDialog.dismiss()
            }
            updateDialog.show()
        }
        showItemDialog.show()
    }
}

private fun isExisting(name: String): Boolean {
    val productNameList = suggestions.toMutableList()
    return productNameList.contains(name)
}

private fun validateProduct(name: String, category: String, price: Double): Boolean {
    return when {
        TextUtils.isEmpty(name) -> {
            showErrorSnackBar(binding.root, "Product name cannot be empty.", true)
            false
        }

        category.uppercase() == "PLEASE SELECT FROM THE FF--" -> {
            showErrorSnackBar(binding.root, "Select a valid category.", true)
            false
        }

        price <= 0 -> {
            showErrorSnackBar(binding.root, "Price cannot be less than or equal to 0.", true)
            false
        }

        isExisting(name) -> {
            showErrorSnackBar(binding.root, "Product with the same name already exist.", true)
            false
        }

        else -> {
            true
        }
    }
}

private fun replaceFragment(fragment: Fragment) {
    val transaction = supportFragmentManager.beginTransaction()
    transaction.replace(R.id.fragment_container, fragment)
    transaction.commit()
}

private fun manageImageSlider() {
    val slider = binding.imageSlider
    val imageList = ArrayList<SlideModel>()
    imageList.add(
        SlideModel(
            "https://static.vecteezy.com/system/resources/previews/000/177/333/original/vector-promotional-advertising-banner-poster-template-with-offer-detail.jpg",
            ScaleTypes.CENTER_CROP
        )
    )
    imageList.add(
        SlideModel(
            "https://images.vexels.com/media/users/3/152066/raw/cfa8213abe1afe20ccc88e935918db23-super-sale-online-shopping-banner.jpg",
            ScaleTypes.CENTER_CROP
        )
    )
    imageList.add(
        SlideModel(
            "https://static.vecteezy.com/system/resources/previews/000/176/887/original/creative-sale-banner-in-purple-and-yellow-color-with-offer-and-d-vector.jpg",
            ScaleTypes.CENTER_CROP
        )
    )
    imageList.add(
        SlideModel(
            "https://drive.google.com/uc?id=1gcFa1RUFLSsfdcd9puiskL70SHCLrvr5&export=download",
            ScaleTypes.CENTER_CROP
        )
    )
    imageList.add(
        SlideModel(
            "https://static.vecteezy.com/system/resources/previews/000/185/424/original/discount-voucher-poster-template-design-in-premium-style-vector.jpg",
            ScaleTypes.CENTER_CROP
        )
    )
    slider.setImageList(imageList)

}
}