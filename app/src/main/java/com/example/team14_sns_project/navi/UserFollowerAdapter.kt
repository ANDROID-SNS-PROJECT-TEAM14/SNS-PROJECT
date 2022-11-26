package com.example.team14_sns_project.navi

import android.content.ContentValues
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.team14_sns_project.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class friendData (
    var email : String,
    var id : String,
    var name : String,
    var friendBtn : String
)

private var data = ArrayList<friendData>()  // Activity에서 전달받을 검색 결과 리스트

private var myEmail : String = "user"  // NullException을 막기위해 일단 init
private var myId : String = "user"  // NullException을 막기위해 일단 init
private var myName: String = "user"  // NullException을 막기위해 일단 init

class UserFollowerAdapter(val context: Context) : RecyclerView.Adapter<UserFollowerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var fireStore: FirebaseFirestore
        private var userId: TextView
        private var userName: TextView
        private var followBtn: Button

        init {
            userId = itemView.findViewById(R.id.itemPersonId)
            userName = itemView.findViewById(R.id.itemPersonName)
            followBtn = itemView.findViewById(R.id.itemPersonBtn)
            fireStore = Firebase.firestore

            // 팔로우 or 팔로잉 버튼을 누르면
            followBtn.setOnClickListener {
                // 위치 확인용 로그
                var position: Int = adapterPosition
                Log.w("RecyclerView 위치 : " , position.toString())

                // 클릭한 사용자의 email과 id와 name
                var friendEmail = data[position].email
                var friendId = data[position].id
                var friendName = data[position].name

                // FireStore에 집어넣을 친구 HashMap 생성
                val friendMap = hashMapOf(
                    "friendEmail" to friendEmail,
                    "friendId" to friendId,
                    "friendName" to friendName
                )

                // 팔로우 버튼을 누를 시
                if(followBtn.text == "팔로우") {
                    if(myEmail != null) {
                        addFollowing(friendEmail, friendMap)
                        addFollwer(friendEmail)
                    }
                }
                // 팔로잉 버튼을 누를 시
                else {
                    if(myEmail != null) {
                        deleteFollowing(friendEmail)
                        deleteFollower(friendEmail)
                    }
                }
            }
        }
        fun bind(list : friendData) {
            userId.text = "@" + list.id
            userName.text = list.name
            if(list.friendBtn != "friend") {
                followBtn.text = "팔로우"
                followBtn.setBackgroundColor(Color.parseColor("#2196F3"))
                followBtn.setTextColor(Color.WHITE)
            }
            else {
                followBtn.text = "팔로잉"
                followBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                followBtn.setTextColor(Color.BLACK)
            }
        }

        private fun addFollowing(friendEmail : String, friendMap : HashMap<String, String>) {
            // 현재 사용자의 document URL
            val userDocument = fireStore.document("/Users/$myEmail")
            userDocument.collection("Following")
                .document(friendEmail).set(friendMap)
                .addOnSuccessListener {
                    Log.w("현재 사용자의 팔로잉 목록에서 다른 사용자 추가", "성공")
                    // 팔로잉 버튼으로 변경
                    followBtn.text = "팔로잉"
                    followBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                    followBtn.setTextColor(Color.BLACK)
                    // 유저 친구정보 리스트 갱신
                }.addOnFailureListener {
                    Log.w("현재 사용자의 팔로잉 목록에서 다른 사용자 추가", "실패")
                }
        }

        private fun addFollwer(friendEmail : String) {
            // 현재 사용자의 정보를 담은 HashMap
            val userMap = hashMapOf(
                "friendEmail" to myEmail,
                "friendId" to myId,
                "friendName" to myName
            )

            // 현재 사용자의 정보를 팔로잉 유저의 팔로워 목록에 삽입
            val friendDocument = fireStore.collection("Users").document(friendEmail)
                .collection("Follower").document(myEmail)
            friendDocument.set(userMap).addOnSuccessListener {
                Log.w("친구의 팔로워 목록에서 현재 사용자 추가", "성공")
            }.addOnFailureListener {
                Log.w("친구의 팔로워 목록에서 현재 사용자 추가", "실패")
            }
        }

        private fun deleteFollowing(friendEmail : String) {
            // 현재 사용자의 document URL
            val userDocument = fireStore.document("/Users/$myEmail")
            userDocument.collection("Following")
                .document(friendEmail).delete()
                .addOnSuccessListener {
                    Log.w("현재 사용자의 팔로잉 목록에서 다른 사용자 삭제", "성공")
                    followBtn.text = "팔로우"
                    followBtn.setBackgroundColor(Color.parseColor("#2196F3"))
                    followBtn.setTextColor(Color.WHITE)
                }.addOnFailureListener {
                    Log.w("현재 사용자의 팔로잉 목록에서 다른 사용자 삭제", "실패")
                }
        }

        private fun deleteFollower(friendEmail : String) {
            // 현재 사용자의 정보를 팔로잉 유저의 팔로워 목록에 삽입
            val friendDocument = fireStore.collection("Users").document(friendEmail)
                .collection("Follower").document(myEmail)
            friendDocument.delete().addOnSuccessListener {
                Log.w("친구의 팔로워 목록에서 현재 사용자 삭제", "성공")
            }.addOnFailureListener {
                Log.w("친구의 팔로워 목록에서 현재 사용자 삭제", "실패")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context)
            .inflate(R.layout.person_add_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(newList: ArrayList<friendData>) {
        data = newList
        Log.w("updateList", "$newList")
        notifyDataSetChanged()
    }

    fun setUser(email: String, id: String, name: String) {
        myEmail = email
        myId = id
        myName = name
    }
}