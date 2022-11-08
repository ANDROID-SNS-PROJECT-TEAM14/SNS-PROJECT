package com.example.team14_sns_project

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.team14_sns_project.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailEditText = binding.signUpEmailEditText
        val passwordEditText = binding.signUpPasswordEditText
        val signUpBtn = binding.signUpBtn


        signUpBtn.setOnClickListener {
            val logMessage = "User 이메일 : " + emailEditText.text.toString() + "비밀번호 : " + passwordEditText.text.toString()
            Log.w("signUp" , logMessage)
            Firebase.auth.createUserWithEmailAndPassword(emailEditText.text.toString(), passwordEditText.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        // task.result?.user => user
                        Log.w("회원가입 성공", logMessage)
                        finish()
                    }
                    else{
                        Log.w("회원가입 실패", logMessage)
                    }


            }
        }

    }
}