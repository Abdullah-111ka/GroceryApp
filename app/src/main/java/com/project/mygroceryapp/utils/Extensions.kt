package com.project.mygroceryapp.utils

import android.content.Context
import com.project.mygroceryapp.model.UserModel

fun storeUserData(userModel: UserModel, ctx: Context) {
    // Get the Shared Preferences instance
    val sharedPreferences = ctx.getSharedPreferences(Constant.USER, Context.MODE_PRIVATE)
// Get the editor to modify preferences
    val editor = sharedPreferences.edit()
// Store data using key-value pairs
    editor.putString(Constant.FIRSTNAME, userModel.firstName)
    editor.putString(Constant.LASTNAME, userModel.lastName)
    editor.putString(Constant.EMAIL, userModel.email)
    editor.putString(Constant.PASSWORD, userModel.password)
    editor.putString(Constant.USERID, userModel.userID)
// Apply changes
    editor.apply()
}


fun fetchUserData(ctx: Context): UserModel {
    // Get the Shared Preferences instance
    val sharedPreferences = ctx.getSharedPreferences(Constant.USER, Context.MODE_PRIVATE)
// Fetch data using key-value pairs
    return UserModel(
        sharedPreferences.getString(Constant.FIRSTNAME, "").toString(),
        sharedPreferences.getString(Constant.LASTNAME, "").toString(),
        sharedPreferences.getString(Constant.EMAIL, "").toString(),
        sharedPreferences.getString(Constant.PASSWORD, "").toString(),
        sharedPreferences.getString(Constant.USERID, "").toString()
    )
}