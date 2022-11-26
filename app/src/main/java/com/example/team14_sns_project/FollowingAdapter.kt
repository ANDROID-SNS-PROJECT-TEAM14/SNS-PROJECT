package com.example.team14_sns_project

import android.content.Intent
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.team14_sns_project.Friend
import com.example.team14_sns_project.R
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.jar.Attributes

// following 목록 Adapter
class FollowingAdapter : RecyclerView.Adapter<FollowingAdapter.MyViewHolder>() {

    private var userList = ArrayList<Friend>()
    private val currentList = ArrayList<Friend>()
    val database = Firebase.database("https://team14-sns-project-default-rtdb.asia-southeast1.firebasedatabase.app/")
    val myRef = database.getReference("following")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowingAdapter.MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.item_layout,
            parent,false
        )

        return FollowingAdapter.MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: FollowingAdapter.MyViewHolder, position: Int) {

        val currentitem = userList[position]

        //println("currentList.size : " + currentList.size)
        //if (currentitem == 1) {
            holder.firstName.text = currentitem.Name
            holder.ProfileImage.setImageResource(R.drawable.user)
            holder.FollowButtonImage.setImageResource(R.drawable.followingbutton)
            holder.FollowButtonImage.setOnClickListener {
                holder.FollowButtonImage.setImageResource(R.drawable.followbutton)
                var p = position

                for (i:Int in 1..userList.size) {
                    if (holder.firstName.text.equals(userList[i-1].Name)) {
                        System.out.println("발견!! holder.firstName.text: " + holder.firstName.text + ", userList[i-1].Name: " + userList[i-1].Name + ", position: " + position)
                        System.out.println("userList.size: " + userList.size)
                        System.out.println("myRef.child((position+1).toString()): " + myRef.child((position+1).toString()).get())

                            myRef.get().addOnSuccessListener {
                                myRef.child((position+1).toString()).removeValue()
                                userList.removeAt(position)

                                for (i:Int in 1..userList.size+1) {
                                    System.out.println("it.child((position+1).toString()).getValue(): " + it.child(i.toString()).getValue())

                                    val db =
                                        Firebase.database("https://team14-sns-project-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                    val Ref = db.getReference("following/")

                                    Ref.child((p + 1).toString()).push()
                                    Ref.child((p+1).toString()).setValue(it.child((p + 2).toString()).getValue())
                                    p++

                                    System.out.println("it.child((position+2).toString()).getValue(): " + it.child((p+2).toString()).getValue())

                                }

                            }
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