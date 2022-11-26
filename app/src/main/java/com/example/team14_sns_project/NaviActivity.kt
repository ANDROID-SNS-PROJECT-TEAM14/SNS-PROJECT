package com.example.team14_sns_project

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.team14_sns_project.databinding.ActivityNaviBinding
import com.example.team14_sns_project.navi.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class NaviActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNaviBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    lateinit var userEmail : String
    lateinit var userId : String
    lateinit var userName : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fireStore = Firebase.firestore
        // 구글 로그인을 위해 intent로부터 userEmail값을 받아오는 것으로 함.
        userEmail = intent.getStringExtra("userEmail").toString()
        userId = ""
        userName = ""

        if((auth.currentUser?.displayName != "" || auth.currentUser?.displayName != null)
            && (auth.currentUser?.email == "" || auth.currentUser?.email == null)) {
            val userCollection = fireStore.collection("Users")
            userCollection.get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if(document.data["name"] == auth.currentUser?.displayName) {
                            userEmail = document.id
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting collections.", exception)
                }
        }
        else {
            userEmail = auth.currentUser?.email.toString()
        }

        ActivityNaviBinding.inflate(layoutInflater).navigation.selectedItemId = R.id.homeFrag

        if(auth.currentUser?.displayName != null || auth.currentUser?.email != null) {
            setFragment(homeFragment())
            val userCollectionDoc = fireStore.collection("Users").document(userEmail)
            userCollectionDoc.get()
                .addOnSuccessListener {
                    userId = it["id"].toString()
                    userName = it["name"].toString()
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents.", exception)
                }
        }

        binding.navigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFrag -> {
                    setFragment(homeFragment())
                }
                R.id.postFrag-> {
                    val intent = Intent(this, AddPostActivity::class.java)
                    // 필요없으면 삭제
                    intent.putExtra("userEmail", userEmail)
                    intent.putExtra("userId", userId)
                    intent.putExtra("userName", userName)
                    startActivity(intent)
                }
                R.id.personAddFrag-> {
                    setFragment(personAddFragment())
                }
                R.id.myprofileFrag-> {
                    setFragment(myprofileFragment())
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Any) {
        supportFragmentManager.beginTransaction().replace(R.id.main_content, fragment as Fragment).commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeFragment().onDestroy()
    }
}