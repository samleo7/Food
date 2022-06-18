package com.example.food.ui.cart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.Common.Common
import com.example.food.Database.CartDataSource
import com.example.food.Database.CartDatabase
import com.example.food.Database.LocalCartDataSource
import com.example.food.EventBus.HideFABCart
import com.example.food.EventBus.UpdateItemInCart
import com.example.food.R
import com.example.food.adapter.MyCartAdapter
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartFragment : Fragment() {

    private var cartDataSource:CartDataSource?=null
    private var compositeDisposable:CompositeDisposable = CompositeDisposable()
    private var recyclerViewState:Parcelable?=null
    private lateinit var cartViewModel: CartViewModel

    var txt_empty_cart: TextView? = null
    var txt_total_price:TextView? = null
    var group_place_holder:CardView? = null
    var recycler_cart:RecyclerView? = null

    override fun onResume() {
        super.onResume()
        calulateTotalPrice()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        EventBus.getDefault().postSticky(HideFABCart(true))

        cartViewModel =
                ViewModelProvider(this).get(CartViewModel::class.java)
        //After create cartViewModel, init data source
        cartViewModel.initCartdataSource(requireContext())
        val root = inflater.inflate(R.layout.fragment_cart, container, false)
        initViews(root)
        cartViewModel.getMutableLiveDataCartItem().observe(viewLifecycleOwner, Observer {
            if (it == null || it.isEmpty())
            {
                recycler_cart!!.visibility = View.GONE
                group_place_holder!!.visibility = View.GONE
                txt_empty_cart!!.visibility = View.VISIBLE
            }
            else
            {
                recycler_cart!!.visibility = View.VISIBLE
                group_place_holder!!.visibility = View.VISIBLE
                txt_empty_cart!!.visibility = View.GONE

                val adapter = MyCartAdapter(requireContext(),it)
                recycler_cart!!.adapter = adapter
            }
        } )

        return root
    }

    private fun initViews(root:View) {

        cartDataSource = LocalCartDataSource(CartDatabase.getInstance(requireContext()).cartDAO())

        recycler_cart = root.findViewById(R.id.id_recycler_cart) as RecyclerView
        recycler_cart!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))

        txt_empty_cart = root.findViewById(R.id.id_txt_empty_cart) as TextView
        txt_total_price = root.findViewById(R.id.id_txt_total_price) as TextView
        group_place_holder = root.findViewById(R.id.id_group_place_holder) as CardView

    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        cartViewModel!!.onStop()
        compositeDisposable.clear()
        EventBus.getDefault().postSticky(HideFABCart(false))
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    @Subscribe(sticky = true,threadMode = ThreadMode.MAIN)
    fun onUpdateItemInCart(event:UpdateItemInCart){
        if (event.cartItem != null)
        {
            recyclerViewState = recycler_cart!!.layoutManager!!.onSaveInstanceState()
            cartDataSource!!.updateCart(event.cartItem)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object :SingleObserver<Int>{

                        override fun onSuccess(t: Int) {
                            calulateTotalPrice()
                            recycler_cart!!.layoutManager!!.onRestoreInstanceState(recyclerViewState)

                        }

                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onError(e: Throwable) {
                            Toast.makeText(context,"[UPDATE CART]" +e.message,Toast.LENGTH_SHORT).show()

                        }

                    })
        }
    }

    private fun calulateTotalPrice() {
        cartDataSource!!.sumPrice(Common.currentUser!!.uid!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object :SingleObserver<Double>{

                    override fun onSuccess(price: Double) {
                        txt_total_price!!.text = StringBuilder("Total: ")
                                .append(Common.formatPrice(price))
                    }

                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context,"[SUM CART]"+e.message,Toast.LENGTH_SHORT).show()
                    }

                })
    }

}