package com.example.project_friend

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.team14_sns_project.R
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.sql.Ref


class FollowerAdapter : RecyclerView.Adapter<FollowerAdapter.MyViewHolder>() {

    private val userList = ArrayList<Friend>()
    val database = Firebase.database("https://team14-sns-project-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val myRef = database.getReference("following")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_layout,
            parent,false
        )

        return FollowerAdapter.MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: FollowerAdapter.MyViewHolder, position: Int) {

        val currentitem = userList[position]
        var exist = 0

        holder.firstName.text = currentitem.Name
        holder.ProfileImage.setImageResource(R.drawable.user)

        myRef.get().addOnSuccessListener {
            var k = 1
            for (i:Int in 1..5) {
                System.out.println("myRef.child(" + i + ").child(\"Name\").get(): " + it.child(i.toString()).child("Name").getValue())

            }
        }
        if (exist == 0)
            holder.FollowButtonImage.setImageResource(R.drawable.followbutton)
        holder.FollowButtonImage.setOnClickListener {
            holder.FollowButtonImage.setImageResource(R.drawable.followingbutton)

            myRef.get().addOnSuccessListener {
                var index = 1
                while (true) {
                    System.out.println(
                        "myRef.child(index).get(): " + it.child(index.toString()).getValue()
                    )
                    if (it.child(index.toString()).getValue() == null) {
                        myRef.child(index.toString()).push() // 맨 끝에 인덱스 새로 생성
                        myRef.child(index.toString()).setValue(currentitem)
                        index = 1
                        break
                    }
                    index++
                }

            }
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUserList(userList : List<Friend>){

        this.userList.clear()
        this.userList.addAll(userList)
        notifyDataSetChanged()

    }

    class  MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val ProfileImage : ImageView = itemView.findViewById(R.id.imageView)
        val firstName : TextView = itemView.findViewById(R.id.textView)
        val FollowButtonImage : ImageView = itemView.findViewById(R.id.followimage)

    }

}