package com.example.team14_sns_project.navi.data

data class ContentDTO(  var explain: String ?= null,
                        var imageURL: String ?= null, // upload image
                        var uID: String ?= null, // Uid
                        var userName: String ?= null,
                        var userEmail: String ?= null,
                        var timestamp: Long ?= null,
                        var userImageId: String ?= null, // 유저 프로필 사진
                        var favoriteCount: Int = 0,
                        var favoritesUserList: MutableMap<String, Boolean> = HashMap()) {

    data class Comment(var uID: String ?= null,
                       var userName: String ?= null,
                       var userEmail: String ?= null,
                       var comment: String ?= null,
                       var timestamp: Long ?= null)
}