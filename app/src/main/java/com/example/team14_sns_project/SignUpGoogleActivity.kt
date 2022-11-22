package com.example.team14_sns_project

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.team14_sns_project.databinding.ActivitySignUpGoogleBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpGoogleActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpGoogleBinding
    private lateinit var userType : String
    private lateinit var userEmail : String
    private lateinit var userName : String
    private lateinit var userId : String
    private var fireStore: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra("userType").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        userName = intent.getStringExtra("userName").toString()



        binding.signUpGoogleBtn.setOnClickListener {
            // id 값이 비어있지 않으면
            if(binding.signUpGoogleIdEditText.text.toString() != "") {
                userId = binding.signUpGoogleIdEditText.text.toString()
                val userMap = hashMapOf(
                    "type" to userType,
                    "email" to userEmail,
                    "password" to "",
                    "id" to userId,
                    "name" to userName
                )
               val userCollection = fireStore.collection("Users")
                userCollection.document(userEmail).set(userMap)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Log.w("구글 유저 데이터베이스 삽입 : ", "성공")
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("userType", "googleLogin")
                        intent.putExtra("userEmail", userEmail)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener{
                    Log.w("구글 유저 데이터베이스 삽입 : ", "실패")
                    Log.w("구글 유저 데이터베이스 삽입 실패 이유: ", "$userEmail $userType $userId $userName")
                }
            }
            else {
                // warning
            }
        }
    }


}