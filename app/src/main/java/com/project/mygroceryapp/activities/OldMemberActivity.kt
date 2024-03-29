package com.project.mygroceryapp.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import com.project.mygroceryapp.model.OldMember
import com.project.mygroceryapp.R
import com.project.mygroceryapp.databinding.ActivityOldMemberBinding

class OldMemberActivity : BaseActivity() {
    private lateinit var binding : ActivityOldMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOldMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnPurchaseOld.setOnClickListener {
            if(validateName()){
                val lastname = binding.etOldMemberLastname.text.toString()
                val firstname = binding.etOldMemberFirstname.text.toString()

                //show dialog
                val dialog = Dialog(this)
                dialog.setContentView(R.layout.dialog_old_member_info)
                dialog.setCancelable(true)

                val dialogLastName = dialog.findViewById<TextView>(R.id.tv_lastname_old)
                val dialogFirstName = dialog.findViewById<TextView>(R.id.tv_firstname_old)

                dialogLastName.text = lastname.uppercase()
                dialogFirstName.text = firstname.uppercase()

                val btnOrder = dialog.findViewById<Button>(R.id.btn_order)
                btnOrder.setOnClickListener {

                    Intent(this, OldMemberPurchaseActivity::class.java).also {
                        OldMember.lastName = dialogLastName.text.toString()
                        OldMember.firstName = dialogFirstName.text.toString()
                        dialog.dismiss()
                        finish()
                        startActivity(it)
                    }
                }
                dialog.show()
            }
        }

    }

    private fun validateName() : Boolean {
        val etLastName = binding.etOldMemberLastname.text?.toString()?.trim()
        val etFirstName = binding.etOldMemberFirstname.text?.toString()?.trim()

        return when{
            TextUtils.isEmpty(etLastName) ->{
                showErrorSnackBar(binding.root, "Last Name cannot be empty", true)
                false
            }

            TextUtils.isEmpty(etFirstName) ->{
                showErrorSnackBar(binding.root, "First Name cannot be empty", true)
                false
            }

            else -> true

        }
    }
}