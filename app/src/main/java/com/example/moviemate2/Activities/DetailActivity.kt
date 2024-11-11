package com.example.moviemate2.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.example.moviemate2.Activities.SeatListActivity
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.moviemate2.Adapters.ActorsListAdapter
import com.example.moviemate2.Domain.FilmItem
import com.example.moviemate2.R
import com.google.gson.Gson

class DetailActivity : AppCompatActivity() {
    private lateinit var mRequestQueue: RequestQueue
    private lateinit var progressBar: ProgressBar
    private lateinit var titleTxt: TextView
    private lateinit var movieRateTxt: TextView
    private lateinit var movieTimeTxt: TextView
    private lateinit var movieSummaryInfo: TextView
    private lateinit var movieActorsInfo: TextView
    private lateinit var pic2: ImageView
    private lateinit var backImg: ImageView
    private lateinit var recyclerViewActors: RecyclerView
    private lateinit var scrollView: NestedScrollView
    private lateinit var bookTicketsButton: Button
    private var adapterActorList: RecyclerView.Adapter<*>? = null
    private var idFilm: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        idFilm = intent.getIntExtra("id", 0)
        initView()
        sendRequest()
    }

    private fun sendRequest() {
        mRequestQueue = Volley.newRequestQueue(this)
        progressBar.visibility = View.VISIBLE
        scrollView.visibility = View.GONE

        val url = "https://moviesapi.ir/api/v1/movies/$idFilm"
        val stringRequest = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            progressBar.visibility = View.GONE
            scrollView.visibility = View.VISIBLE

            val item = gson.fromJson(response, FilmItem::class.java)

            Glide.with(this)
                .load(item.poster)
                .into(pic2)

            titleTxt.text = item.title
            movieRateTxt.text = item.imdbRating
            movieTimeTxt.text = item.runtime
            movieSummaryInfo.text = item.plot
            movieActorsInfo.text = item.actors
            item.images?.let {
                adapterActorList = ActorsListAdapter(it)
                recyclerViewActors.adapter = adapterActorList
            }
        }, { error: VolleyError? ->
            progressBar.visibility = View.GONE
        })

        mRequestQueue.add(stringRequest)
    }

    private fun initView() {
        titleTxt = findViewById(R.id.movieNameTxt)
        progressBar = findViewById(R.id.progressBarDetail)
        scrollView = findViewById(R.id.scrollView2)
        pic2 = findViewById(R.id.picDetail)
        movieRateTxt = findViewById(R.id.movieStar)
        movieTimeTxt = findViewById(R.id.movieTime)
        movieSummaryInfo = findViewById(R.id.movieSummery)
        movieActorsInfo = findViewById(R.id.movieActorInfo)
        backImg = findViewById(R.id.backImg)
        recyclerViewActors = findViewById(R.id.imagesRecycler)
        bookTicketsButton = findViewById(R.id.bookTicketsButton)  // Initialize the button

        recyclerViewActors.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        backImg.setOnClickListener { finish() }

        // Set the click listener for the book tickets button
        bookTicketsButton.setOnClickListener {
            val intent = Intent(this, SeatListActivity::class.java)
            intent.putExtra("id", idFilm) // Pass the film ID to the next activity if needed
            startActivity(intent)
        }
    }
}
