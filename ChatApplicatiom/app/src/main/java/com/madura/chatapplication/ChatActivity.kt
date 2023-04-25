package com.madura.chatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.madura.chatapplication.adapters.MessageAdapter
import com.madura.chatapplication.models.Message
import com.madura.chatapplication.models.User

private const val TAG = "ChatActivity"

class ChatActivity : AppCompatActivity() {
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var chatLayout: RelativeLayout
    private lateinit var messageBox: EditText
    private lateinit var sendButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messages: ArrayList<Message>

    private var db = Firebase.firestore

    var receiverRoom: String? = null
    var senderRoom: String? = null
    var senderUid: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        chatLayout = findViewById(R.id.chat_layout)
        messages = ArrayList()

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        senderUid = FirebaseAuth.getInstance().currentUser?.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        supportActionBar?.title = name
        messageRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendButton = findViewById(R.id.sent_btn)
        messageAdapter = MessageAdapter(this, messages)


        getMessage()

        messageRecyclerView.layoutManager = LinearLayoutManager(this)
        messageRecyclerView.adapter = messageAdapter

        sendButton.setOnClickListener { sendMessage() }
        chatLayout.setOnClickListener {
            val view = this.currentFocus
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm?.hideSoftInputFromWindow(view?.windowToken, 0)
        }


    }

    private fun sendMessage() {
        try {
            val message = messageBox.text.toString()
            val messageObject = Message(message, senderUid)

            db.collection("Chats").document(senderRoom!!).collection("messages").add(messageObject)
                .addOnSuccessListener { documentRef ->
                    Log.d(
                        TAG,
                        "DocumentSnapshot added with ID: ${documentRef.id}"
                    )
                    messageBox.text.clear()
                }.addOnFailureListener { e ->
                    Log.d(
                        TAG,
                        "addOnFailureListener: $e"

                    )
                    messageBox.text.clear()
                }
        } catch (e: Exception) {
            Log.d(TAG, "Error $e")

        }
    }

    private fun getMessage() {
        val docRef = db.collection("Chats").document(senderRoom!!).collection("messages")

        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.d(TAG, "getChatFromFireStore $e")
                throw e
            }

            if (snapshot != null) {
                messages.clear()
                snapshot.documents.forEach { document ->
                    Log.d(TAG, document.id)
                    messages.add(
                        Message(
                            document.data!!["message"].toString(),
                            document.data!!["senderId"].toString()
                        )
                    )

                }
                messageAdapter.notifyDataSetChanged()
            } else {
                Log.d(TAG, "No data found")
            }

        }
    }
}