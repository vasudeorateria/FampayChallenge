package com.kjstudios.fampaychallenge

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.kjstudios.fampaychallenge.adapter.RvAdapter
import com.kjstudios.fampaychallenge.data.RetrofitInstance
import com.kjstudios.fampaychallenge.models.CardGroupList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity() {

    private val swipeRefreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.swipe)
    }
    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }

    var cardGroupsList: CardGroupList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SharedPrefs(this).clearRemindLater()
        loadData()
        swipeRefreshLayout.setOnRefreshListener {
            if (swipeRefreshLayout.isRefreshing) {
                loadData()
            }
        }
    }

    private fun loadData() {
        swipeRefreshLayout.isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            getData()
        }
    }

    private fun getData() {
        RetrofitInstance.retrofit.getCardGroupList()
            .enqueue(object : Callback<CardGroupList> {
                override fun onResponse(
                    call: Call<CardGroupList>,
                    response: Response<CardGroupList>
                ) {
                    swipeRefreshLayout.isRefreshing = false
                    cardGroupsList = response.body()
                    if (cardGroupsList != null) {
                        setupView()
                    }else{
                        toast(getString(R.string.error_while_loading))
                        Log.e("MainActivity", "onError: ${response.errorBody().toString()}")
                    }
                }

                override fun onFailure(call: Call<CardGroupList>, t: Throwable) {
                    swipeRefreshLayout.isRefreshing = false
                    toast(getString(R.string.failed_to_load))
                    Log.e("MainActivity", "onFailure: ${t.message}")
                }
            })
    }

    private fun setupView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RvAdapter(context, cardGroupsList!!.card_groups)
            setHasFixedSize(true)
        }
    }

    fun toast(message:String){
        Toast.makeText(
            this@MainActivity,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

}

