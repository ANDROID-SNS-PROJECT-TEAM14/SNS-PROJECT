package com.example.project_friend

import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User

class FollowingRepository {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("following")

    @Volatile private var INSTANCE : FollowingRepository ?= null

    fun getInstance() : FollowingRepository{
        return INSTANCE ?: synchronized(this) {

            val instance = FollowingRepository()
            INSTANCE = instance
            instance
        }
    }
    fun loadUsers(userList : MutableLiveData<List<Friend>>){

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                try {

                    val _userList : List<Friend> = snapshot.children.map { dataSnapshot ->

                        dataSnapshot.getValue(Friend::class.java)!!

                    }

                    userList.postValue(_userList)

                }catch (e : Exception){


                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


    }
}

