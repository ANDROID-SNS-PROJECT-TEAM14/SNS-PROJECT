package com.example.team14_sns_project.navi

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team14_sns_project.R
import com.example.team14_sns_project.navi.data.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore

private var myEmail : String = "user"  // NullException을 막기위해 일단 init
private var myId : String = "user"  // NullException을 막기위해 일단 init
private var myName: String = "user"  // NullException을 막기위해 일단 init

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {
    private var fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var contentDTOs: ArrayList<ContentDTO> = ArrayList()
    private var contentUserIdList: ArrayList<String> = ArrayList() // UserId 를 담을 List

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.feed_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var viewholder = holder.itemView
        viewholder.findViewById<TextView>(R.id.userName).text = contentDTOs!![position].userName // userId
        Glide.with(holder.itemView.context).load(contentDTOs!![position].imageURL).into(viewholder.findViewById(R.id.uploadImage)) // user upload Image
        viewholder.findViewById<TextView>(R.id.description).text = contentDTOs!![position].explain// description
        viewholder.findViewById<TextView>(R.id.like_count).text = "좋아요 " + contentDTOs!![position].favoriteCount + "개"// favorite Count

        // favorite 버튼에 이벤트
        viewholder.findViewById<ImageView>(R.id.heart_line).setOnClickListener {
            favoriteEvent(position)
        }

        // 좋아요 수와 좋아요 색이 채워지거나 비워지는 이벤트
        if(contentDTOs!![position].favoritesUserList.containsKey(myId)) {// 나의 userId가 포함되어 있을 경우
            viewholder.findViewById<ImageView>(R.id.heart_line).setImageResource(R.drawable.ic_favorite) // 좋아요 클릭한 부분 - 채워진 하트

        } else { // 포함되어 있지 않을 경우
            viewholder.findViewById<ImageView>(R.id.heart_line).setImageResource(R.drawable.ic_favorite_border) // 아직 좋아요 클릭하지 않은 부분 - 비워진 하트
        }
    }

    override fun getItemCount(): Int {
        return contentDTOs.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newDto: ArrayList<ContentDTO>, newCui: ArrayList<String>) {
        contentDTOs = newDto
        contentUserIdList = newCui
        Log.w("updateList", "$contentDTOs  $contentUserIdList")
        notifyDataSetChanged()
    }

    fun setUser(email: String, id: String, name: String) {
        myEmail = email
        myId = id
        myName = name
    }

    // 좋아요 누르면 생기는 이벤트
    private fun favoriteEvent(position: Int) {
        // 내가 선택한 컨텐츠의 userId  받아와서 좋아요를 누르는 이벤트
        // document안에 내가 선택한 컨텐츠 uid값을 넣어줌
        var userInfoDocument = fireStore?.collection("userInfo")?.document(contentUserIdList[position])

        fireStore?.runTransaction {
            var contentDTO = it.get(userInfoDocument!!).toObject(ContentDTO::class.java) // transaction의 데이터를 contentDTO로 캐스팅

            // 좋아요 버튼이 이미 클릭 되어 있을 경우
            if(contentDTO!!.favoritesUserList.containsKey(myId)) { // containsKey = 좋아요 버튼이 눌려 있다는 뜻
                contentDTO?.favoriteCount = contentDTO.favoriteCount - 1 // 클릭된 좋아요가 취소되도록
                contentDTO?.favoritesUserList?.remove(myId)

            } else { // 좋아요 버튼이 눌려있지 않을 경우
                contentDTO?.favoriteCount = contentDTO.favoriteCount +1 // 클릭되지 않은 좋아요가 눌리도록
                contentDTO?.favoritesUserList?.set(myId!!, true)

            }
            it.set(userInfoDocument, contentDTO) // 서버로 돌려줌
        }
    }
}