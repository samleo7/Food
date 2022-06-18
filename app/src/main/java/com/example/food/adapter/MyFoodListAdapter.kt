package com.example.food.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.food.Callback.IRecyclerItemClickListener
import com.example.food.Common.Common
import com.example.food.Database.CartDataSource
import com.example.food.Database.CartDatabase
import com.example.food.Database.CartItem
import com.example.food.Database.LocalCartDataSource
import com.example.food.EventBus.CategoryClick
import com.example.food.EventBus.CountCartEvent
import com.example.food.EventBus.FoodItemClick
import com.example.food.R
import com.example.food.model.FoodModel
import io.reactivex.Scheduler
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.io
import org.greenrobot.eventbus.EventBus

class MyFoodListAdapter (internal var context: Context,
                         internal var foodList:List<FoodModel>):
        RecyclerView.Adapter<MyFoodListAdapter.MyViewHolder>() {

    private val compositeDisposable:CompositeDisposable
    private val cartDataSource:CartDataSource

    init {
        compositeDisposable = CompositeDisposable()
        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(context).cartDAO())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFoodListAdapter.MyViewHolder {
        return MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_food_item, parent, false))
    }

    override fun getItemCount(): Int {
        return foodList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        Glide.with(context)
                .load(foodList.get(position).image)
                .into(holder.img_food_image!!)
        holder.txt_food_name!!.setText(foodList.get(position).name)
        holder.txt_food_price!!.setText(foodList.get(position).price.toString())

        //Event
        holder.setListener(object :IRecyclerItemClickListener{
            override fun onItemClick(view: View, pos: Int) {
                Common.foodSelected = foodList.get(pos)
                Common.foodSelected!!.key = pos.toString()

                EventBus.getDefault().postSticky(FoodItemClick(true,foodList.get(pos)))
            }
        })

        holder.img_cart!!.setOnClickListener {
            val cartItem = CartItem()
            cartItem.uid = Common.currentUser!!.uid
            cartItem.userPhone = Common.currentUser!!.phone

            cartItem.foodId = foodList.get(position).id!!
            cartItem.foodName = foodList.get(position).name!!
            cartItem.foodImage = foodList.get(position).image!!
            cartItem.foodPrice = foodList.get(position).price!!.toDouble()
            cartItem.foodQuantity = 1
            cartItem.foodExtraPrice = 0.0
            cartItem.foodAddon = "Default"
            cartItem.foodSize = "Default"

            cartDataSource.getItemWithAllOptionsInCart(Common.currentUser!!.uid!!,
                    cartItem.foodId,
                    cartItem.foodSize!!,
                    cartItem.foodAddon!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :SingleObserver<CartItem>{
                        override fun onSuccess(cartItemFromDB: CartItem) {
                            if (cartItemFromDB.equals(cartItem))
                            {
                                //If item already in database, just update
                                cartItemFromDB.foodExtraPrice = cartItem.foodExtraPrice
                                cartItemFromDB.foodAddon = cartItem.foodAddon
                                cartItemFromDB.foodSize = cartItem.foodSize
                                cartItemFromDB.foodQuantity = cartItemFromDB.foodQuantity + cartItem.foodQuantity

                                cartDataSource.updateCart(cartItemFromDB)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(object :SingleObserver<Int>{
                                            override fun onSuccess(t: Int) {
                                                Toast.makeText(context, "Update Cart Success", Toast.LENGTH_SHORT).show()
                                                EventBus.getDefault().postSticky(CountCartEvent(true))
                                            }

                                            override fun onSubscribe(d: Disposable) {

                                            }

                                            override fun onError(e: Throwable) {
                                                Toast.makeText(context,"[UPDATE CART]"+e.message,Toast.LENGTH_SHORT).show()

                                            }

                                        })
                            }
                            else
                            {
                                //If item not available in database, just insert
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Toast.makeText(context, "Add to cart success", Toast.LENGTH_SHORT).show()
                                            //Here we will send a notify to HomeActivity to update CounterFab
                                            EventBus.getDefault().postSticky(CountCartEvent(true))
                                        },{
                                            t:Throwable? -> Toast.makeText(context, "[INSERT CART]"+t!!.message,Toast.LENGTH_SHORT).show()
                                        }))
                            }

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            if (e.message!!.contains("empty"))
                            {
                                compositeDisposable.add(cartDataSource.insertOrReplaceAll(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({
                                            Toast.makeText(context, "Add to cart success", Toast.LENGTH_SHORT).show()
                                            //Here we will send a notify to HomeActivity to update CounterFab
                                            EventBus.getDefault().postSticky(CountCartEvent(true))
                                        },{
                                            t:Throwable? -> Toast.makeText(context, "[INSERT CART]"+t!!.message,Toast.LENGTH_SHORT).show()
                                        }))
                            }
                            else
                                Toast.makeText(context, "[CART ERROR]"+e.message,Toast.LENGTH_SHORT).show()

                        }

                    })
        }
    }

    fun onStop(){
        if (compositeDisposable != null)
            compositeDisposable.clear()
    }

    inner class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
            View.OnClickListener  {

        override fun onClick(view: View?) {
            listener!!.onItemClick(view!!,adapterPosition)
        }

        var txt_food_name: TextView?=null
        var txt_food_price: TextView?=null

        var img_food_image: ImageView? = null
        var img_fav: ImageView? = null
        var img_cart: ImageView? = null

        internal var listener:IRecyclerItemClickListener?=null

        fun setListener(listener:IRecyclerItemClickListener)
        {
            this.listener = listener
        }

        init {
            txt_food_name = itemView.findViewById(R.id.id_txt_food_name) as TextView
            txt_food_price = itemView.findViewById(R.id.id_txt_food_price) as TextView
            img_food_image = itemView.findViewById(R.id.id_img_food_image) as ImageView
            img_cart = itemView.findViewById(R.id.id_img_quick_cart) as ImageView
            img_fav = itemView.findViewById(R.id.id_img_fav) as ImageView

            itemView.setOnClickListener(this)
        }



    }

}