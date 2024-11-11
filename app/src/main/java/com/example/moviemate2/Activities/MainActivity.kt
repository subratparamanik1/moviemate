package com.example.moviemate2.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.moviemate2.Adapters.FilmListAdapter
import com.example.moviemate2.Adapters.SliderAdapter
import com.example.moviemate2.Domain.ListFilm
import com.example.moviemate2.Domain.SliderItems
import com.example.moviemate2.R
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var adapterTrending: RecyclerView.Adapter<*>
    private lateinit var adapterLatestLaunches: RecyclerView.Adapter<*>
    private lateinit var recyclerViewTrending: RecyclerView
    private lateinit var recyclerViewLatestLaunches: RecyclerView
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var mStringRequest: StringRequest
    private lateinit var mStringRequest2: StringRequest
    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var viewPager2: ViewPager2
    private lateinit var profileButton: ImageView
    private lateinit var orderButton: ImageView
    private lateinit var wishlistButton: ImageView
    private lateinit var exploreButton: ImageView
    private val slideHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
        banners()
        sendRequestTrending()
        sendRequestLatestLaunches()
        profileButton = findViewById(R.id.imageView6)
        profileButton.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        orderButton = findViewById(R.id.imageView5)
        orderButton.setOnClickListener{
            Toast.makeText(this, "Order clicked !", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, OrderActivity::class.java)
//            startActivity(intent)
        }
        wishlistButton = findViewById(R.id.pic)
        wishlistButton.setOnClickListener{
            Toast.makeText(this, "Wishlist clicked !", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, WishlistActivity::class.java)
//            startActivity(intent)
        }
        exploreButton = findViewById(R.id.imageView3)
        exploreButton.setOnClickListener{
            Toast.makeText(this, "Explore clicked !", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this, ExploreActivity::class.java)
//            startActivity(intent)
        }
    }

    private fun sendRequestTrending() {
        mRequestQueue = Volley.newRequestQueue(this)
        loading1.visibility = View.VISIBLE
        mStringRequest = StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=1", { response ->
            val gson = Gson()
            loading1.visibility = View.GONE
            val items = gson.fromJson(response, ListFilm::class.java)
            adapterTrending = FilmListAdapter(items)
            recyclerViewTrending.adapter = adapterTrending
        }, { error ->
            loading1.visibility = View.GONE
            Log.i("UiLover", "onErrorResponse: $error")
        })
        mRequestQueue.add(mStringRequest)
    }

    private fun sendRequestLatestLaunches() {
        mRequestQueue = Volley.newRequestQueue(this)
        loading2.visibility = View.VISIBLE
        mStringRequest2 = StringRequest(Request.Method.GET, "https://moviesapi.ir/api/v1/movies?page=2", { response ->
            val gson = Gson()
            loading2.visibility = View.GONE
            val items = gson.fromJson(response, ListFilm::class.java)
            adapterLatestLaunches = FilmListAdapter(items)
            recyclerViewLatestLaunches.adapter = adapterLatestLaunches
        }, { error ->
            loading2.visibility = View.GONE
            Log.i("UiLover", "onErrorResponse: $error")
        })
        mRequestQueue.add(mStringRequest2)
    }

    private fun banners() {
        val sliderItems = mutableListOf(
            SliderItems(R.drawable.wide),
            SliderItems(R.drawable.wide1),
            SliderItems(R.drawable.wide3)
        )

        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 3
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer(ViewPager2.PageTransformer { page, position ->
                val r = 1 - Math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            })
        }

        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.currentItem = 1
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                slideHandler.removeCallbacks(sliderRunnable)
            }
        })
    }

    private val sliderRunnable = Runnable {
        viewPager2.currentItem = viewPager2.currentItem + 1
    }

    override fun onPause() {
        super.onPause()
        slideHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        slideHandler.postDelayed(sliderRunnable, 2000)
    }

    private fun initView() {
        viewPager2 = findViewById(R.id.viewpagerSlider)
        recyclerViewTrending = findViewById(R.id.view1)
        recyclerViewTrending.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewLatestLaunches = findViewById(R.id.view2)
        recyclerViewLatestLaunches.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        loading1 = findViewById(R.id.progressBar1)
        loading2 = findViewById(R.id.progressBar2)
    }
}
