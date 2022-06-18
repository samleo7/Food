package com.example.food.ui.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.food.Callback.ICategoryCallbackListener
import com.example.food.Common.Common
import com.example.food.model.CategoryModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MenuViewModel : ViewModel(), ICategoryCallbackListener {

    override fun onCategoryLoadSucces(categoriesList: List<CategoryModel>) {
        categoriesListMutable!!.value = categoriesList
    }

    override fun onCategoryLoadFailed(message: String) {
        messageError.value = message

    }

    private var categoriesListMutable:MutableLiveData<List<CategoryModel>>?=null
    private var messageError:MutableLiveData<String> = MutableLiveData()
    private val categoryCallBackListener: ICategoryCallbackListener?

    init {
        categoryCallBackListener = this
    }

    fun getCategoryList(): MutableLiveData<List<CategoryModel>>{
        if (categoriesListMutable == null){
            categoriesListMutable = MutableLiveData()
            loadCategory()
        }
        return categoriesListMutable!!
    }

    fun getMessageError():MutableLiveData<String>{
        return messageError
    }

    private fun loadCategory() {
        val tempList = ArrayList<CategoryModel>()
        val categoryRef = FirebaseDatabase.getInstance().getReference(Common.CATEGORY_REF)

        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                categoryCallBackListener!!.onCategoryLoadFailed((error.message!!))
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapShot in snapshot!!.children){
                    val model = itemSnapShot.getValue<CategoryModel>(CategoryModel::class.java)
                    model!!.menu_id = itemSnapShot.key
                    tempList.add(model!!)
                }
                categoryCallBackListener!!.onCategoryLoadSucces(tempList)
            }
        })
    }
}