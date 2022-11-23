package com.example.project_friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide.init
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.sql.Ref

data class Item(val icon: String, val firstName: String)

enum class ItemEvent { ADD, UPDATE, DELETE }

class FollowingViewModel : ViewModel() {
    val database = Firebase.database("https://team14-sns-project-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val myRef = database.getReference("following")

    private val repository : FollowingRepository
    private var _allUsers = MutableLiveData<List<Friend>>()
    val allUsers : LiveData<List<Friend>> = _allUsers


    init {
        repository = FollowingRepository().getInstance()
        repository.loadUsers(_allUsers)

    }
}