package com.example.recruitica

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            Toast.makeText(this, "You are already logged in.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.selectedItemId = R.id.navigation_login
        val isLoggedIn = FirebaseAuth.getInstance().currentUser != null

        val menuResId = if (isLoggedIn) R.menu.bottom_nav_menu_logged_out else  R.menu.bottom_nav_menu

        bottomNavigationView.menu.clear()
        bottomNavigationView.inflateMenu(menuResId)

        bottomNavigationView.selectedItemId = R.id.navigation_login
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

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            loginUser(email, password)
        }

        val signupPageButton: TextView = findViewById(R.id.signupTextView)
        signupPageButton.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                Log.d("Auth ",email+password)
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
