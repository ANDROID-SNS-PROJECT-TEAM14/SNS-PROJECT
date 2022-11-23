package com.example.team14_sns_project

import android.R
import android.app.Activity
import android.content.ClipData
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.team14_sns_project.databinding.ActivitySignInBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import org.checkerframework.checker.units.qual.Length
import java.util.regex.Pattern


class SignInActivity : AppCompatActivity(){
    private lateinit var binding : ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    // onStart()때 마다 입력칸을 초기화시키기 위해 전역변수로 선언
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText : EditText
    // 사용자가 선택한 이메일 주소 (ex) @naver.com)
    private lateinit var userAddressPick : String
    // google signIn
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager

    private var emailFlag : Boolean = false
    private var pwFlag : Boolean = false
    private var flag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initGoogleSignIn()

        fireStore = Firebase.firestore
        auth = Firebase.auth
        emailEditText = binding.signInEmailEditText
        passwordEditText = binding.signInPassswordEditText
        callbackManager = CallbackManager.Factory.create()

        val passwordView = binding.signInPasswordView
        val signInBtn = binding.signInBtn
        val googleBtn = binding.signInGoogleBtn
        //val facebookBtn = binding.signInFaceBookBtn
        val passwordCheckText = binding.signInPwCheckText
        val gotoSignUpBtn = binding.signInGoToSignUpBtn
        val spinner = binding.signInSpinner

        /** +) 기타 입력도 가능하면 추가하기*/
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

        // email 변경 감지
        val emaildWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 이메일이 비어있지 않은 경우에 true
                emailFlag = emailEditText.text.toString() != ""
            }
        }
        emailEditText.addTextChangedListener(emaildWatcher)

        // password 변경 감지
        val passwordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 비밀번호가 6자리 이상인 경우
                if(passwordEditText.text.toString().length >= 6) {
                    // 경고 문구 변경.
                    passwordCheckText.text = ""
                    pwFlag = true
                }

                // 비밀번호가 6자리 미만인 경우
                else {
                    // 경고 문구 변경.
                    passwordCheckText.text = "6자리 이상 입력해주시오."
                    // 경고 문구색 빨간색으로 변경.
                    passwordCheckText.setTextColor(Color.parseColor("#F44336"))
                    pwFlag = false
                }
            }
        }
        passwordEditText.addTextChangedListener(passwordWatcher)

        // email과 password 변경 감지
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 버튼 활성화 조건에 해당되는 경우
                if( emailFlag && pwFlag) {
                    // 버튼 활성화
                    signInBtn.isEnabled = true
                    signInBtn.setBackgroundColor(Color.parseColor("#023F96"))
                }
                // 아닌 경우
                else {
                    // 버튼 무효화
                    signInBtn.isEnabled = false
                    signInBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                }
            }
        }
        emailEditText.addTextChangedListener(watcher)
        passwordEditText.addTextChangedListener(watcher)

        // 비밀번호 보이게 하는 버튼
        passwordView.setOnClickListener {
            /** 메소드 추가 예정 ... */
        }

        // 로그인 버튼
        signInBtn.setOnClickListener {
            val userEmail = emailEditText.text.toString() + userAddressPick
            val password = passwordEditText.text.toString()
            doSignIn(userEmail, password)
        }

        googleBtn.setOnClickListener {
            signInGoogle()
        }

        // facebook login 오류로 일단 보류

        /*
        facebookBtn.setOnClickListener {
            // Facebook에서 제공하는 LoginButton사용 안하기 때문에 LoginManager사용
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("email", "public_profile"))

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        if (loginResult != null) {
                            Log.w("loginResult : ", "onSuccess")
                            handleFacebookAccessToken(loginResult.accessToken)
                        }
                    }

                    override fun onCancel() {
                        // App code
                        Log.w("loginResult : ", "onCancel")
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
                        Log.w("loginResult : ", "onError")
                    }
                })
        }
        */
        // 회원가입 버튼
        gotoSignUpBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    // 이메일과 비밀번호로 로그인
    private fun doSignIn(userEmail: String, password: String) {
        val logMessage = "User 이메일 : " + userEmail + "비밀번호 : " + password
        Firebase.auth.signInWithEmailAndPassword(userEmail, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Log.w("로그인 성공", logMessage)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("userType", "emailLogin")
                    intent.putExtra("userEmail", userEmail)
                    startActivity(intent)
                    finish()
                }
                else {
                    Log.w("로그인 실패", logMessage)
                    // warningDialog or Toast Message
                    // 아이디와 비밀번호를 틀리게 입력했거나 기타 오류인 경우
                    Toast.makeText(this, "로그인 실패. 다시 시도하시오.", Toast.LENGTH_SHORT).show()
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

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful) {
                try {
                    Log.w("구글 로그인 : ", "성공")
                    val account: GoogleSignInAccount? = task.result
                    if (account != null) {
                        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                        auth.signInWithCredential(credential).addOnCompleteListener{
                            if(it.isSuccessful){
                                val userCollection = fireStore.collection("Users")
                                userCollection.get()
                                    .addOnSuccessListener { result ->
                                        for (document in result) {
                                            Log.w(ContentValues.TAG, "${document.id} => ${document.data}")
                                            // 데이터베이스에 이미 존재하면
                                            if (document.id == account.email.toString()) {
                                                Log.w("MainActivity 이동 : ", "성공")
                                                flag = true
                                                // 이미 계정아이디를 생성한 유저라면 바로 MainActivity로 이동
                                                val intent1 = Intent(this, MainActivity::class.java)
                                                intent1.putExtra("userType", "googleLogin")
                                                intent1.putExtra("userEmail", account.email.toString())
                                                startActivity(intent1)
                                                finish()
                                            }
                                        }
                                        // google sign한 user가 계정아이디가 없을 경우 SignUpGoogleActivity로 이동하여 생성
                                        if(!flag) {
                                            Log.w("SignUpGoogleActivity 이동 : ", "성공")
                                            val intent2 = Intent(this, SignUpGoogleActivity::class.java)
                                            intent2.putExtra("userType", "googleLogin")
                                            intent2.putExtra("userEmail", account.email.toString())
                                            intent2.putExtra("userName", account.displayName.toString())
                                            startActivity(intent2)
                                            finish()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(ContentValues.TAG, "Error getting documents.", exception)
                                    }
                            }
                            else{
                                Log.w("Sign 후 Activity 이동 : ", "실패")
                            }
                        }.addOnFailureListener{
                            Log.w("구글 로그인 : ", "실패")
                        }
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

    /*
    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.w("페이스북 로그인 ", "성공")
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    if (user != null) {
                        updateUI(user)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("페이스북 로그인 ", "실패")
                    updateUI(null)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user!= null) {
            Log.w("updateUI ", "성공")
            //val intent = Intent(this, MainActivity::class.java)
            //intent.putExtra("email", user.email)
            //intent.putExtra("username", user.displayName)
            //startActivity(intent)
        }
        else {
            Log.w("updateUI ", "실패")
        }
    }
    */

    override fun onStart() {
        super.onStart()
        emailEditText.text = null
        passwordEditText.text = null
        /*
        auth = Firebase.auth
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
        */
    }
}

