package com.hawksappstudio.photostockfreewallpapersandphoto.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.StaggeredGridAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.StaggeredGridAdapterSearch
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.StaggeredTopicPhotoAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.adapter.TopicsAdapter
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.PER_PAGE
import com.hawksappstudio.photostockfreewallpapersandphoto.viewmodel.FeedViewModel
import kotlinx.android.synthetic.main.fragment_feed.*
import java.util.*

//ca-app-pub-3058271853907431/6435071325 banner

class FeedFragment : Fragment(), StaggeredGridAdapter.SelectedPhoto,TopicsAdapter.SelectedTopic,StaggeredTopicPhotoAdapter.SelectedPhoto,
        StaggeredGridAdapterSearch.SearchPhoto , View.OnClickListener {

    private lateinit var adapter : StaggeredGridAdapter
    private lateinit var topicPhotoAdapter : StaggeredTopicPhotoAdapter
    private var adapterTopic : TopicsAdapter = TopicsAdapter(arrayListOf(),this)
    private lateinit var searchAdapter : StaggeredGridAdapterSearch




    private lateinit var navController : NavController

    private lateinit var  feedViewModel : FeedViewModel
    private var query : String = ""
    private var slug :String = ""
    var isLoading = false
    var currentPage = 1
    private var totalPage = 1
    lateinit var mAdView : AdView
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
        searchAdapter = StaggeredGridAdapterSearch(requireContext(),this)

        MobileAds.initialize(requireContext())
        mAdView = view.findViewById(R.id.feed_banner)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        feedViewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        feedViewModel.topicsFromApi()
        feedViewModel.photoListFromApi(1)

        navController = Navigation.findNavController(view)

        initView()
        initRecycler()


    }


    override fun selectedPhoto(image: Model.Photo) {
        val bundle = bundleOf("imageId" to image.id)
        navController.navigate(R.id.action_feedFragment_to_detailsFragment,bundle)
    }


    @SuppressLint("CutPasteId")
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

        textWatcher()

        searchSend.setOnClickListener {
            query = searchText.text.toString().toLowerCase(Locale.ROOT).trim()
            currentPage = 1
            if (query.isEmpty()){
                Toast.makeText(requireContext(), "Search isn't Empty.", Toast.LENGTH_SHORT).show()
            }

            feedViewModel.searchPhotoFromApi(query,1)

            searchBtn.visibility = View.VISIBLE
            homeBtn.visibility = View.VISIBLE

            it.visibility = View.GONE
            searchSwipy.visibility = View.VISIBLE
            swipyRefresh.visibility= View.GONE
            swipyRefreshTopic.visibility = View.GONE

            searchText.visibility = View.GONE

            searchText.setText("")
            frontView.visibility = View.GONE
        }

        observeSearchPhoto()

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


        staggeredView.adapter = adapter


        topicPhotoRecycler.apply {
            val nLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
           topicPhotoRecycler.layoutManager = nLayoutManager
        }

        swipyRefreshTopic.setOnRefreshListener {
            currentPage++
            feedViewModel.photoTopicsFromApiHandle(slug,currentPage)

        }
        topicPhotoRecycler.adapter = topicPhotoAdapter

       searchRecycler.apply {
            val sLayoutManager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
            this.layoutManager = sLayoutManager
        }

        searchSwipy.setOnRefreshListener {
            if (currentPage <= totalPage){
                currentPage++
                feedViewModel.searchPhotoFromApiHandle(query,currentPage)
            }
        }
        searchRecycler.adapter = searchAdapter

    }


   private fun textWatcher(){

        searchText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                if (p3!=0){
                    searchSend.visibility = View.VISIBLE
                    closeSearchBtn.visibility = View.GONE
                }else{
                    searchSend.visibility = View.GONE
                    if (searchText.visibility == View.VISIBLE){
                        closeSearchBtn.visibility  = View.VISIBLE
                    }
                }

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })

        searchText.setOnKeyListener(object : View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                    query = searchText.text.toString().toLowerCase(Locale.ROOT).trim()
                    currentPage = 1
                    if (query.isEmpty()){
                        Toast.makeText(requireContext(), "Search isn't Empty.", Toast.LENGTH_SHORT).show()
                    }

                    feedViewModel.searchPhotoFromApi(query,1)

                    searchBtn.visibility = View.VISIBLE
                    homeBtn.visibility = View.VISIBLE
                    searchSend.visibility = View.GONE

                    searchSwipy.visibility = View.VISIBLE
                    swipyRefresh.visibility= View.GONE
                    swipyRefreshTopic.visibility = View.GONE

                    searchText.visibility = View.GONE
                    searchText.setText("")
                    frontView.visibility = View.GONE
                }
                return false
            }

        })
    }



    private fun observePhotoListLiveData(){

        feedViewModel.photoData.observe(viewLifecycleOwner, {
            it.let {
                Log.d("photogelmesi", "observePhotoListLiveData: ${it.size}")
                paginationProgressBar.visibility = View.GONE
                searchRecycler.visibility = View.GONE
                topicPhotoRecycler.visibility = View.GONE
                staggeredView.visibility = View.VISIBLE
                swipyRefresh.isRefreshing = false
                isLoading = false

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
                paginationProgressBar.visibility = View.GONE
                staggeredView.visibility = View.GONE
                searchRecycler.visibility = View.GONE
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
                paginationProgressBar.visibility  = View.GONE
                topicPhotoRecycler.visibility = View.VISIBLE
                swipyRefreshTopic.isRefreshing = false
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
        observeSearchHandle()
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


    private fun observeSearchHandle(){

        feedViewModel.searchDataHandle.observe(viewLifecycleOwner,{
            it.let {
                paginationProgressBar.visibility  = View.GONE
                searchSwipy.isRefreshing = false
                searchAdapter.addPhotoList(it.results)
            }
        })
        feedViewModel.searchError.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    Toast.makeText(requireContext(),"Search Photo Loading Error! $it",Toast.LENGTH_SHORT).show()
                    searchRecycler.visibility = View.GONE
                }
            }
        })
        feedViewModel.searchLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    searchSwipy.isRefreshing = true
                    searchRecycler.visibility = View.GONE
                }else{
                    paginationProgressBar.visibility  = View.GONE
                    searchSwipy.isRefreshing = false
                }
            }
        })
    }
    private fun observeSearchPhoto(){
        feedViewModel.searchData.observe(viewLifecycleOwner, {
            it.let {
                Log.d("searchPhoto", "observeSearchPhoto: ${it.results.size}")
                Log.d("searchTotalPage", "observeSearchPhoto: ${it.total_pages}")

                staggeredView.visibility = View.GONE
                topicPhotoRecycler.visibility = View.GONE
                paginationProgressBar.visibility = View.GONE

                searchRecycler.visibility = View.VISIBLE
                searchAdapter.updatePhotoList(it.results)

                totalPage = it.total_pages!!
            }
        })
        feedViewModel.searchError.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    Toast.makeText(requireContext(),"Search Photo Loading Error! $it",Toast.LENGTH_SHORT).show()
                    searchRecycler.visibility = View.GONE
                }
            }
        })
        feedViewModel.searchLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    paginationProgressBar.visibility = View.VISIBLE
                    searchRecycler.visibility = View.GONE
                }else{
                    paginationProgressBar.visibility  = View.GONE
                }
            }
        })
    }

    override fun searchPhotoSelect(image: Model.Photo) {
        val bundle = bundleOf("imageId" to image.id)
        navController.navigate(R.id.action_feedFragment_to_detailsFragment,bundle)
    }

    override fun onClick(view: View?) {
    }


}