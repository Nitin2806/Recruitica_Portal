package com.example.recruitica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase

interface OnCandidateClickListener {
    fun onCandidateClick(candidateData: CandidateData)
}

class Candidate : AppCompatActivity(),OnCandidateClickListener {
    private lateinit var adapter: CandidateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_candidate)

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

        val query = FirebaseDatabase.getInstance().reference.child("users")

        val options = FirebaseRecyclerOptions.Builder<CandidateData>()
            .setQuery(query, CandidateData::class.java)
            .build()


        adapter = CandidateAdapter(options,this)

        val rView: RecyclerView = findViewById(R.id.candidateRecyclerView)
        rView.layoutManager = LinearLayoutManager(this)
        rView.adapter = adapter
    }
    override fun onCandidateClick(candidateData: CandidateData) {
        val intent = Intent(this, Detail::class.java)
        intent.putExtra("candidateData", candidateData)
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