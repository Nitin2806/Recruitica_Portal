package com.example.recruitica
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class Signup : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference
    private var userCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()
        usersRef = database.getReference("users")

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("SignUp", dataSnapshot.toString())
                userCount = dataSnapshot.childrenCount.toInt()
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        val emailEditText: EditText = findViewById(R.id.emailEditText)
        val passwordEditText: EditText = findViewById(R.id.passwordEditText)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirmPasswordEditText)
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val genderEditText: EditText = findViewById(R.id.genderEditText)
        val locationEditText: EditText = findViewById(R.id.locationEditText)
        val signupButton: Button = findViewById(R.id.signupButton)

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val name = nameEditText.text.toString()
            val gender = genderEditText.text.toString()
            val location = locationEditText.text.toString()

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (auth.currentUser != null) {
                Toast.makeText(this, "You are already logged in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Toast.makeText(applicationContext, "User already exists.", Toast.LENGTH_SHORT).show()
                    } else {

                        createUser(email, password, name, gender, location)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(applicationContext, "Database Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val loginButton: TextView = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }

    private fun createUser(email: String, password: String, name: String, gender: String, location: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userID = user?.uid ?: ""

                    writeUserDataToDatabase(userID, name, email, gender, location)

                    Toast.makeText(this, "Signup successful.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Signup failed. ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun writeUserDataToDatabase(userID: String, name: String, email: String, gender: String, location: String) {
        val userData = HashMap<String, Any>()
        userData["name"] = name
        userData["email"] = email
        userData["gender"] = gender
        userData["location"] = location
        userData["company"] = "Recruitica"
        userData["bio"] = "My New Profile on Recruitica"
        userData["photo"] = "https://firebasestorage.googleapis.com/v0/b/recruitica-8c2be.appspot.com/o/logo.png?alt=media&token=9ec2840c-c6d7-4ac0-a75a-c2dbce9f3715"
        userData["userID"] = userCount + 1

        usersRef.child(userID).setValue(userData)
            .addOnSuccessListener {
            }
            .addOnFailureListener {
            }
    }
}
