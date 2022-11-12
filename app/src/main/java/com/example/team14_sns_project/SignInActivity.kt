package com.example.team14_sns_project

import android.R
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.team14_sns_project.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity(){
    private lateinit var binding : ActivitySignInBinding

    // onStart()때 마다 입력칸을 초기화시키기 위해 전역변수로 선언
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    // 사용자가 선택한 이메일 주소 (ex) @naver.com)
    private lateinit var userAddressPick : String
    // google signIn
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEditText = binding.signInEmailEditText
        passwordEditText = binding.signInPassswordEditText

        val passwordView = binding.signInPasswordView
        val signInBtn = binding.signInBtn
        val googleBtn = binding.signInGoogleBtn
        val facebookBtn = binding.signInFaceBookBtn
        val gotoSignUpBtn = binding.signInGoToSignUpBtn
        val spinner = binding.signInSpinner

        /** +) 기타 입력도 추가하기*/
        val emailAddress = arrayOf("@daum.net", "@gmail.com", "@hansung.ac.kr", "@korea.kr", "@kakao.com", "@nate.com", "@naver.com", "@outlook.com", "@tistory.com", "@yahoo.com")

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
                // @daum.net으로 설정
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
            initGoogleSignIn()
            signInGoogle()
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

    private fun initGoogleSignIn() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            // default_client_id가 인식되지 않아 리소스를 그대로 복사
            .requestIdToken("16439493728-mbqgnvttk0hrgm23gq1obacsjs9fkr25.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    if(task.isSuccessful) {
                        try {
                            Log.w("구글 로그인 : ", "성공")
                            val account : GoogleSignInAccount? = task.result
                            if(account != null) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.putExtra("email", account.email)
                                intent.putExtra("username", account.displayName)
                                startActivity(intent)
                                finish()
                            }
                        } catch (e: Exception) {
                            Log.w("구글 로그인 : ", "실패")
                        }
                    }
                    // task failed
                    else {
                        Log.w("구글 resultCode : ", "오류")
                    }
            }
    }

    override fun onStart() {
        super.onStart()
        emailEditText.text = null
        passwordEditText.text = null
    }
}

