package com.example.team14_sns_project.navi.data
<<<<<<< HEAD
/*
설명을 관리하는 explain
이미지 주소를 관리하는 imageURL
@asdf 같은 아이디 id
어느 유저가 올렸는지 UserId
올린 유저의 프로필 이미지를 관리해주는 UserImageId
유저 이름 userName
몇시몇분에 컨텐츠를 올렸는지 timestamp
좋아요가 몇개 눌렸는지 favoriteCount
중복 좋아요 방지하도록 좋아요를 누른 유저를 관리 favoritesUserList
*/
data class ContentDTO(  var explain: String ?= null,
                        var imageURL: String ?= null,
                        var userId: String ?= null,
                        var name: String ?= null,
                        var email: String ?= null,
                        var timestamp: Long ?= null,
                        var userImageId: String ?= null,
                        var favoriteCount: Int = 0,
                        var favoritesUserList: MutableMap<String, Boolean> = HashMap()) {
=======

data class ContentDTO(var explain: String ?= null,
                 var imageURL: String ?= null,
                 //var id: String ?= null,
                 var userId: String ?= null,
                 var userImageId: String ?= null,
                 var userName: String ?= null,
                 var userEmail: String ?= null,
                 var timestamp: Long ?= null,
                 var favoriteCount: Int = 0,
                 var favoritesUserList: MutableMap<String, Boolean> = HashMap()) {
>>>>>>> yhgg

    data class Comment(var userId: String ?= null,
<<<<<<< HEAD
                       var name: String ?= null,
=======
                       var userName: String ?= null,
                       var userEmail: String ?= null,
>>>>>>> yhgg
                       var comment: String ?= null,
                       var timestamp: Long ?= null)
}