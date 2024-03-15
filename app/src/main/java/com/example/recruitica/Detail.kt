package com.example.recruitica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class Detail : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: PostAdapter
    private lateinit var postsRecyclerView: RecyclerView

    private lateinit var nameTextView: TextView
    private lateinit var companyTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var bioTextView: TextView
    private lateinit var connectButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_candidate
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }

                R.id.navigation_login -> {
                    startActivity(Intent(this, Login::class.java))
                    true
                }

                R.id.navigation_candidate -> {
                    startActivity(Intent(this, Candidate::class.java))
                    true
                }

                else -> false
            }
        }

        nameTextView = findViewById(R.id.nameTextView)
        companyTextView = findViewById(R.id.companyTextView)
        locationTextView = findViewById(R.id.locationTextView)
        bioTextView = findViewById(R.id.bioTextView)
        connectButton = findViewById(R.id.connectButton)

        mDatabase = FirebaseDatabase.getInstance().reference
        val candidateData = intent.getParcelableExtra<CandidateData>("candidateData")
        candidateData?.let { data ->
            fetchAndDisplayUserDetails(data.userID.toString())
            fetchAndDisplayPosts(data.userID.toString())

        }
    }

    private fun fetchAndDisplayUserDetails(userID: String) {
        mDatabase.child("users").child(userID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(CandidateData::class.java)
                    user?.let {
                        updateUIWithUserData(user)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to read user details: ${error.message}")
                }
            })
    }

    private fun fetchAndDisplayPosts(userID: String) {

        val postsQuery = FirebaseDatabase.getInstance().reference.child("posts")

        postsQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val postList = mutableListOf<PostData>()

                for (postSnapshot in snapshot.children) {

                    val post = postSnapshot.getValue(PostData::class.java)


                    if (post?.userID == userID) {

                        post?.let {
                            postList.add(it)
                        }
                    }
                }

                Log.d("user PostList",postList.toString())


                displayPosts(postList)
            }

            override fun onCancelled(error: DatabaseError) {

                Log.e(TAG, "Failed to read user posts: ${error.message}")
            }
        })
    }


    private fun updateUIWithUserData(user: CandidateData) {

        nameTextView.text = user.name
        companyTextView.text = user.company
        locationTextView.text = user.location
        bioTextView.text = user.bio
        Log.d("user UI","UI UPdatted")
    }

    private fun displayPosts(posts: List<PostData>) {

        Log.d("user Post","Trying to display post")
        postsRecyclerView = findViewById(R.id.postsRecyclerView)
        postsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(posts)
        postsRecyclerView.adapter = adapter
        Log.d("user Post"," display post")
    }

    companion object {
        private const val TAG = "Detail"
    }
}
