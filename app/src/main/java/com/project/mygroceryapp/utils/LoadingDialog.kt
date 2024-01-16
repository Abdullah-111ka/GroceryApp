package com.project.mygroceryapp.utils

import android.app.Activity
import android.app.AlertDialog
import com.project.mygroceryapp.R

class LoadingDialog(private val activity : Activity) {
    private lateinit var dialog : AlertDialog

    fun startLoading(){
        val inflater = activity.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_loading,null)

        //set up alertdialog
        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }
    fun dismiss(){
        dialog.dismiss()
    }

}