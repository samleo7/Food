package com.example.food.Callback

import com.example.food.model.CategoryModel

interface ICategoryCallbackListener {
    fun onCategoryLoadSucces(categoriesList:List<CategoryModel>)
    fun onCategoryLoadFailed(message:String)
}