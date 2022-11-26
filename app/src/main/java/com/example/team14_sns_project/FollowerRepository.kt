package com.example.team14_sns_project

import androidx.lifecycle.MutableLiveData
import com.example.team14_sns_project.Friend
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User

class FollowerRepository {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("follower")

    @Volatile private var INSTANCE : FollowerRepository ?= null

    fun getInstance() : FollowerRepository{
        return INSTANCE ?: synchronized(this) {

            val instance = FollowerRepository()
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

