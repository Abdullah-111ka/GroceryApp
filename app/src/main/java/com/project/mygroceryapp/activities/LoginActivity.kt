package com.project.mygroceryapp.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.mygroceryapp.databinding.ActivityLoginBinding
import com.project.mygroceryapp.model.UserModel
import com.project.mygroceryapp.utils.storeUserData

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //check if user is already logged in
        val user = auth.currentUser
        if (user != null) {
            goToMainActivity()
        }

        binding.tvSendRequest.setOnClickListener {
//            composeEmail()
            Intent(this, SignUpActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            hideKeyboard(currentFocus ?: View(this))
            login(email, password, auth)
        }
    }

    private fun login(
        email: String,
        password: String,
        auth: FirebaseAuth
    ) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->

                    if (task.isSuccessful) {
                        task.result?.user?.let { fetchDataFromFirestore(it.uid) }
                    } else {
                        showErrorSnackBar(
                            binding.root,
                            "Account does not exist. Try again.",
                            true
                        )
                    }
                }
        } else {
            showErrorSnackBar(binding.root, "Email and Password cannot be empty!", true)
        }
    }

    private lateinit var firestore: FirebaseFirestore

    private fun fetchDataFromFirestore(userId: String) {
        // Get a reference to the "users" collection in Firestore
        val usersCollection = firestore.collection("users")

        // Get a reference to the document with the specified custom ID
        val userDocument = usersCollection.document(userId)

        // Fetch data from Firestore
        userDocument.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    // Document exists, retrieve data
                    val firstName = documentSnapshot.getString("firstName").toString()
                    val lastName = documentSnapshot.getString("lastName").toString()
                    val email = documentSnapshot.getString("email").toString()
                    val password = documentSnapshot.getString("password").toString()
                    val userID = documentSnapshot.getString("userID").toString()
                    storeUserData(UserModel(firstName, lastName, email, password, userID), this)
                    goToMainActivity()
                } else {
                    // Document does not exist
                    Toast.makeText(this, "User not found", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .addOnFailureListener { e ->
                // If data retrieval fails, display an error message
                Log.e("Firestore", "Error fetching data from Firestore: $e")
            }
    }

    private fun goToMainActivity() {
        Intent(this, MainActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }

    private fun composeEmail() {
        val to = "jygrzn@gmail.com"
        val subject = "[APAR APP]Request Access"
        val body =
            "I am sending you an email to request for an access for the APAR which I have installed.\n\n" +
                    "Kindly fill-out the following: \n" +
                    "Last Name: \n First Name: \n Middle Name: \n\n" +
                    "APAR no: \n" +
                    "Store Code: \n" +
                    "Region: SOUTH GMA\n" +
                    "Cluster number: \n\n" +
                    "For DEMO, just leave all spaces blank. This is to limit the access to avoid Firebase charges. " +
                    "Thank you!"

        val mailTo = "mailto:" + to +
                "?&subject=" + Uri.encode(subject) +
                "&body=" + Uri.encode(body)
        val emailIntent = Intent(Intent.ACTION_VIEW)
        emailIntent.data = Uri.parse(mailTo)
        startActivity(emailIntent)
    }
}
