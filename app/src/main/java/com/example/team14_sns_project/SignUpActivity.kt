package com.example.team14_sns_project

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.team14_sns_project.databinding.ActivitySignUpBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private lateinit var fireStore: FirebaseFirestore

    // 사용자가 선택한 이메일 주소 (ex) @naver.com)
    private lateinit var userAddressPick : String
    private lateinit var type : String
    private var idFlag : Boolean = false
    private var nameFlag : Boolean = false
    private var pwFlag : Boolean = false
    private var pwCheckFlag : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fireStore = Firebase.firestore
        val userCollection = fireStore.collection("Users")

        val idEditText = binding.signUpIdEditText
        val idCheckText = binding.signUpIdCheckText
        val nameEditText = binding.signUpNameEditText
        val nameCheckText = binding.signUpNameCheckText
        val emailEditText = binding.signUpEmailEditText
        val passwordEditText = binding.signUpPassswordEditText
        val passwordOverSixText = binding.signUpPasswordOverSixText
        val passwordCheckEditText = binding.signUpPassswordEditCheckText
        val passwordCheckText = binding.signUpPasswordCheckText
        val signUpBtn = binding.signUpBtn

        val spinner = binding.signUpSpinner

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

        val idWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 아이디가 영어 정규 표현식에 적합한 경우
                if(Pattern.matches("^[a-zA-Z]*\$", idEditText.text.toString())) {
                    // 경고 문구 변경.
                    idCheckText.text = ""
                    idFlag = true
                }

                // 이름이 한글 또는 영어 정규 표현식에 어긋난 경우
                else {
                    // 경고 문구 변경.
                    idCheckText.text = "영어로만 입력하시오."
                    // 경고 문구색 빨간색으로 변경.
                    idCheckText.setTextColor(Color.parseColor("#F44336"))
                    idFlag = false
                }
            }
        }
        idEditText.addTextChangedListener(idWatcher)

        // name 변경 감지
        val nameWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 이름이 한글 또는 영어 정규 표현식에 적합한 경우
                if(Pattern.matches("^[가-힣]*\$", nameEditText.text.toString()) || Pattern.matches("^[a-zA-Z]*\$", nameEditText.text.toString())) {
                    // 경고 문구 변경.
                    nameCheckText.text = ""
                    nameFlag = true
                }

                // 이름이 한글 또는 영어 정규 표현식에 어긋난 경우
                else {
                    // 경고 문구 변경.
                    nameCheckText.text = "영어 혹은 한글로만 입력하시오."
                    // 경고 문구색 빨간색으로 변경.
                    nameCheckText.setTextColor(Color.parseColor("#F44336"))
                    nameFlag = false
                }
            }
        }
        nameEditText.addTextChangedListener(nameWatcher)

        // password 변경 감지
        val passwordWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 비밀번호가 6자리 이상인 경우
                if(passwordEditText.text.toString().length >= 6) {
                    // 경고 문구 변경.
                    passwordOverSixText.text = ""
                    pwFlag = true
                }

                // 비밀번호가 6자리 미만인 경우
                else {
                    // 경고 문구 변경.
                    passwordOverSixText.text = "6자리 이상 입력해주시오."
                    // 경고 문구색 빨간색으로 변경.
                    passwordOverSixText.setTextColor(Color.parseColor("#F44336"))
                    pwFlag = false
                }
            }
        }
        passwordEditText.addTextChangedListener(passwordWatcher)

        // password와 passwordCheck 일치 감지
        val passwordCheckWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 비밀번호가 비밀번호 확인이랑 같은 경우
                if(passwordEditText.text.toString() == passwordCheckEditText.text.toString()) {
                    // 경고 문구 변경.
                    passwordCheckText.text = "비밀번호가 일치합니다."
                    // 경고 문구색 초록색으로 변경.
                    passwordCheckText.setTextColor(Color.parseColor("#4CAF50"))
                    pwCheckFlag = true
                }

                // 비밀번호가 비밀번호 확인이랑 다른 경우
                else {
                    // 경고 문구 변경.
                    passwordCheckText.text = "비밀번호가 다릅니다."
                    // 경고 문구색 빨간색으로 변경.
                    passwordCheckText.setTextColor(Color.parseColor("#F44336"))
                    pwCheckFlag = false
                }
            }
        }

        // 비밀번호 입력 필드와 비밀번호 확인 입력 필드 모두에 텍스트 감지 설정.
        // 둘 중 아무 입력 필드에 입력해도 텍스트 감지하여 일치 여부 확인 가능하도록 함.
        passwordEditText.addTextChangedListener(passwordCheckWatcher)
        passwordCheckEditText.addTextChangedListener(passwordCheckWatcher)

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 버튼 활성화 조건에 해당되는 경우
                if(idFlag && nameFlag && pwFlag && pwCheckFlag) {
                    // 버튼 활성화
                    signUpBtn.isEnabled = true
                    signUpBtn.setBackgroundColor(Color.parseColor("#023F96"))
                }
                // 아닌 경우
                else {
                    // 버튼 무효화
                    signUpBtn.isEnabled = false
                    signUpBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                }
            }
        }
        idEditText.addTextChangedListener(watcher)
        nameEditText.addTextChangedListener(watcher)
        passwordEditText.addTextChangedListener(watcher)
        passwordCheckEditText.addTextChangedListener(watcher)


        signUpBtn.setOnClickListener {
            val logMessage =
                "User Email : " + emailEditText.text.toString() + userAddressPick +
                        "User Pw : " + passwordEditText.text.toString() +
                        "User Id : " + idEditText.text.toString() +
                        "User Name : " + nameEditText.text.toString()

            Log.w("[signUp] : " , logMessage)
            Firebase.auth.createUserWithEmailAndPassword(emailEditText.text.toString()+userAddressPick, passwordEditText.text.toString())
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        // task.result?.user => user
                        Log.w("회원가입 성공", logMessage)
                        val userMap = hashMapOf(
                            "type" to "emailLogin",
                            "email" to emailEditText.text.toString()+userAddressPick,
                            "password" to passwordEditText.text.toString(),
                            "id" to idEditText.text.toString(),
                            "name" to nameEditText.text.toString()
                        )
                        userCollection.document(emailEditText.text.toString()+userAddressPick).set(userMap)
                            .addOnSuccessListener {
                                Log.w("유저 데이터베이스 삽입 : ", "성공")
                            }.addOnFailureListener {
                                Log.w("유저 데이터베이스 삽입 : ", "실패")
                            }
                        intent = Intent(this, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.w("회원가입 실패", logMessage)
                    }
                }
        }
    }
}