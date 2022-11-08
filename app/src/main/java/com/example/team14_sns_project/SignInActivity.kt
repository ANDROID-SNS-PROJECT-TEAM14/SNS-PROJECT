package com.example.team14_sns_project

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.team14_sns_project.databinding.ActivitySignInBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SignInActivity : AppCompatActivity(){
    private lateinit var binding : ActivitySignInBinding

    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var passwordView: ImageView
    private lateinit var signInBtn : Button
    private lateinit var googleBtn : ImageView
    private lateinit var facebookBtn : ImageView
    private lateinit var gotoSignUpBtn : TextView

    private lateinit var userAddressPick : String  // 사용자가 선택한 이메일 주소 (ex) @naver.com)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEditText = binding.signInEmailEditText
        passwordEditText = binding.signInPassswordEditText
        passwordView = binding.signInPasswordView
        signInBtn = binding.signInBtn
        googleBtn = binding.signInGoogleBtn
        facebookBtn = binding.signInFaceBookBtn
        gotoSignUpBtn = binding.signInGoToSignUpBtn

        val spinner = binding.signInSpinner
        val emailAddress = arrayOf("@daum.net", "@gmail.com", "@korea.kr", "@kakao.com", "@nate.com", "@naver.com", "@outlook.com", "@tistory.com", "@yahoo.com")

        val spinnerAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.simple_spinner_item, emailAddress)
        spinnerAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)

        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // Spinner 아이템 선택
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View, position: Int, id: Long
            )
            {
                Log.w("Spinner 아이템 선택 : ", emailAddress[position])
                userAddressPick = emailAddress[position]
            }

            // 아무것도 선택 안했을 시
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                // @naver.com으로 설정
                userAddressPick = emailAddress[0]
            }
        }

        passwordView.setOnClickListener {

        }

        signInBtn.setOnClickListener {
            val userEmail = emailEditText.text.toString() + userAddressPick
            val password = passwordEditText.text.toString()
            doSignIn(userEmail, password)
        }

        googleBtn.setOnClickListener {

        }

        facebookBtn.setOnClickListener {

        }

        gotoSignUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        
    }

    private fun doSignIn(userEmail: String, password: String) {
        val logMessage = "User 이메일 : " + userEmail + "비밀번호 : " + password
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.w("로그인 성공", logMessage)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("email", userEmail)
                    intent.putExtra("password", password)
                    startActivity(intent)

                    finish()
                }
                else {
                    Log.w("로그인 실패", logMessage)

                    // warningDialog
                    // 이메일과 비밀번호를 입력 안했을 경우
                    if (userEmail == null || password == null) {

                    }
                    // 아이디와 비밀번호를 틀리게 입력한 경우
                    else {

                    }
                }
            }
    }

    override fun onStart() {
        super.onStart()
        emailEditText.text = null
        passwordEditText.text = null
    }
}

