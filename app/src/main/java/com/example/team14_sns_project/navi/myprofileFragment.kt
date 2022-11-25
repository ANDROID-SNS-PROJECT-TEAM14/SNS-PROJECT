package com.example.team14_sns_project.navi

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.team14_sns_project.NaviActivity
import com.example.team14_sns_project.R
import com.example.team14_sns_project.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 내 프로필 화면
class myprofileFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    private lateinit var userEmail : String
    private lateinit var userId : String
    private lateinit var userName : String

    private lateinit var userIdEditText: TextView
    private lateinit var userNameEditText: TextView
    private lateinit var userFollowerEditText: TextView
    private lateinit var userFollowingEditText: TextView
    private lateinit var logOutBtn : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_myprofile, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // NaviActivity에 저장되어 있는 user 정보 가져옴
        val parent = activity as NaviActivity
        userEmail = parent.userEmail
        userId = parent.userId
        userName = parent.userName

        userIdEditText = view.findViewById(R.id.myProfileUserId)
        userNameEditText = view.findViewById(R.id.myProfileUserName)
        userFollowerEditText = view.findViewById(R.id.myProfileFollwer)
        userFollowingEditText = view.findViewById(R.id.myProfileFollowing)
        logOutBtn = view.findViewById(R.id.myProfileLogOutBtn)

        // NaviActivity에 저장되어 있는 userId와 userName 가져옴
        userIdEditText.text = "@" + userId
        userNameEditText.text = userName

        // 유저의 팔로우/팔로잉 FireStoreUrl
        val userFollowerCollection = fireStore.collection("Users").document(userEmail).collection("Follower")
        val userFollowingCollection = fireStore.collection("Users").document(userEmail).collection("Following")

        userFollowerCollection.get()
            .addOnSuccessListener { result ->
                Log.w("userFollowerCollection.get()", "userFollowerCollection 성공")
                // 아직 팔로워가 없다면 0으로 설정
                if(result.size() == null) {
                    userFollowerEditText.text = "0"
                }
                // 팔로워가 있다면 그 수 만큼 설정
                else {
                    userFollowerEditText.text = result.size().toString()
                }
            }.addOnFailureListener{
                Log.w("userFollowerCollection.get()", "userFollowerCollection 실패")
            }

        userFollowingCollection.get()
            .addOnSuccessListener { result ->
                Log.w("userFollowingCollection.get()", "userFollowingCollection 성공")
                // 아직 팔로잉이 없다면 0으로 설정
                if(result.size() == null) {
                    userFollowingEditText.text = "0"
                }
                // 팔로워가 있다면 그 수 만큼 설정
                else {
                    userFollowingEditText.text = result.size().toString()
                }

            }.addOnFailureListener{
                Log.w("userFollowingCollection.get()", "userFollowingCollection 실패")
            }

        // signOut 용도로 선언
        val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        val googleSignInClient = GoogleSignIn.getClient(view.context, option)

        // 로그아웃 버튼
        logOutBtn.setOnClickListener {
            //fragment라서 activity intent와는 다른 방식
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
            googleSignInClient.signOut()
            FirebaseAuth.getInstance().signOut()
        }

        return view
    }

    // 친구 추가 후 프로필 Fragment가 보여질 때 팔로우/팔로잉 목록이 업데이트 될 수 있도록
    override fun onStart() {
        super.onStart()

        // 유저의 팔로우/팔로잉 FireStoreUrl
        val userFollowerCollection = fireStore.collection("Users").document(userEmail).collection("Follower")
        val userFollowingCollection = fireStore.collection("Users").document(userEmail).collection("Following")

        userFollowerCollection.get()
            .addOnSuccessListener { result ->
                Log.w("userFollowerCollection.get()", "userFollowerCollection 성공")
                // 아직 팔로워가 없다면 0으로 설정
                if(result.size() == null) {
                    userFollowerEditText.text = "0"
                }
                // 팔로워가 있다면 그 수 만큼 설정
                else {
                    userFollowerEditText.text = result.size().toString()
                }
            }.addOnFailureListener{
                Log.w("userFollowerCollection.get()", "userFollowerCollection 실패")
            }

        userFollowingCollection.get()
            .addOnSuccessListener { result ->
                Log.w("userFollowingCollection.get()", "userFollowingCollection 성공")
                // 아직 팔로잉이 없다면 0으로 설정
                if(result.size() == null) {
                    userFollowingEditText.text = "0"
                }
                // 팔로워가 있다면 그 수 만큼 설정
                else {
                    userFollowingEditText.text = result.size().toString()
                }

            }.addOnFailureListener{
                Log.w("userFollowingCollection.get()", "userFollowingCollection 실패")
            }
    }
}