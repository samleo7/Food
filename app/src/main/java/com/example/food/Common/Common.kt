package com.example.food.Common

import com.example.food.model.*
import java.lang.StringBuilder
import java.math.RoundingMode
import java.text.DecimalFormat

object Common {

    fun formatPrice(price: Double): String {
        if (price != 0.toDouble()) {
            val df = DecimalFormat("#,##0.00")
            df.roundingMode = RoundingMode.HALF_UP
            val finalPrice = StringBuilder(df.format(price)).toString()
            return finalPrice.replace(".", ",")
        } else
            return "0,00"
    }

    fun calculateExtraPrice(
            userSelectedSize: SizeModel?,
            userSelectedAddon: MutableList<AddonModel>?
    ): Double {
        var result = 0.0
        if (userSelectedSize == null && userSelectedAddon ==  null)
            return 0.0
        else if (userSelectedSize == null)
        {
            for (addonModel in userSelectedAddon!!)
                result += addonModel.price!!.toDouble()
            return result
        }
        else if (userSelectedAddon == null)
        {
            result = userSelectedSize!!.price.toDouble()
            return  result
        }
        else
        {
            result = userSelectedSize!!.price.toDouble()
            for (addonModel in userSelectedAddon!!)
                result += addonModel.price!!.toDouble()
            return result
        }


    }

    val COMMENT_REF: String = "Comments"
    var foodSelected: FoodModel?=null
    var categorySelected: CategoryModel? = null
    val CATEGORY_REF: String = "Category"
    val FULL_WIDTH_COLUMN: Int = 1
    val DEFAULT_COLUMN_COUNT: Int = 0
    val BEST_DEALS_REF:String = "BestDeals"
    val POPULAR_REF:String = "MostPopular"
    val USER_REFERENCE = "Users"
    val currentUser:UserModel?=null
}