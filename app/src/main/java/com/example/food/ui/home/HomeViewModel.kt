package com.example.food.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.food.Callback.IBestDealLoadCallBack
import com.example.food.Callback.IPopularLoadCallback
import com.example.food.Common.Common
import com.example.food.model.BestDealModel
import com.example.food.model.PopularCategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel(), IPopularLoadCallback, IBestDealLoadCallBack {

    override fun onBestDealLoadSucces(bestDealList: List<BestDealModel>) {
        bestDealListMutableLiveData!!.value = bestDealList
    }

    override fun onBestDealLoadFailed(message: String) {
       messageError.value = message
    }

    override fun onPopularLoadSucces(popularModelList: List<PopularCategoryModel>) {
        popularListMutableLiveData!!.value = popularModelList
    }

    override fun onPopularLoadFailed(message: String) {
        messageError.value = message
    }

    private var popularListMutableLiveData:MutableLiveData<List<PopularCategoryModel>>?=null
    private var bestDealListMutableLiveData:MutableLiveData<List<BestDealModel>>?=null
    private lateinit var messageError:MutableLiveData<String>
    private  var popularLoadCallbackListener:IPopularLoadCallback
    private var bestDealCallbackListener:IBestDealLoadCallBack

    ////Lista del viewPager
    val bestDealList:LiveData<List<BestDealModel>>
        get() {
            if (bestDealListMutableLiveData == null)
            {
                bestDealListMutableLiveData = MutableLiveData()
                messageError = MutableLiveData()
                loadBestDealList()
            }
            return bestDealListMutableLiveData!!
        }

    private fun loadBestDealList() {
        val tempList = ArrayList<BestDealModel>()
        val bestDealrRef = FirebaseDatabase.getInstance().getReference(Common.BEST_DEALS_REF)

        bestDealrRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                bestDealCallbackListener.onBestDealLoadFailed((error.message))
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot!!.children){
                    val model = itemSnapShot.getValue<BestDealModel>(BestDealModel::class.java)
                    tempList.add(model!!)
                }
                bestDealCallbackListener.onBestDealLoadSucces(tempList)
            }
        })
    }


    //Lista del recyclerSuperior Horizontal
    val popularList:LiveData<List<PopularCategoryModel>>
        get() {
             if(popularListMutableLiveData == null)
             {
                 popularListMutableLiveData = MutableLiveData()
                 messageError = MutableLiveData()
                 loadPopularList()
             }
             return popularListMutableLiveData!!
            }

    private fun loadPopularList() {
        val tempList = ArrayList<PopularCategoryModel>()
        val popularRef = FirebaseDatabase.getInstance().getReference(Common.POPULAR_REF)

        popularRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                popularLoadCallbackListener.onPopularLoadFailed((error.message))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot!!.children){
                    val model = itemSnapShot.getValue<PopularCategoryModel>(PopularCategoryModel::class.java)
                    tempList.add(model!!)
                }
                popularLoadCallbackListener.onPopularLoadSucces(tempList)
            }
        })
    }

    init {
        popularLoadCallbackListener = this
        bestDealCallbackListener = this
    }

}