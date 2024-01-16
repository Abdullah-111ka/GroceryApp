package com.project.mygroceryapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.project.mygroceryapp.databinding.ActivitySignUpBinding
import com.project.mygroceryapp.model.UserModel
import com.project.mygroceryapp.utils.storeUserData

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
// Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.apply {

            tvLogin.setOnClickListener {
                startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                finish()
            }

            btnLogin.setOnClickListener {
                registerUser(
                    UserModel(
                        firstName = edtFirstname.text.toString().trim(),
                        lastName = edtLastname.text.toString().trim(),
                        email = edtEmail.text.toString().trim(),
                        password = edtPassword.text.toString().trim()
                    )
                )
            }
        }


    }

    private fun registerUser(userModel: UserModel) {
        auth.createUserWithEmailAndPassword(userModel.email, userModel.password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign up success
                    userModel.userID = task.result?.user?.uid.toString()
                    saveDataToFirestore(userModel)
                } else {
                    // If sign up fails, display a message to the user.
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private lateinit var firestore: FirebaseFirestore

    private fun saveDataToFirestore(userModel: UserModel) {
        // Get a reference to the "users" collection in Firestore
        val usersCollection = firestore.collection("users")

        // Add the new user document to the "users" collection
        usersCollection.document(userModel.userID).set(userModel)
            .addOnSuccessListener { documentReference ->
                // Document added successfully
                storeUserData(userModel,this)
                Toast.makeText(this, "Data saved to Firestore successfully", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                // If document addition fails, display an error message
                Toast.makeText(this, "Error saving data to Firestore: $e", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    //to store user in realtime DB
    private fun saveDataToDatabase(userModel: UserModel) {
        // Get a reference to the "users" node in the database
        val usersRef = database.reference.child("users")

        // Create a unique key for the new user
        val userId = userModel.userID
        // Save the user data to the database
        usersRef.child(userId).setValue(userModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Data saved successfully
                    Toast.makeText(this, "User created successfully", Toast.LENGTH_SHORT).show()
                } else {
                    // If data saving fails, display an error message
                    Toast.makeText(this, "Error saving data to database", Toast.LENGTH_SHORT).show()
                }
            }
    }
}