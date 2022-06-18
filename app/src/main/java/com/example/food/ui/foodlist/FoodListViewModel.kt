package com.example.food.ui.foodlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.food.Common.Common
import com.example.food.model.FoodModel

class FoodListViewModel : ViewModel() {

    private var mutableFoodModelListData : MutableLiveData<List<FoodModel>>? = null

    fun getMutableFoodModelListData(): MutableLiveData<List<FoodModel>>{
        if (mutableFoodModelListData == null)
            mutableFoodModelListData = MutableLiveData()
        mutableFoodModelListData!!.value = Common.categorySelected!!.foods
        return mutableFoodModelListData!!

    }
}