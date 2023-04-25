package com.madura.chatapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.madura.chatapplication.adapters.UserAdapter
import com.madura.chatapplication.auth.Login
import com.madura.chatapplication.models.User

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var users: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()

        users = ArrayList()
        adapter = UserAdapter(this, users)

        userRecyclerView = findViewById(R.id.user_rv)
        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        getUsersFromFireStore()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mAuth.signOut()
            startActivity(Intent(this@MainActivity, Login::class.java))
            finish()
            return true;
        }
        return true
    }

    private fun getUsersFromFireStore() {
        val docRef = db.collection("Users")
        users.clear()
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d(TAG, "getUsersFromFireStore $e")
                throw e
            }

            if (snapshot != null) {
                snapshot.documents.forEach { document ->
                    Log.d(TAG, document.id)
                    users.add(
                        User(
                            document.data!!["userName"].toString(),
                            document.data!!["email"].toString(),
                            document.id
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            } else {
                Log.d(TAG, "No data found")
            }

        }
    }
}