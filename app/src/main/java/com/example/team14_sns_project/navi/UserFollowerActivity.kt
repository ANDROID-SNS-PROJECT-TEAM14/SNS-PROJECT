package com.example.team14_sns_project.navi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team14_sns_project.databinding.ActivityUserFollowerBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserFollowerActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUserFollowerBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    // 사용자의 검색된 Follower 리스트
    private lateinit var dataList : ArrayList<friendData>
    // 사용자의 Following 리스트
    private lateinit var userFriend : ArrayList<String>

    // 사용자의 Follower 정보
    private lateinit var followerEmail : String
    private lateinit var followerId : String
    private lateinit var followerName : String
    private lateinit var followerBtn : String

    private lateinit var userEmail : String
    private lateinit var userId : String
    private lateinit var userName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserFollowerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userEmail = intent.getStringExtra("userEmail").toString()
        userId = intent.getStringExtra("userId").toString()
        userName = intent.getStringExtra("userName").toString()

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // 검색된 결과를 보여줄 recyclerView
        val recyclerView = binding.userFollowerRecyclerView
        val layoutManager = LinearLayoutManager(this)

        // adapter
        val adapter = UserFollowerAdapter(this)
        // 현재 사용자의 정보들을 설정해준다.
        adapter.setUser(userEmail, userId, userName)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        // user의 Following Collection
        val userFollowing = fireStore.collection("Users").document(userEmail).collection("Following")
        // user의 Follower Collection
        val userFollower = fireStore.collection("Users").document(userEmail).collection("Follower")

        dataList = ArrayList()
        // Following 리스트 초기화
        userFriend = ArrayList()
        dataList.clear()

        // 사용자의 Following Collection을 따라 현재 시점에서의 사용자 Following을 가져옴
        userFollowing.get().addOnSuccessListener { result ->
            for (document in result) {
                Log.w("userFollowing.get()", "성공")
                // 사용자의 Follwer 리스트에 사용자의 Follwer들의 이메일 추가
                userFriend.add(document.id)
            }
            userFollower.get()
                .addOnSuccessListener { result ->
                    Log.w("userFollower.get()", "성공")
                    for (document in result) {
                        userFollower.document(document.id).get().addOnSuccessListener {
                            Log.w("userCollection.document(document.id).get()", "성공")
                            followerEmail = document.id
                            followerId = it["friendId"].toString()
                            followerName = it["friendName"].toString()
                            // 팔로우/팔로잉 버튼을 나타낼 수 있도록 이미 사용자가 팔로잉한 상태인지 구분한다
                            followerBtn = if(userFriend.contains(followerEmail)) {
                                "friend"
                            }else { "noFriend" }
                            Log.w("검색된 다른 유저들 정보 : ", "$followerEmail  $followerId  $followerName  $followerBtn")
                            val data = friendData(followerEmail, followerId, followerName, followerBtn)
                            dataList.add(data)
                            if(dataList.isNotEmpty()) {
                                adapter.updateList(dataList)
                            }
                        }.addOnFailureListener{
                            Log.w("userCollection.document(document.id).get()", "실패")
                        }
                    }
                }
                .addOnFailureListener { Log.w("userFollower.get()", "실패") }
        }.addOnFailureListener{
            Log.w("userFollowing.get()", "실패")
        }

        // 검색창
        val searchView = binding.userFollowerSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            // 텍스트 변화가 있을 때 마다 recyclerView 갱신하도록 함.
            override fun onQueryTextChange(newText: String): Boolean {
                // 결과가 중복되지 않도록 사용자의 Following 리스트 초기화
                userFriend.clear()
                // 사용자의 Following Collection을 따라 현재 시점에서의 사용자 Following을 가져옴
                userFollowing.get().addOnSuccessListener { result ->
                    for (document in result) {
                        Log.w("userFollowing.get()", "성공")
                        // 사용자의 Following 리스트에 사용자의 Following들의 이메일 추가
                        userFriend.add(document.id)
                    }
                }.addOnFailureListener{
                    Log.w("userFollowing.get()", "실패")
                }

                userFollower.get()
                    .addOnSuccessListener { result ->
                        Log.w("userCollection.get()", "성공")
                        for (document in result) {
                            userFollower.document(document.id).get().addOnSuccessListener {
                                Log.w("userCollection.document(document.id).get()", "성공")
                                // 다른 사용자들의 id나 name에 검색한 글자가 포함되어 있다면 목록에 보여줌
                                if((it["friendId"].toString().contains(newText) || it["friendName"].toString().contains(newText)) && it["friendId"].toString() != userId) {
                                    // 정확한 정보를 가지고 존재하는 계정만 보이도록
                                    if(it["friendName"].toString() != null) {
                                        followerEmail = document.id
                                        followerId = it["friendId"].toString()
                                        followerName = it["friendName"].toString()
                                        // 팔로우/팔로잉 버튼을 나타낼 수 있도록 이미 사용자가 팔로잉한 상태인지 구분한다
                                        followerBtn = if(userFriend.contains(followerEmail)) {
                                            "friend"
                                        }else { "noFriend" }
                                        Log.w("검색된 다른 유저들 정보 : ", "$followerEmail  $followerId  $followerName  $followerBtn")
                                        val data = friendData(followerEmail, followerId, followerName, followerBtn)
                                        // adapter에게 보내줄 리스트에 이미 있다면 포함시키지 않음
                                        // 혹시 모를 중복을 예방함
                                        if(!dataList.contains(data)) {
                                            dataList.add(data)
                                        }
                                    }
                                }
                            }.addOnFailureListener{
                                Log.w("userCollection.document(document.id).get()", "실패")
                            }
                        }
                        // 사용자가 검색한 정보들이 recyclerView에 뜨도록 adapter에게 알림
                        adapter.updateList(dataList)
                        // 검색할 때마다 결과화면이 중복되지 않도록 adapter에게 넘겨줄 리스트 정보 초기화
                        dataList.clear()
                    }
                    .addOnFailureListener { Log.w("userCollection.get()", "실패") }
                return true
            }
        })
    }
}