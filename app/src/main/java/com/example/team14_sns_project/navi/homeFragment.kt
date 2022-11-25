package com.example.team14_sns_project.navi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team14_sns_project.navi.data.ContentDTO
import com.example.team14_sns_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// 사람들 피드 나타나는 home Fragment
class homeFragment : Fragment() {
    var firestore: FirebaseFirestore ?= null
    var firebaseAuth: FirebaseAuth ?= null

    var userId: String ?= null //공통으로 쓰기 위해

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = LayoutInflater.from(activity).inflate(R.layout.fragment_home, container, false)
        firestore = FirebaseFirestore.getInstance() // 초기화
        firebaseAuth = FirebaseAuth.getInstance()
        userId = FirebaseAuth.getInstance().currentUser?.uid

        view.findViewById<RecyclerView>(R.id.homeFragRecyclerview).adapter = homeFragmentRecyclerViewAdapter()
        view.findViewById<RecyclerView>(R.id.homeFragRecyclerview).layoutManager = LinearLayoutManager(activity) // 화면에 세로로 배치
        return view
    }

    inner class homeFragmentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        var contentDTOs: ArrayList<ContentDTO> = arrayListOf()
        var contentUserIdList: ArrayList<String> = arrayListOf() // UserId를 담을 List

        init { // DB에 접근해서 데이터를 받아올 수 있는 query
            // 시간순으로 받아올 수 있도록
            firestore?.collection("images")?.orderBy("timestamp")?.addSnapshotListener { snapshot, error ->
                // 받자마자 List에 있는 값을 초기화
                contentDTOs.clear()
                contentUserIdList.clear()

                // logOut시 오류를 막기 위해
                if(firebaseAuth?.currentUser != null){
                    for(s in snapshot!!.documents) {
                        var item = s.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUserIdList.add(s.id!!)
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
            viewholder.findViewById<TextView>(R.id.userId).text = contentDTOs!![position].userId // userId
            Glide.with(holder.itemView.context).load(contentDTOs!![position].imageURL).into(viewholder.findViewById<ImageView>(R.id.uploadImage)) // user upload Image
            Glide.with(holder.itemView.context).load(contentDTOs!![position].userImageId).into(viewholder.findViewById<ImageView>(R.id.profileImage)) // user profile Image
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
        }



        // 좋아요 누르면 생기는 이벤트
        fun favoriteEvent(position: Int) {
            // 내가 선택한 컨텐츠의 userId  받아와서 좋아요를 누르는 이벤트
            // document안에 내가 선택한 컨텐츠 uid값을 넣어줌
            var tsDoc = firestore?.collection("images")?.document(contentUserIdList[position])

            firestore?.runTransaction { transaction ->
                //var userId = FirebaseAuth.getInstance().currentUser?.uid //데이터를 입력하기 위해서는 transaction을 불러와야함
                var contentDTO = transaction.get(tsDoc!!).toObject(ContentDTO::class.java) // transaction의 데이터를 contentDTO로 캐스팅

                // 좋아요 버튼이 이미 클릭 되어 있을 경우
                if(contentDTO!!.favoritesUserList.containsKey(userId)) { // containsKey = 좋아요 버튼이 눌려 있다는 뜻
                    // 클릭된 좋아요가 취소되도록
                    contentDTO?.favoriteCount = contentDTO.favoriteCount - 1
                    contentDTO?.favoritesUserList?.remove(userId)
                } else { // 좋아요 버튼이 눌려있지 않을 경우
                    // 클릭되지 않은 좋아요가 눌리도록
                    contentDTO?.favoriteCount = contentDTO.favoriteCount +1
                    contentDTO?.favoritesUserList?.set(userId!!, true)
                }
                transaction.set(tsDoc, contentDTO) // 서버로 돌려줌
            }
        }
    }
}