package com.example.team14_sns_project.navi

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team14_sns_project.NaviActivity
import com.example.team14_sns_project.navi.data.ContentDTO
import com.example.team14_sns_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.log

// 사람들 피드 나타나는 home Fragment
class homeFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var adapter: HomeAdapter
    private lateinit var contentDTOs: ArrayList<ContentDTO>
    private lateinit var contentDTOsList: ArrayList<ContentDTO>
    private lateinit var contentUserIdList: ArrayList<String>

    private lateinit var userEmail : String
    private lateinit var userId: String
    private lateinit var userName : String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)

        // 초기화
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // NaviActivity에 저장되어 있는 user 정보 가져옴
        val parent = activity as NaviActivity
        userEmail = parent.userEmail
        userId = parent.userId
        userName = parent.userName

        // adapter
        adapter = HomeAdapter()
        // 현재 사용자의 정보들을 설정해준다.
        adapter.setUser(userEmail, userId, userName)

        // 검색된 결과를 보여줄 recyclerView
        val recyclerView = view.findViewById<RecyclerView>(R.id.homeFragRecyclerview)
        val linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        contentDTOs = ArrayList()
        contentDTOsList = ArrayList()
        contentUserIdList = ArrayList() // UserId 를 담을 List

        return view
    }

    override fun onStart() {
        super.onStart()
        val followingCol = firestore.collection("Users").document(userEmail).collection("Following")
        val feedCol = firestore.collection("userInfo")

        // 배열 초기화
        contentDTOs.clear()
        contentUserIdList.clear()

        feedCol.orderBy("timestamp").addSnapshotListener { snapshot, error ->
            if (auth?.currentUser != null) {
                for (snap in snapshot!!.documents) {
                    var item = snap.toObject(ContentDTO::class.java)
                    followingCol.get().addOnSuccessListener { result ->
                        for(document in result) {
                            if (item != null) {
                                if (document.id == item.userEmail.toString() || userEmail == item.userEmail.toString()) {
                                    if(!contentDTOs.contains(item)){
                                        contentDTOs.add(item)
                                        contentUserIdList.add(snap.id)
                                    }
                                    Log.w("contentDTOs", contentDTOs.toString())
                                    Log.w("contentUserIdList", contentUserIdList.toString())
                                    Log.w("document", document.id)
                                    Log.w("item", item.userEmail.toString())
                                    Log.w("userEmail", userEmail)
                                    adapter.updateList(contentDTOs, contentUserIdList)
                                }
                            }
                        }
                    }.addOnFailureListener {

                    }
                }
            }
        }

//        feedCol.orderBy("timestamp").addSnapshotListener { snapshot, error ->
//            if (auth?.currentUser != null) {
//                followingCol.get().addOnSuccessListener { result ->
//                    Log.w("firestore?.collection(\"userInfo\")", "성공")
//                    // 배열 초기화
//                    contentDTOs.clear()
//                    contentUserIdList.clear()
//                    // 팔로잉이 하나도 없다면
//                    if(result.size() == 0) {
//                        for (snap in snapshot!!.documents) {
//                            // 아이템 매핑
//                            var item = snap.toObject(ContentDTO::class.java)
//                            if (item != null) {
//                                if(userEmail == item.userEmail.toString()){
//                                    contentDTOs.add(item)
//                                    contentUserIdList.add(snap.id)
//                                    adapter.updateList(contentDTOs, contentUserIdList)
//                                }
//                            }
//                        }
//                    }
//                    // 팔로잉이 있다면
//                    else{
//                        for(document in result) {
//                            Log.w("document", document.id)
//                            for (snap in snapshot!!.documents) {
//                                Log.w("snap", snap.toString())
//                                var item = snap.toObject(ContentDTO::class.java)
//                                if (item != null) {
//                                    if(document.id == item.userEmail.toString() || userEmail == item.userEmail.toString()){
//                                        contentDTOs.add(item)
//                                        contentUserIdList.add(snap.id)
//                                        Log.w("contentDTOs", contentDTOs.toString())
//                                        Log.w("contentUserIdList", contentUserIdList.toString())
//                                        Log.w("document", document.id)
//                                        Log.w("item", item.userEmail.toString())
//                                        Log.w("userEmail", userEmail)
//                                        adapter.updateList(contentDTOs, contentUserIdList)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }.addOnFailureListener {
//                    Log.w("firestore?.collection(\"userInfo\")", "실패")
//                }
//            }
//        }
    }
}

