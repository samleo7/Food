package com.example.food.Callback

import com.example.food.model.PopularCategoryModel

interface IPopularLoadCallback {
    fun onPopularLoadSucces(popularModelList:List<PopularCategoryModel>)
    fun onPopularLoadFailed(message:String)
}