package com.example.team14_sns_project.navi.data

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

    data class Comment(var userId: String ?= null,
                       var userName: String ?= null,
                       var userEmail: String ?= null,
                       var comment: String ?= null,
                       var timestamp: Long ?= null)
}