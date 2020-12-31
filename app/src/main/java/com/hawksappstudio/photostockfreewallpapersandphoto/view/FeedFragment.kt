package com.hawksappstudio.photostockfreewallpapersandphoto.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.StaggeredGridAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.TopicsAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.PER_PAGE
import com.hawksappstudio.photostockfreewallpapersandphoto.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment(), StaggeredGridAdapter.SelectedPhoto {

    private lateinit var adapter : StaggeredGridAdapter
    private var adapterTopic : TopicsAdapter = TopicsAdapter(arrayListOf())
    private lateinit var navController : NavController

    private lateinit var  feedViewModel : FeedViewModel

    var isLoading = false
    var isLastPage = false
    var isScrolling  = false
    var currentPage = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view : View =  inflater.inflate(R.layout.fragment_feed, container, false)
        adapter = StaggeredGridAdapter(arrayListOf(),requireContext(),this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedViewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        feedViewModel.topicsFromApi()
        feedViewModel.photoListFromApi(currentPage)
        observePhotoListLiveData()

        navController = Navigation.findNavController(view)

        val edittext = view.findViewById<EditText>(R.id.searchText)
        val frontView = view.findViewById<View>(R.id.frontView)
        val animation = AnimationUtils.loadAnimation(context,R.anim.searchanim)
        val openTopics = AnimationUtils.loadAnimation(context,R.anim.topicsanimopen)
        val closeTopics = AnimationUtils.loadAnimation(context,R.anim.topicsanimclose)


        staggeredView.apply {
            val mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            staggeredView.layoutManager = mLayoutManager

            staggeredView.addOnScrollListener(scrollListener)
        }
        staggeredView.adapter = adapter


        val tLayoutManager = LinearLayoutManager(requireContext())
        topicsRecycler.layoutManager = tLayoutManager
        topicsRecycler.adapter = adapterTopic




        topic_linear_btn.setOnClickListener {
            observeTopicsLiveData()
            if (topic_linear.visibility == View.GONE){
                topic_linear.visibility = View.VISIBLE
                topic_linear.startAnimation(openTopics)

            }else{
                topic_linear.visibility = View.GONE
                topic_linear.startAnimation(closeTopics)
            }
        }

        searchBtn.setOnClickListener {
            frontView.visibility = View.VISIBLE
            edittext.visibility = View.VISIBLE
            edittext.startAnimation(animation)
            closeSearchBtn.visibility = View.VISIBLE
            it.visibility = View.GONE
        }

        closeSearchBtn.setOnClickListener {
            frontView.visibility = View.GONE
            searchBtn.visibility = View.VISIBLE
            if(searchText.visibility == View.VISIBLE){
                val closeanim = AnimationUtils.loadAnimation(context,R.anim.searchanimclose)
                edittext.startAnimation(closeanim)
                edittext.visibility = View.GONE
            }
            it.visibility = View.GONE
        }
        frontView.setOnClickListener {
            closeSearchBtn.visibility = View.GONE
            searchBtn.visibility = View.VISIBLE
            if(searchText.visibility == View.VISIBLE){
                val closeanim = AnimationUtils.loadAnimation(context,R.anim.searchanimclose)
                edittext.startAnimation(closeanim)
                edittext.visibility = View.GONE
            }
            it.visibility = View.GONE
        }

    }
    override fun selectedPhoto(image: Model.Photo) {
        val bundle = bundleOf("imageId" to image.urls.full)
        navController.navigate(R.id.action_feedFragment_to_detailsFragment,bundle)
    }



    val scrollListener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            
            val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
            
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            var firstVisibleItems : IntArray? = null
            firstVisibleItems = layoutManager.findFirstVisibleItemPositions(firstVisibleItems)
            var pastVisibleItems:Int? = null
            if (firstVisibleItems !=null && firstVisibleItems.isNotEmpty()){
                pastVisibleItems = firstVisibleItems[0]
            }



            if (isScrolling && (visibleItemCount + pastVisibleItems!! == totalItemCount )){
                isScrolling = false
                currentPage++
                feedViewModel.photoListFromApi(currentPage)
            }



        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }


    private fun observePhotoListLiveData(){

        feedViewModel.photoData.observe(viewLifecycleOwner, {
            it.let {
                Log.d("photogelmesi", "observePhotoListLiveData: $it")

                paginationProgressBar.visibility = View.GONE
                staggeredView.visibility = View.VISIBLE
                adapter.updatePhotoList(it)
            }
        })
        feedViewModel.photoError.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    Log.d("photoErr", "observePhotoListLiveData: $it")
                    staggeredView.visibility = View.GONE

                }
            }
        })
        feedViewModel.photoLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    paginationProgressBar.visibility = View.VISIBLE
                    staggeredView.visibility = View.GONE
                }else{
                    paginationProgressBar.visibility = View.GONE
                }
            }
        })
    }

    private  fun observeTopicsLiveData(){
        feedViewModel.topicsData.observe(viewLifecycleOwner, {
            it.let {
                Log.d("topicgelmesi", "observeTopicsLiveData: ${it}")
                topic_progress_bar.visibility = View.GONE
                topicsRecycler.visibility = View.VISIBLE
                adapterTopic.updateTopicsList(it)
            }
        })
        feedViewModel.topicsError.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    topicsRecycler.visibility = View.GONE
                    topics_error_text.text = it.toString()
                    topic_progress_bar.visibility = View.GONE
                }else{
                    topicsRecycler.visibility = View.VISIBLE
                }
            }
        })
        feedViewModel.topicsLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    topic_progress_bar.visibility = View.VISIBLE
                    topicsRecycler.visibility = View.GONE
                    topics_error_text.visibility = View.GONE
                }else{
                    topic_progress_bar.visibility = View.GONE
                }
            }
        })
    }



}