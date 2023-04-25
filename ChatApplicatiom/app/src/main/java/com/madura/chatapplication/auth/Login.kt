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
import com.madura.chatapplication.MainActivity
import com.madura.chatapplication.R


private const val TAG = "LoginActivity"
class Login : AppCompatActivity() {

    private lateinit var editEmail: EditText
    private lateinit var editPassword: EditText
    private lateinit var loginBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var activityLayout: ConstraintLayout

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        activityLayout = findViewById(R.id.activity_login)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()

        editEmail = findViewById(R.id.input_login_email)
        editPassword = findViewById(R.id.input_login_password)

        loginBtn = findViewById(R.id.login_btn_login)
        signUpBtn = findViewById(R.id.login_btn_signup)

        loginBtn.setOnClickListener {
            try {
                val email = editEmail.text.toString()
                val password = editPassword.text.toString()

                login(email, password)
            } catch (e: Exception) {
                Log.d(TAG, "Login Exception $e")
                Toast.makeText(this@Login, "Something went to wrong", Toast.LENGTH_SHORT).show()
            }

        }

        signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
        activityLayout.setOnClickListener {
            val view=this.currentFocus
            val imm =getSystemService(INPUT_METHOD_SERVICE)as InputMethodManager
            imm?.hideSoftInputFromWindow(view?.windowToken,0)
        }
        dummy(1)
    }

    private fun login(email: String, password: String) {
        try {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    Log.d(TAG, "Current User ---->>>>> ${user}")
                    startActivity(Intent(this@Login, MainActivity::class.java))
                    finish()
                } else {
                    Log.d(TAG, "Login Failed ${task.exception}")
                    Toast.makeText(this@Login, "User login failed", Toast.LENGTH_SHORT).show()
                }

            }
        } catch (e: Exception) {
            throw e
        }
    }

    private  fun dummy(id:Int){
        when(id){
            1->{
                editEmail.setText("test@mail.com")
                editPassword.setText("11111111")
            }
        2->{
            editEmail.setText("test2@mail.com")
            editPassword.setText("11111111")
        } 3->{
            editEmail.setText("test3@mail.com")
            editPassword.setText("11111111")
        }
        }
    }
}