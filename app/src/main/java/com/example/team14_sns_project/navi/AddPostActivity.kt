package com.example.team14_sns_project.navi

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team14_sns_project.navi.data.ContentDTO
import com.example.team14_sns_project.databinding.ActivityAddPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class  AddPostActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAddPostBinding

    var IMAGE_FROM_ALBUM = 0 // request code
    var storage: FirebaseStorage ?= null
    var photoUri: Uri ?= null

    var auth: FirebaseAuth?= null // 유저의 정보를 가져올 수 있도록  firebase auth 추가
    var firestore: FirebaseFirestore ?= null // database를 사용할 수 있도록

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // 이 액티비티가 시작하자마자 앨범이 열리도록
        var photoPickIntent = Intent(Intent.ACTION_PICK)
        photoPickIntent.type = "image/*"

        binding.addphotoImageBtn.setOnClickListener { // 이미지 선택하는 경우
            startActivityForResult(photoPickIntent, IMAGE_FROM_ALBUM)
        }

        binding.uploadBtn.setOnClickListener { // uploadBtn에 이벤트 추가
            contentUpload()
        }
    }

    // 선택한 이미지를 받는 부분
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) { // 사진을 선택 했을때
                photoUri = data?.data // 이미지의 경로가 이쪽으로 넘어오도록
                binding.addphotoImage.setImageURI(photoUri) // ImageView에 선택한 이미지를 표시해주도록
            } else { // 취소 버튼 눌렀을때
                finish()
            }
        }
    }



    private fun contentUpload() {
        // 파일 이름 만들어줌
        var timesnap = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())
        var imageFileName = "IMAGE " + timesnap + "_.png"

        // 이미지 업로드
        var storageRef = storage?.reference?.child("images")?.child(imageFileName)
        storageRef?.putFile(photoUri!!)?.addOnCompleteListener {
            if(it.isSuccessful) { // 업로드 성공했을때
                storageRef.downloadUrl.addOnSuccessListener { uri -> // 이미지 업로드 완료했으면 이미지 주소를 받아옴
                    var contentDTO = ContentDTO() // 이미지 주소를 받아오자마자 data model을 만들어줌

                    contentDTO.imageURL = uri.toString() // downloadUrl을 넣어줌
                    contentDTO.userId = auth?.currentUser?.uid // 현재 유저의 id
                    contentDTO.userImageId = auth?.currentUser?.email // 컨텐츠 올린 유저의 프로필 이미지
                    contentDTO.explain = binding.editDescription.text.toString() // desription
                    contentDTO.timestamp = System.currentTimeMillis() // 시간

                    firestore?.collection("images")?.document()?.set(contentDTO)
                    setResult(Activity.RESULT_OK) // 정상적으로 닫혔다는 frag 값을 넘겨주기 위해서
                    finish()
                }
            }
        }
    }

}