package com.example.newsapplication_r1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapplication_r1.api.ApiManager
import com.example.newsapplication_r1.model.NewsResponse
import com.example.newsapplication_r1.model.SourcesItem
import com.example.newsapplication_r1.model.SourcesResponse
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    lateinit var tabLayout: TabLayout
    lateinit var progressBar: ProgressBar
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        getSources()

    }

    val adapter = NewsAdapter(null)
    fun initView() {

        tabLayout = findViewById(R.id.tab_layout)
        progressBar = findViewById(R.id.progress_bar)
        recyclerView = findViewById(R.id.recycler_view)

        recyclerView.adapter = adapter

    }

    fun getSources() {

        ApiManager.getApi().getSources(Constants.apiKey)
            .enqueue(object : Callback<SourcesResponse> {

                override fun onResponse(
                    call: Call<SourcesResponse>,
                    response: Response<SourcesResponse>
                ) {

                    // add data to tab layout

                    progressBar.isVisible = false

                    addSourcesToTabLayout(response.body()?.sources)

                }

                override fun onFailure(call: Call<SourcesResponse>, t: Throwable) {

                    Log.e("error", t.localizedMessage)
                }

            })

    }

    fun addSourcesToTabLayout(sources: List<SourcesItem?>?) {

        sources?.forEach { source ->

            val tab = tabLayout.newTab()
            tab.setText(source?.name)

            tab.tag = source
            tabLayout.addTab(tab)

        }

        tabLayout.addOnTabSelectedListener(
            object : TabLayout.OnTabSelectedListener {

                override fun onTabSelected(tab: TabLayout.Tab?) {

                    val source = tab?.tag as SourcesItem
                    getNewsBySource(source)

                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    return;
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    val source = tab?.tag as SourcesItem
                    getNewsBySource(source)
                }

            }
        )
        tabLayout.getTabAt(0)?.select()


    }


    fun getNewsBySource(source: SourcesItem) {

        progressBar.isVisible = true;

        ApiManager.getApi().getNews(Constants.apiKey, source.id ?: "")
            .enqueue(object : Callback<NewsResponse> {

                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    progressBar.isVisible = false
                    // load data to RV
                    adapter.changeDate(response.body()?.articles)
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    progressBar.isVisible = false
                }


            })


    }


}