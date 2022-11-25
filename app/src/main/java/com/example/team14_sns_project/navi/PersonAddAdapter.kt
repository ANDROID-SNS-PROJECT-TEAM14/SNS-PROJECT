package com.example.team14_sns_project.navi

import android.content.ContentValues
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.team14_sns_project.R
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class ListData (
    var id : String,
    var name : String,
    var friendBtn : String
)

private lateinit var friendList : ArrayList<String> // 팔로잉 목록
private var data = ArrayList<ListData>()
private var user : String = "test"

class PersonAddAdapter : RecyclerView.Adapter<PersonAddAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var userId: TextView
        private var userName: TextView
        private var followBtn: Button
        private var fireStore: FirebaseFirestore

        init {
            userId = itemView.findViewById(R.id.itemPersonId)
            userName = itemView.findViewById(R.id.itemPersonName)
            followBtn = itemView.findViewById(R.id.itemPersonBtn)
            fireStore = Firebase.firestore
            friendList = ArrayList()
            val userDocument = fireStore.document("/Users/$user")
            val friend = fireStore.collection("Users").document(user).collection("Following")

            friend.get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.w(ContentValues.TAG, "${document.id} => ${document.data}")
                    friendList.add(document.id)
                }
            }.addOnFailureListener{
                Log.w(ContentValues.TAG, "Error getting documents.")
            }

            // 팔로우 or 팔로잉 버튼을 누르면
            followBtn.setOnClickListener {
                var position: Int = adapterPosition
                Log.w("RecyclerView 위치 : " , position.toString())

                var friendId = data[position].id
                var friendName = data[position].name

                // 계정아이디는 고유한 것으로 가정
                val friendMap = hashMapOf(
                    "friendId" to friendId,
                    "friendName" to friendName
                )

                if(followBtn.text == "팔로우") {
                    if(user != null) {
                        userDocument.collection("Following")
                            .document(friendId).set(friendMap)
                            .addOnSuccessListener {
                                Log.w("Following", "성공")
                                followBtn.text = "팔로잉"
                                followBtn.setBackgroundColor(Color.parseColor("#DDDDDD"))
                                followBtn.setTextColor(Color.BLACK)

                                friendList.clear()
                                friend.get().addOnSuccessListener { result ->
                                    for (document in result) {
                                        Log.w(ContentValues.TAG, "${document.id} => ${document.data}")
                                        friendList.add(document.id)
                                    }
                                }.addOnFailureListener{
                                    Log.w(ContentValues.TAG, "Error getting documents.")
                                }
                        }.addOnFailureListener {
                                Log.w("Following", "실패")
                        }
                    }
                }
                else {
                    if(user != null) {
                        userDocument.collection("Following")
                            .document(friendId).delete()
                            .addOnSuccessListener {
                                Log.w("Follow", "성공")
                                followBtn.text = "팔로우"
                                followBtn.setBackgroundColor(Color.parseColor("#2196F3"))
                                followBtn.setTextColor(Color.WHITE)

                                friendList.clear()
                                friend.get().addOnSuccessListener { result ->
                                    for (document in result) {
                                        Log.w(ContentValues.TAG, "${document.id} => ${document.data}")
                                        friendList.add(document.id)
                                    }
                                }.addOnFailureListener{
                                    Log.w(ContentValues.TAG, "Error getting documents.")
                                }
                            }.addOnFailureListener {
                                Log.w("Follow", "실패")
                            }
                    }
                }
            }
        }

        fun bind(list : ListData) {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.person_add_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun updateList(newList: ArrayList<ListData>) {
        data = newList
        Log.w("updateList", "$newList")
        notifyDataSetChanged()
    }

    fun setUser(name: String) {
        user = name
    }
}