package com.example.food.Callback

import com.example.food.model.BestDealModel

interface IBestDealLoadCallBack {
    fun onBestDealLoadSucces(bestDealList:List<BestDealModel>)
    fun onBestDealLoadFailed(message:String)
}