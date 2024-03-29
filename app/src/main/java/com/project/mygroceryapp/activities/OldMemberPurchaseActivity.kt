package com.project.mygroceryapp.activities

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.mygroceryapp.adapter.ProductAdapter
import com.project.mygroceryapp.model.Cart
import com.project.mygroceryapp.model.OldMember
 
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.project.mygroceryapp.R
import com.project.mygroceryapp.databinding.ActivityOldMemberPurchaseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class OldMemberPurchaseActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityOldMemberPurchaseBinding
    private val productCollectionRef = Firebase.firestore.collection("products")


    override fun onBackPressed() {
        super.onBackPressed()
        showAlertDialog()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOldMemberPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)



        showCustomerData()
        setButtonClick()

        binding.customerCartOld.setOnClickListener{
            Intent(this@OldMemberPurchaseActivity, OldMemberCartActivity::class.java).also {
                startActivity(it)
            }
        }

    }
    private fun setButtonClick() {
        //back navigation
        binding.sideBarPurchaseOrderBackOld.setOnClickListener {
            showAlertDialog()
        }

        binding.btnAmaxLoad.setOnClickListener(this)
        binding.btnBeverages.setOnClickListener(this)
        binding.btnCannedGoods.setOnClickListener(this)
        binding.btnCigarettesMed.setOnClickListener(this)
        binding.btnCondiments.setOnClickListener(this)
        binding.btnDetergent.setOnClickListener(this)
        binding.btnDiapers.setOnClickListener(this)
        binding.btnHousehold.setOnClickListener(this)
        binding.btnNoodles.setOnClickListener(this)
        binding.btnPowderedMilk.setOnClickListener(this)
        binding.btnShampooConditioner.setOnClickListener(this)
        binding.btnSoapHygiene.setOnClickListener(this)
        binding.btnSugar.setOnClickListener(this)
        binding.btnBisquits.setOnClickListener(this)


    }

    private fun showCustomerData() {
        binding.tvCustomerNameOld.text = "${OldMember.lastName}, ${OldMember.firstName}"

    }

    private fun showAlertDialog() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Return to Dashboard?")
            .setTitle("Leave Purchase Order")
            .setPositiveButton("YES", DialogInterface.OnClickListener { _, _
                ->
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                }
                Cart.getList().clear()
                finish()
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener { dialog, _
                ->
                dialog.dismiss()
            })
        alertDialog.show()
    }

    private fun showProductDialog(button : Button){
        //extract product info selected from button

        //show dialog
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_product)
        dialog.setCancelable(false)
        //to dismiss
        val btnReturn = dialog.findViewById< Button>(R.id.btn_return)
        btnReturn.setOnClickListener{
            dialog.dismiss()
        }


        //set text and icon
        val titleText = dialog.findViewById<TextView>(R.id.tv_product_selected)
        val category = button.text.toString()

        val tvEmptyList = dialog.findViewById<TextView>(R.id.tv_list_empty)

        //get text of button, set to titleText
        titleText.text = category.uppercase()

        //get all products within the category clicked
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val querySnapshot = productCollectionRef.get().await()
                withContext(Dispatchers.Main){
                    val list = mutableListOf<DocumentSnapshot>()
                    for(document in querySnapshot.documents){
                        if(document["category"] == category.lowercase()){
                            list.add(document)
                        }
                    }
                    val recyclerView = dialog.findViewById<RecyclerView>(R.id.rv_products)
                    if(list.isEmpty()){
                        recyclerView.isVisible = false
                        tvEmptyList.isVisible = true
                    }else{
                        recyclerView.layoutManager = LinearLayoutManager(this@OldMemberPurchaseActivity)
                        val adapter = ProductAdapter(this@OldMemberPurchaseActivity, list, category)
                        recyclerView.adapter = adapter

                        //search
                        val etSearch = dialog.findViewById<TextInputEditText>(R.id.et_search)
                        etSearch.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {

                            }

                            override fun onTextChanged(
                                p0: CharSequence?,
                                p1: Int,
                                p2: Int,
                                p3: Int
                            ) {

                            }

                            override fun afterTextChanged(p0: Editable?) {
                                filter(list, p0, adapter)
                            }

                        })

                    }

                }

            }catch (e : Exception){
                withContext(Dispatchers.Main){
                    Toast.makeText(this@OldMemberPurchaseActivity, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        dialog.show()
    }

    private fun filter(
        list: MutableList<DocumentSnapshot>,
        p0: Editable?,
        adapter: ProductAdapter
    ) {
        val temp = mutableListOf<DocumentSnapshot>()
        for (item in list) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (item["name"].toString().contains(p0.toString().lowercase())) {
                temp.add(item)
            }
        }
        //update recyclerview
        adapter.updateList(temp)
    }


    override fun onClick(p0: View?) {
        showProductDialog(p0 as Button)
    }

}