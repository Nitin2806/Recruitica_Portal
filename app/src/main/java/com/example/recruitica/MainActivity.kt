package com.example.recruitica

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var mDatabase: DatabaseReference
    private lateinit var adapter: PostAdapter
    private lateinit var postsRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance().reference
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        val menuResId = if (isLoggedIn) R.menu.bottom_nav_menu_logged_out else  R.menu.bottom_nav_menu

        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(menuResId)


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
                R.id.navigation_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    true
                }

                else -> false
            }
        }
        postsRecyclerView = findViewById(R.id.postsRecyclerView)
        postsRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter(ArrayList())
        postsRecyclerView.adapter = adapter
        loadPostsFromFirebase()
    }
    private fun loadPostsFromFirebase() {
        val currentUserID = auth.currentUser?.uid
        if (currentUserID != null) {
            mDatabase.child("connections").child(currentUserID)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val connections = snapshot.children
                        val posts = mutableListOf<PostData>()

                        connections.forEach { connectionSnapshot ->
                            val userID = connectionSnapshot.key
                            mDatabase.child("posts").orderByChild("userID").equalTo(userID)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(postsSnapshot: DataSnapshot) {
                                        postsSnapshot.children.forEach { postSnapshot ->
                                            val post = postSnapshot.getValue(PostData::class.java)
                                            post?.let {
                                                posts.add(it)
                                            }
                                        }

                                        adapter.updateData(posts)
                                    }

                                    override fun onCancelled(error: DatabaseError) {

                                        Toast.makeText(
                                            this@MainActivity,
                                            "Failed to fetch posts: ${error.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                        Toast.makeText(
                            this@MainActivity,
                            "Failed to fetch connections: ${error.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
