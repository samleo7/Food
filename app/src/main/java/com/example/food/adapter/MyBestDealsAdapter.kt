package com.example.food.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.asksira.loopingviewpager.LoopingPagerAdapter
import com.bumptech.glide.Glide
import com.example.food.R
import com.example.food.model.BestDealModel

class MyBestDealsAdapter(context:Context,
                         itemList:List<BestDealModel>,
                         isInfinite:Boolean):LoopingPagerAdapter<BestDealModel>(context,itemList, isInfinite) {
    override fun inflateView(viewType: Int, container: ViewGroup?, listPosition: Int): View {
       return LayoutInflater.from(context)
           .inflate(R.layout.layout_best_deals_item, container!!, false)
    }

    override fun bindView(convertView: View?, listPosition: Int, viewType: Int) {
       val imageView = convertView!!.findViewById<ImageView>(R.id.id_img_best_deal)
        val textView = convertView!!.findViewById<TextView>(R.id.id_txt_best_deal)
        //Set data
        Glide.with(context).load(itemList[listPosition].image).into(imageView)
        textView.text = itemList[listPosition].name
    }
}