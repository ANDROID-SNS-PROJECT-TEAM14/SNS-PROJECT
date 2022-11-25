package com.example.team14_sns_project

import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.team14_sns_project.databinding.ActivitySignUpGoogleBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class SignUpGoogleActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpGoogleBinding
    private var fireStore: FirebaseFirestore = Firebase.firestore

    private lateinit var userType : String
    private lateinit var userEmail : String
    private lateinit var userName : String
    private lateinit var userId : String

    private lateinit var userIdEditText: EditText
    private lateinit var userIdCheckText: TextView
    private lateinit var signUpGoogleBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userType = intent.getStringExtra("userType").toString()
        userEmail = intent.getStringExtra("userEmail").toString()
        userName = intent.getStringExtra("userName").toString()

        userIdEditText = binding.signUpGoogleIdEditText
        userIdCheckText = binding.signUpGoogleIdCheckText
        signUpGoogleBtn = binding.signUpGoogleBtn

        val idWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 아이디가 영어 정규 표현식에 적합한 경우
                if(Pattern.matches("^[a-zA-Z]*\$", userIdEditText.text.toString())) {
                    // 경고 문구 변경.
                    userIdCheckText.text = ""
                    // 버튼 활성화
                    signUpGoogleBtn.isEnabled = true
                    signUpGoogleBtn.setBackgroundColor(Color.parseColor("#023F96"))
                }
                // 아이디 입력 안 했을 시
                if(userIdEditText.text.toString() == "") {
                    // 버튼 무효화
                    signUpGoogleBtn.isEnabled = false
                    signUpGoogleBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                }
                // 아이디가 영어 정규 표현식에 어긋난 경우
                if(!Pattern.matches("^[a-zA-Z]*\$", userIdEditText.text.toString())) {
                    // 경고 문구 변경.
                    userIdCheckText.text = "영어로만 입력하시오."
                    // 경고 문구색 빨간색으로 변경.
                    userIdCheckText.setTextColor(Color.parseColor("#F44336"))
                    // 버튼 무효화
                    signUpGoogleBtn.isEnabled = false
                    signUpGoogleBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                }
            }
        }
        userIdEditText.addTextChangedListener(idWatcher)

        signUpGoogleBtn.setOnClickListener {
            // id 값이 비어있지 않으면
            if(binding.signUpGoogleIdEditText.text.toString() != "") {
                userId = binding.signUpGoogleIdEditText.text.toString()
                // 유저 hashMap 생성
                // password는 구글 로그인일때만 비워놓음
                val userMap = hashMapOf(
                    "type" to userType,
                    "email" to userEmail,
                    "password" to "",
                    "id" to userId,
                    "name" to userName
                )
                // Users collection 가져와서 user 정보 데이터베이스에 삽입
                val userCollection = fireStore.collection("Users")
                userCollection.document(userEmail).set(userMap)
                    .addOnCompleteListener{ task ->
                        if(task.isSuccessful){
                            Log.w("구글 유저 데이터베이스 삽입 : ", "성공")
                            val intent = Intent(this, NaviActivity::class.java)
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
                // 오류일 시
                val dialog = WarningDialog(99)
                dialog.show(supportFragmentManager, "warningDialog")
            }
        }
    }
}