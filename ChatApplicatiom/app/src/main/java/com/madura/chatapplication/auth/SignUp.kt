package com.madura.chatapplication.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.madura.chatapplication.MainActivity
import com.madura.chatapplication.R

private const val TAG = "SignUpActivity"

class SignUp : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var signUpBtn: Button
    private lateinit var activityLayout: ConstraintLayout

    private lateinit var mAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        activityLayout = findViewById(R.id.activity_signup)

        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()


        editName = findViewById(R.id.input_signup_name)
        editEmail = findViewById(R.id.input_signup_email)
        editPassword = findViewById(R.id.input_signup_password)

        signUpBtn = findViewById(R.id.signup_btn_signup)


        signUpBtn.setOnClickListener {
            try {
                val email = editEmail.text.toString()
                val password = editPassword.text.toString()
                val name = editName.text.toString()

                signUp(email, password, name)
            } catch (e: Exception) {
                Log.d(TAG, "SignUp Exception $e")
                Toast.makeText(this@SignUp, "Something went to wrong", Toast.LENGTH_SHORT).show()

            }

        }

        activityLayout.setOnClickListener {
            val view = this.currentFocus
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    private fun signUp(email: String, password: String, name: String) {
        try {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        addUserToDatabase(name, email, user?.uid)
                        startActivity(Intent(this@SignUp, MainActivity::class.java))
                        finish()
                    } else {
                        Log.d(TAG, "SignupFailed")
                        Toast.makeText(this@SignUp, "User signup failed", Toast.LENGTH_SHORT).show()
                    }

                }
        } catch (e: Exception) {
            throw e
        }

    }

    private fun addUserToDatabase(name: String, email: String, uid: String?) {
        try {
            var user = hashMapOf("uid" to uid, "userName" to name, "email" to email)

            db.collection("Users").add(user).addOnSuccessListener { documentRef ->
                Log.d(
                    TAG,
                    "DocumentSnapshot added with ID: ${documentRef.id}"
                )
            }.addOnFailureListener{
                e-> Log.d(
                TAG,
                "DocumentSnapshot added with ID: $e"
            )
            }
        } catch (e: Exception) {

        }
    }

}