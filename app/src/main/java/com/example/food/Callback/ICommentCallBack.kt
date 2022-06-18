package com.example.food.Callback

import com.example.food.model.CommentModel

interface ICommentCallBack {
    fun onCommentLoadSucces(commentList:List<CommentModel>)
    fun onCommentLoadFailed(message:String)
}