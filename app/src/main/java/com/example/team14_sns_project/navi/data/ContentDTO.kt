package com.example.team14_sns_project.navi.data
/*
설명을 관리하는 explain
이미지 주소를 관리하는 imageURL
어느 유저가 올렸는지 UserId
올린 유저의 프로필 이미지를 관리해주는 UserImageId
몇시몇분에 컨텐츠를 올렸는지 timestamp
좋아요가 몇개 눌렸는지 favoriteCount
중복 좋아요 방지하도록 좋아요를 누른 유저를 관리 favoritesUserList
*/
data class ContentDTO(var explain: String? = null,
                 var imageURL: String? = null,
                 var userId: String? = null,
                 var userImageId: String? = null,
                 var timestamp: Long? = null,
                 var favoriteCount: Int = 0,
                 var favoritesUserList: MutableMap<String, Boolean> = HashMap()) {


    /*
    // 댓글 관리해주는 Comment class
    UserId: 어느 유저가 올렸는지
    UserEmail: 유저의 이메일을 관리해주는 변수
    comment: 댓글을 관리해주는 변수
    timestamp: 커멘트를 올린 시간을 관리해주는 변수
    * */
    data class Comment(var userId: String? = null,
                       var userEmail: String? = null,
                       var comment: String? = null,
                       var timestamp: Long? = null)
}