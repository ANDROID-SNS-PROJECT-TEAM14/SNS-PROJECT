package com.example.team14_sns_project

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.team14_sns_project.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var googleSignInClient: GoogleSignInClient

    private lateinit var userType : String
    private lateinit var userEmail : String
    private lateinit var userName : String
    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fireStore = Firebase.firestore

        userType = intent.getStringExtra("userType").toString()
        userEmail = intent.getStringExtra("userEmail").toString()

        val userCollectionDoc = fireStore.collection("Users").document(userEmail)
        userCollectionDoc.get()
            .addOnSuccessListener {
                userName = it["name"].toString()
                userId = it["id"].toString()
                binding.textView3.text = userName
                binding.textView4.text = userId
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }

        binding.textView.text = userType
        binding.textView2.text = userEmail

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // default_client_id가 인식되지 않아 리소스를 그대로 복사
            .requestIdToken("16439493728-mbqgnvttk0hrgm23gq1obacsjs9fkr25.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)


        binding.mainSignOutBtn.setOnClickListener {
            auth.signOut()
            googleSignInClient.signOut()
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

}