package com.example.team14_sns_project.navi

import android.content.ContentValues
import android.content.Intent
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
import com.example.team14_sns_project.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 사람들 피드 나타나는 home Fragment
class homeFragment : Fragment() {
    var auth: FirebaseAuth ?= null
    var firestore: FirebaseFirestore ?= null
<<<<<<< HEAD
    var auth: FirebaseAuth ?= null
    var userId: String ?= null // Uid
    var userEmail: String ?= null
=======
    var userId: String ?= null // Uid

    private lateinit var userEmail : String
    private lateinit var userName : String

>>>>>>> yhgg

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance() // 초기화
<<<<<<< HEAD
        auth = FirebaseAuth.getInstance()
        userId = auth?.currentUser?.uid
=======
>>>>>>> yhgg

        userId = auth?.currentUser?.uid
        // NaviActivity에 저장되어 있는 user 정보 가져옴
        val parent = activity as NaviActivity
        userEmail = parent.userEmail
<<<<<<< HEAD
=======
        userName = parent.userName
>>>>>>> yhgg

        view.findViewById<RecyclerView>(R.id.homeFragRecyclerview).adapter = homeFragmentRecyclerViewAdapter()
        view.findViewById<RecyclerView>(R.id.homeFragRecyclerview).layoutManager = LinearLayoutManager(activity) // 화면에 세로로 배치
        return view
    }

    inner class homeFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
<<<<<<< HEAD
        var contentUserIdList: ArrayList<String> = arrayListOf() // UserId를 담을 List

        var userCollection = firestore?.collection("Users")

        // 유저의 팔로우/팔로잉 FireStoreUrl
        val userFollowingCollection = userEmail?.let { firestore?.collection("Users")?.document(it)?.collection("Following") }
=======
        var contentUserIdList: ArrayList<String> = arrayListOf() // UserId 를 담을 List
        var followingList: ArrayList<String> = arrayListOf() // 현재 유저(email)가 팔로잉 하고있는 유저 리스트(email)
>>>>>>> yhgg

        init { // DB에 접근해서 데이터를 받아올 수 있는 query
            // 시간순으로 받아올 수 있도록
            firestore?.collection("userInfo")?.orderBy("timestamp")?.addSnapshotListener { snapshot, error ->
                // 받자마자 List에 있는 값을 초기화
                contentDTOs.clear()
                contentUserIdList.clear()

<<<<<<< HEAD
                for(snap in snapshot!!.documents) {
                    var item = snap.toObject(ContentDTO::class.java)
                    userFollowingCollection?.get()?.addOnSuccessListener { result ->
                        for(document in result) {
                            if (item != null) {
                                if(document.id == item.email){
                                    contentDTOs.add(item!!)
                                    contentUserIdList.add(snap.id!!)
=======
                // logOut시 오류를 막기 위해
                if(auth?.currentUser != null){
                    // 현재 유저의 Following 안에 있는 documents를 모두 가져옴
                    firestore?.collection("Users")!!.document(userEmail).collection("Following").get().addOnSuccessListener { result ->
                        if(result.size() != null) {
                            for(document in result) {
                                followingList.add(document.id)
                            }
                        }
                    }.addOnFailureListener{
                        Log.w("userFollowingCollection.get()", "userFollowingCollection 실패")
                    }

                    for(doc  in snapshot!!.documents) {
                        var item = doc.toObject(ContentDTO::class.java)
                        for(i in followingList) {
                            if (item != null) {
                                if(item.userEmail == i) {
                                    contentDTOs.add(item!!)
                                    contentUserIdList.add(doc.id!!)
>>>>>>> yhgg
                                }
                            }
                        }
                    }
                }
                notifyDataSetChanged() // 값이 새로고침 되도록
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.feed_layout, parent, false)
            return CustomViewHolder(view)
        }
        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }

        // 서버에서 넘어온 데이터들을 mapping 시켜주는 부분
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            var viewholder = (holder as CustomViewHolder).itemView
<<<<<<< HEAD
            viewholder.findViewById<TextView>(R.id.userId).text = contentDTOs!![position].name // 유저이름
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageURL).into(viewholder.findViewById(R.id.uploadImage)) // user upload Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageURL).into(viewholder.findViewById(R.id.profileImage)) // user profile Image
=======
            viewholder.findViewById<TextView>(R.id.userName).text = contentDTOs!![position].userName // userId
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageURL).into(viewholder.findViewById<ImageView>(R.id.uploadImage)) // user upload Image
            // Glide.with(holder.itemView.context).load(contentDTOs!![position].userImageId).into(viewholder.findViewById<ImageView>(R.id.profileImage)) // user profile Image
>>>>>>> yhgg
            viewholder.findViewById<TextView>(R.id.description).text = contentDTOs!![position].explain// description
            viewholder.findViewById<TextView>(R.id.like_count).text = "좋아요 " + contentDTOs!![position].favoriteCount + "개"// favorite Count

            // favorite 버튼에 이벤트
            viewholder.findViewById<ImageView>(R.id.heart_line).setOnClickListener {
                favoriteEvent(position)
            }

            // 좋아요 수와 좋아요 색이 채워지거나 비워지는 이벤트
            if(contentDTOs!![position].favoritesUserList.containsKey(userId)) {// 나의 userId가 포함되어 있을 경우
                viewholder.findViewById<ImageView>(R.id.heart_line).setImageResource(R.drawable.ic_favorite) // 좋아요 클릭한 부분 - 채워진 하트

            } else { // 포함되어 있지 않을 경우
                viewholder.findViewById<ImageView>(R.id.heart_line).setImageResource(R.drawable.ic_favorite_border) // 아직 좋아요 클릭하지 않은 부분 - 비워진 하트
            }


            //comment 버튼 누르면 댓글창으로 넘어감
            viewholder.findViewById<ImageView>(R.id.comment).setOnClickListener { view ->
                var intent = Intent(view.context, CommentActivity::class.java)
                intent.putExtra("contentUserId", contentUserIdList[position]) // 내가 선택한 userId 값이 넘어감
                startActivity(intent)
            }

        }



        // 좋아요 누르면 생기는 이벤트
        fun favoriteEvent(position: Int) {
            // 내가 선택한 컨텐츠의 userId  받아와서 좋아요를 누르는 이벤트
            // document안에 내가 선택한 컨텐츠 uid값을 넣어줌
<<<<<<< HEAD
            var userInfoDocument = firestore?.collection("userInfo")?.document(contentUserIdList[position])
=======
            var doc = firestore?.collection("userInfo")?.document(contentUserIdList[position])
>>>>>>> yhgg

            firestore?.runTransaction {
                //var userId = FirebaseAuth.getInstance().currentUser?.uid //데이터를 입력하기 위해서는 transaction을 불러와야함
<<<<<<< HEAD
                var contentDTO = it.get(userInfoDocument!!).toObject(ContentDTO::class.java) // transaction의 데이터를 contentDTO로 캐스팅
=======
                var contentDTO = transaction.get(doc!!).toObject(ContentDTO::class.java) // transaction의 데이터를 contentDTO로 캐스팅
>>>>>>> yhgg

                // 좋아요 버튼이 이미 클릭 되어 있을 경우
                if(contentDTO!!.favoritesUserList.containsKey(userId)) { // containsKey = 좋아요 버튼이 눌려 있다는 뜻
                    contentDTO?.favoriteCount = contentDTO.favoriteCount - 1 // 클릭된 좋아요가 취소되도록
                    contentDTO?.favoritesUserList?.remove(userId)

                } else { // 좋아요 버튼이 눌려있지 않을 경우
                    contentDTO?.favoriteCount = contentDTO.favoriteCount +1 // 클릭되지 않은 좋아요가 눌리도록
                    contentDTO?.favoritesUserList?.set(userId!!, true)

                }
<<<<<<< HEAD
                it.set(userInfoDocument, contentDTO) // 서버로 돌려줌
=======
                transaction.set(doc, contentDTO) // 서버로 돌려줌
>>>>>>> yhgg
            }
        }
    }
}