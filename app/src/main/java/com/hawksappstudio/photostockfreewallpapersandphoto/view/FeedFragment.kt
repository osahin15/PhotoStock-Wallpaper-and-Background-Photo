package com.hawksappstudio.photostockfreewallpapersandphoto.view

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AbsListView
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.StaggeredGridAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.StaggeredTopicPhotoAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.TopicsAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.PER_PAGE
import com.hawksappstudio.photostockfreewallpapersandphoto.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.fragment_feed.*


class FeedFragment : Fragment(), StaggeredGridAdapter.SelectedPhoto,TopicsAdapter.SelectedTopic,StaggeredTopicPhotoAdapter.SelectedPhoto {

    private lateinit var adapter : StaggeredGridAdapter
    private lateinit var topicPhotoAdapter : StaggeredTopicPhotoAdapter
    private var adapterTopic : TopicsAdapter = TopicsAdapter(arrayListOf(),this)




    private lateinit var navController : NavController

    private lateinit var  feedViewModel : FeedViewModel

    private var slug :String = ""
    var isLoading = false
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
        adapter = StaggeredGridAdapter(requireContext(),this)
        topicPhotoAdapter = StaggeredTopicPhotoAdapter(requireContext(),this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedViewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        feedViewModel.topicsFromApi()
        feedViewModel.photoListFromApi(currentPage)

        navController = Navigation.findNavController(view)

        initView()
        initRecycler()


    }

    override fun selectedPhoto(image: Model.Photo) {
        val bundle = bundleOf("imageId" to image.id)
        navController.navigate(R.id.action_feedFragment_to_detailsFragment,bundle)
    }

    private fun initView(){
        val edittext = view?.findViewById<EditText>(R.id.searchText)
        val frontView = view?.findViewById<View>(R.id.frontView)
        val animation = AnimationUtils.loadAnimation(context,R.anim.searchanim)
        val openTopics = AnimationUtils.loadAnimation(context,R.anim.topicsanimopen)
        val closeTopics = AnimationUtils.loadAnimation(context,R.anim.topicsanimclose)



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
            frontView!!.visibility = View.VISIBLE
            edittext!!.visibility = View.VISIBLE
            edittext.startAnimation(animation)
            closeSearchBtn.visibility = View.VISIBLE
            it.visibility = View.GONE
        }

        closeSearchBtn.setOnClickListener {
            frontView!!.visibility = View.GONE
            searchBtn.visibility = View.VISIBLE
            if(searchText.visibility == View.VISIBLE){
                val closeanim = AnimationUtils.loadAnimation(context,R.anim.searchanimclose)
                edittext!!.startAnimation(closeanim)
                edittext.visibility = View.GONE
            }
            it.visibility = View.GONE
        }
        frontView!!.setOnClickListener {
            closeSearchBtn.visibility = View.GONE
            searchBtn.visibility = View.VISIBLE
            if(searchText.visibility == View.VISIBLE){
                val closeanim = AnimationUtils.loadAnimation(context,R.anim.searchanimclose)
                edittext!!.startAnimation(closeanim)
                edittext.visibility = View.GONE
            }
            it.visibility = View.GONE
        }

        homeBtn.setOnClickListener {
            adapterTopic.setSelectedTopic()
            fragmentManager?.beginTransaction()?.detach(this)?.attach(this)?.commit()

        }
    }
    private fun initRecycler(){

        val tLayoutManager = LinearLayoutManager(requireContext())
        topicsRecycler.layoutManager = tLayoutManager
        topicsRecycler.adapter = adapterTopic

        staggeredView.apply {
            val mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            staggeredView.layoutManager = mLayoutManager
        }


        swipyRefresh.setOnRefreshListener {
            currentPage++
            feedViewModel.photoListFromApi(currentPage)

        }

        /*staggeredView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    currentPage++
                    feedViewModel.photoListFromApi(currentPage)
                }
            }
        })*/
        staggeredView.adapter = adapter


        topicPhotoRecycler.apply {
            val nLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
           topicPhotoRecycler.layoutManager = nLayoutManager
        }

        swipyRefreshTopic.setOnRefreshListener {
            currentPage++
            feedViewModel.photoTopicsFromApiHandle(slug,currentPage)

        }

       /* topicPhotoRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE){

                }
            }
        })*/

        topicPhotoRecycler.adapter = topicPhotoAdapter


    }

    private fun observePhotoListLiveData(){

        feedViewModel.photoData.observe(viewLifecycleOwner, {
            it.let {
                Log.d("photogelmesi", "observePhotoListLiveData: ${it.size}")
                paginationProgressBar.visibility = View.GONE
                swipyRefresh.isRefreshing = false
                isLoading = false
                staggeredView.visibility = View.VISIBLE
                topicPhotoRecycler.visibility = View.GONE
                adapter.updatePhotoList(it.toList())
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
                    swipyRefresh.isRefreshing = true
                    paginationProgressBar.visibility = View.VISIBLE
                    isLoading = true
                    staggeredView.visibility = View.GONE
                }else{
                    swipyRefresh.isRefreshing = false
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

    private fun observeTopicPhoto(){
        feedViewModel.photoDataTopics.observe(viewLifecycleOwner, {
            it.let {
                staggeredView.visibility = View.GONE
                topicPhotoRecycler.visibility = View.VISIBLE
                Log.d("topicPhoto", "observeTopicPhoto: ${it.get(0).id} ")
                topicPhotoAdapter.updatePhotoList(it.toList())

            }
        })
        feedViewModel.photoError.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    Log.d("photoErr", "observePhotoListLiveData: $it")
                    topicPhotoRecycler.visibility = View.GONE
                }
            }
        })
        feedViewModel.photoLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    paginationProgressBar.visibility = View.VISIBLE
                    topicPhotoRecycler.visibility = View.GONE
                }else{
                    paginationProgressBar.visibility = View.GONE
                }
            }
        })
    }
    private fun observeTopicPhotoHandle(){
        feedViewModel.photoDataTopicsHandle.observe(viewLifecycleOwner, {
            it.let {
                staggeredView.visibility = View.GONE
                swipyRefreshTopic.isRefreshing = false
                topicPhotoRecycler.visibility = View.VISIBLE
                Log.d("topicPhotoHandle", "observeTopicPhoto: ${it.get(0).id} ")
                topicPhotoAdapter.addPhotoList(it.toList())
            }
        })
        feedViewModel.photoError.observe(viewLifecycleOwner, {
            it.let {
                if (it) {
                    Log.d("photoErr", "observePhotoListLiveData: $it")
                    topicPhotoRecycler.visibility = View.GONE
                }
            }
        })
        feedViewModel.photoLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    swipyRefreshTopic.isRefreshing = true
                    paginationProgressBar.visibility = View.VISIBLE
                    topicPhotoRecycler.visibility = View.GONE
                }else{
                    swipyRefreshTopic.isRefreshing = false
                    paginationProgressBar.visibility = View.GONE
                }
            }
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        observePhotoListLiveData()
        observeTopicPhotoHandle()
    }
    override fun selectedTopic(topic: Model.Topic) {
        //selectTopicText.visibility = View.VISIBLE
        //topicsPhotoLinear.visibility = View.VISIBLE

        searchBtn.visibility = View.GONE
        swipyRefreshTopic.visibility = View.VISIBLE
        swipyRefresh.visibility = View.GONE

        this.slug = topic.slug

        homeBtn.visibility = View.VISIBLE


        Log.d("selectTopic", "selectedTopic: $topic")
        feedViewModel.photoTopicsFromApi(this.slug,1)
        observeTopicPhoto()

    }


}