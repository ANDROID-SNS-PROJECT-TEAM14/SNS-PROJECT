package com.example.team14_sns_project.navi

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team14_sns_project.NaviActivity
import com.example.team14_sns_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 친구 추가 화면
class personAddFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    // 사용자의 Following 리스트
    private var dataList = ArrayList<ListData>()
    private lateinit var userFriend : ArrayList<String>
    private lateinit var adapter: PersonAddAdapter

    // 사용자의 친구들 정보
    private lateinit var friendEmail : String
    private lateinit var friendId : String
    private lateinit var friendName : String
    private lateinit var friendBtn : String

    // 사용자 정보
    private lateinit var userEmail : String
    private lateinit var userId : String
    private lateinit var userName : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_person_add, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        // NaviActivity에 저장되어 있는 user 정보 가져옴
        val parent = activity as NaviActivity
        userEmail = parent.userEmail
        userId = parent.userId
        userName = parent.userName

        // adapter
        adapter = PersonAddAdapter()
        // 현재 사용자의 정보들을 설정해준다.
        adapter.setUser(userEmail, userId, userName)

        // Following 리스트 초기화
        userFriend = ArrayList()
        // user의 Following Collection
        val userFollowing = fireStore.collection("Users").document(userEmail).collection("Following")

        // 검색된 결과를 보여줄 recyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.personAddRecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        // 검색창
        val personAddSearchView = view.findViewById<SearchView>(R.id.personAddSearchView)
        personAddSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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

                // 사용자 본인을 제외한 유저 목록 불러옴
                val userCollection= fireStore.collection("Users")
                userCollection.get()
                    .addOnSuccessListener { result ->
                        Log.w("userCollection.get()", "성공")
                        for (document in result) {
                            userCollection.document(document.id).get().addOnSuccessListener {
                                Log.w("userCollection.document(document.id).get()", "성공")
                                // 다른 사용자들의 id나 name에 검색한 글자가 포함되어 있다면 목록에 보여줌
                                if((it["id"].toString().contains(newText) || it["name"].toString().contains(newText)) && it["id"].toString() != userId) {
                                    // 정확한 정보를 가지고 존재하는 계정만 보이도록
                                    if(it["name"].toString() != null) {
                                        friendEmail = document.id
                                        friendId = it["id"].toString()
                                        friendName = it["name"].toString()
                                        // 팔로우/팔로잉 버튼을 나타낼 수 있도록 이미 사용자가 팔로잉한 상태인지 구분한다
                                        friendBtn = if(userFriend.contains(friendEmail)) {
                                            "friend"
                                        }else { "noFriend" }
                                        Log.w("검색된 다른 유저들 정보 : ", "$friendEmail  $friendId  $friendName  $friendBtn")
                                        val data = ListData(friendEmail, friendId, friendName, friendBtn)
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
        return view
    }

    override fun onStart() {
        super.onStart()
        // 다른 Fragment로 이동했다가 다시 화면에 보여질 때 검색했던 기록이 초기화되도록 설정
        adapter.updateList(dataList)
    }
}