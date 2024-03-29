package com.example.recruitica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.*

class Detail : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: PostAdapter
    private lateinit var postsRecyclerView: RecyclerView
    private lateinit var userID: String
    private lateinit var nameTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var imgPhoto: ImageView
    private lateinit var bioTextView: TextView
    private lateinit var connectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(this, Login::class.java))
            finish()
            return
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.navigation_candidate

        //Check for loggedIn user to load menu
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
        val menuResId = if (isLoggedIn) R.menu.bottom_nav_menu_logged_out else  R.menu.bottom_nav_menu
        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(menuResId)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> startActivity(Intent(this, MainActivity::class.java))
                R.id.navigation_login -> startActivity(Intent(this, Login::class.java))
                R.id.navigation_candidate -> startActivity(Intent(this, Candidate::class.java))
                R.id.navigation_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(Intent(this, Login::class.java))
                    finishAffinity()
                }
            }
            true
        }

        nameTextView = findViewById(R.id.nameTextView)
        companyTextView = findViewById(R.id.companyTextView)
        locationTextView = findViewById(R.id.locationTextView)
        bioTextView = findViewById(R.id.bioTextView)
        connectButton = findViewById(R.id.connectButton)
        imgPhoto= findViewById(R.id.profileImageView)

        mDatabase = FirebaseDatabase.getInstance().reference

        mAuth = FirebaseAuth.getInstance()

        val currentUser = mAuth.currentUser
        val candidateData = intent.getParcelableExtra<CandidateData>("candidateData")
        val uid= intent.getStringExtra("uid")
        if (currentUser != null) {
            Log.d("Detail", currentUser.toString())
            userID = currentUser.uid
        }
        candidateData?.let { data ->
            if (uid != null) {
                displayUserDetails(uid)
            }
            displayPosts(data.userID.toString())
            checkConnectionStatus(data.userID.toString())
        }

        connectButton.setOnClickListener {
            val currentUserID = auth.currentUser!!.uid
            val otherUserID = candidateData?.userID.toString()

            val connectionRef = mDatabase.child("connections").child(currentUserID).child(otherUserID)
            connectionRef.setValue(true)
                .addOnSuccessListener {
                    Log.d(TAG, "Connection added successfully")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to add connection: ${e.message}")
                }
        }
    }

    private fun displayUserDetails(userID: String) {
        Log.d("Details on fetchAndDisplayUserDetails UserID ", userID)
        mDatabase.child("users").child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Details on fetchAndDisplayUserDetails ", snapshot.toString())
                    val user = snapshot.getValue(CandidateData::class.java)
                    Log.d("Details on fetchAndDisplayUserDetails ", user.toString())
                    user?.let {
                        updateUIWithUserData(user)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read user details: ${error.message}")
                }
            })
    }


    private fun displayPosts(userID: String) {

        val postsQuery = FirebaseDatabase.getInstance().reference.child("posts")

        postsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = mutableListOf<PostData>()

                for (postSnapshot in snapshot.children) {

                    val post = postSnapshot.getValue(PostData::class.java)

                    if (post?.userID == userID) {
                        post.let {
                            postList.add(it)
                        }
                    }
                }

                displayPosts(postList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to read user posts: ${error.message}")
            }
        })
    }

    private fun updateUIWithUserData(user: CandidateData) {
        Log.d("Details", user.toString())
        nameTextView.text = user.name
        companyTextView.text = user.company
        locationTextView.text = user.location
        bioTextView.text = user.bio
        Glide.with(this)
            .load(user.photo)
            .into(imgPhoto)

        Log.d(TAG, "UI updated with user data")
    }
    private fun checkConnectionStatus(otherUserID: String) {
        val currentUserID = auth.currentUser!!.uid
        val connectionRef = mDatabase.child("connections").child(currentUserID).child(otherUserID)
        connectionRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    connectButton.visibility = View.GONE
                } else {
                    connectButton.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to check connection status: ${error.message}")
            }
        })
    }
    private fun displayPosts(posts: List<PostData>) {
        postsRecyclerView = findViewById(R.id.postsRecyclerView)
        postsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(posts)
        postsRecyclerView.adapter = adapter
        Log.d(TAG, "Posts displayed")
    }

    companion object {
        private const val TAG = "Detail"
    }
}