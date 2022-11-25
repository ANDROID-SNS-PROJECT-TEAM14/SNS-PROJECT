package com.example.team14_sns_project.navi

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team14_sns_project.NaviActivity
import com.example.team14_sns_project.R
import com.example.team14_sns_project.SignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 친구 추가 화면
class personAddFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore

    private var dataList = ArrayList<ListData>()
    private lateinit var userFriend : ArrayList<String>

    private lateinit var friendId : String
    private lateinit var friendName : String
    private lateinit var friendBtn : String

    private lateinit var userEmail : String
    private lateinit var userId : String
    private lateinit var userName : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_person_add, container, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        val parent = activity as NaviActivity
        userEmail = parent.userEmail
        userId = parent.userId
        userName = parent.userName

        userFriend = ArrayList()

        val friend = fireStore.collection("Users").document(userEmail).collection("Following")
        val adapter = PersonAddAdapter()
        adapter.setUser(userEmail)
        val recyclerView = view.findViewById<RecyclerView>(R.id.personAddRecyclerView)
        val linearLayoutManager = LinearLayoutManager(activity)

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        //검색창
        val personAddSearchView = view.findViewById<SearchView>(R.id.personAddSearchView)
        personAddSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                userFriend.clear()
                friend.get().addOnSuccessListener { result ->
                    for (document in result) {
                        Log.w(ContentValues.TAG, "${document.id} => ${document.data}")
                        userFriend.add(document.id)
                    }
                }.addOnFailureListener{
                    Log.w(ContentValues.TAG, "Error getting documents.")
                }
                // 본인을 제외한 유저 목록 불러옴
                val userCollection= fireStore.collection("Users")
                userCollection.get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            Log.w(ContentValues.TAG, "${document.id} => ${document.data}")
                            userCollection.document(document.id).get().addOnSuccessListener {
                                if((it["id"].toString().contains(newText) || it["name"].toString().contains(newText)) && it["id"].toString() != userId) {
                                    if(it["id"].toString() != null && it["name"].toString() != null) {
                                        friendId = it["id"].toString()
                                        friendName = it["name"].toString()
                                        friendBtn = if(userFriend.contains(friendId)) {
                                            "friend"
                                        }else {
                                            "noFriend"
                                        }
                                        Log.w("정보 : ", "$friendId  $friendName  $friendBtn")
                                        var data = ListData(friendId, friendName, friendBtn)
                                        if(!dataList.contains(data)) {
                                            dataList.add(data)
                                        }
                                    }
                                }
                            }.addOnFailureListener{
                                Log.w("유저 목록 읽어오기 : ", "실패")
                            }
                        }
                        adapter.updateList(dataList)
                        dataList.clear()
                    }
                    .addOnFailureListener { exception ->
                        Log.w(ContentValues.TAG, "Error getting documents.", exception)
                    }
                return true
            }
        })
        return view
    }

}