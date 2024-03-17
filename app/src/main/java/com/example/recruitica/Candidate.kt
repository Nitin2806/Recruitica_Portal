package com.example.recruitica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

interface OnCandidateClickListener {
    fun onCandidateClick(candidateData: CandidateData,uid:String)
}

class Candidate : AppCompatActivity(),OnCandidateClickListener {
    private lateinit var adapter: CandidateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.navigation_candidate
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

        val query = FirebaseDatabase.getInstance().reference.child("users")

        val options = FirebaseRecyclerOptions.Builder<CandidateData>()
            .setQuery(query, CandidateData::class.java)
            .build()


        adapter = CandidateAdapter(options,this)

        val rView: RecyclerView = findViewById(R.id.candidateRecyclerView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter
    }
    override fun onCandidateClick(candidateData: CandidateData, uid: String) {
        Log.d("Candidate",uid)
        val intent = Intent(this, Detail::class.java)
        intent.putExtra("candidateData", candidateData)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }
    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

}
