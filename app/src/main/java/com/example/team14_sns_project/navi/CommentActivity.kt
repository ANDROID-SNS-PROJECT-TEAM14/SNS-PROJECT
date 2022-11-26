package com.example.team14_sns_project.navi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.team14_sns_project.NaviActivity
import com.example.team14_sns_project.R
import com.example.team14_sns_project.databinding.ActivityCommentBinding
import com.example.team14_sns_project.navi.data.ContentDTO
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class CommentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCommentBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        documentId = intent?.getStringExtra("contentUserId") ?: "" // collection("userInfo") 아래 document id

        findViewById<RecyclerView>(R.id.commentRecyclerView).adapter = CommentRecyclerViewAdapter()
        findViewById<RecyclerView>(R.id.commentRecyclerView).layoutManager = LinearLayoutManager(this) // 화면에 세로로 배치


        val userImageCollection = firestore.collection("userInfo")
        var email = NaviActivity().userEmail
        var userName = NaviActivity().userName

        // 게시 버튼
        binding.commentSendBtn.setOnClickListener {
            var comment = ContentDTO.Comment()
            comment.userId = auth.currentUser?.uid
            comment.name = userName
            comment.comment = binding.commentEditText.text.toString()
            comment.timestamp = System.currentTimeMillis()


            FirebaseFirestore.getInstance().collection("userInfo").document(documentId).collection("comments").document().set(comment)
            binding.commentEditText.setText("")
        }
    }

    inner class CommentRecyclerViewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

         // 댓글들 받아옴
        var comments: ArrayList<ContentDTO.Comment> = arrayListOf()

        init {
            FirebaseFirestore.getInstance().collection("userInfo")
                .document(documentId)
                .collection("comments")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    comments.clear() // 댓글 중복 방지

                    if(snapshot == null) return@addSnapshotListener

                    for(s in snapshot.documents) {
                        comments.add(s.toObject(ContentDTO.Comment::class.java)!!)
                    }
                    notifyDataSetChanged()// recyclerview 새로고침
                }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.comment_layout, parent, false)
            return CustomViewHolder(view)
        }
        private inner class CustomViewHolder(view: View): RecyclerView.ViewHolder(view)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // 서버에서 넘어온 데이터 mapping
            var view = holder.itemView
            view.findViewById<TextView>(R.id.commentUserName).text = comments[position].name
            view.findViewById<TextView>(R.id.commentTextViewMessage).text = comments[position].comment
        }

        override fun getItemCount(): Int {
            return comments.size
        }

    }
}