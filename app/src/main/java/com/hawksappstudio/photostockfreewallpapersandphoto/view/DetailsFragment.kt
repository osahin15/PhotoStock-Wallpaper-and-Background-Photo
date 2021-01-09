package com.hawksappstudio.photostockfreewallpapersandphoto.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.UNSPLASH_LINK
import com.hawksappstudio.photostockfreewallpapersandphoto.viewmodel.DetailsViewModel
import kotlinx.android.synthetic.main.fragment_details.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.StringBuilder
import java.util.*


class DetailsFragment : Fragment() {


    //ca-app-pub-3058271853907431/9316207921 interstitial

    private val requestOptions = RequestOptions().placeholder(R.color.black)
    private var USER_HTML = "https://unsplash.com/"
    lateinit var navController: NavController
    private lateinit var imageId: String

    private var image: String? = null
    private var msg: String? = ""
    private var lastMsg = ""

    private lateinit var  detailsViewModel : DetailsViewModel

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageId = arguments?.getString("imageId").toString()

        MobileAds.initialize(requireContext()) {}

        mInterstitialAd = InterstitialAd(requireContext())
        mInterstitialAd.adUnitId = "ca-app-pub-3058271853907431/9316207921"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_details, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        detailsViewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        detailsViewModel.getPhotoDetails(imageId)
        observePhotoDetails()



        var download = view.findViewById<ImageView>(R.id.save_btn)
        var share = view.findViewById<ImageView>(R.id.share_btn)
        var backButton = view.findViewById<ImageView>(R.id.back_button)
        var info = view.findViewById<ImageView>(R.id.info_btn)

        val animation = AnimationUtils.loadAnimation(context,R.anim.infoanim)
        val animationClose = AnimationUtils.loadAnimation(context,R.anim.infoanimclose)


        info.setOnClickListener {
            if (info_Layout.visibility == View.GONE)    {
                it.setBackgroundResource(R.drawable.btn_gray_bg_select)
                info_Layout.visibility = View.VISIBLE
                info_Layout.startAnimation(animation)
            }else{
                it.setBackgroundResource(R.drawable.btn_gray_bg)
                info_Layout.visibility = View.GONE
                info_Layout.startAnimation(animationClose)
            }
            if (mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            } else {
                Log.d("interstitial", "The interstitial wasn't loaded yet.")
            }
        }



        full_screen_photo.setOnClickListener {
            if (linear_btn.visibility == View.GONE && backButton.visibility  == View.GONE){
                linear_btn.visibility = View.VISIBLE
                backButton.visibility = View.VISIBLE

            }else{
                linear_btn.visibility = View.GONE
                backButton.visibility = View.GONE
            }
        }

        backButton.setOnClickListener {
            navController.navigate(R.id.action_detailsFragment_to_feedFragment)
        }

        download.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
               askPermission()
            } else {
                downloadImage(image.toString())
            }
        }

        share.setOnClickListener {
            shareImageFromURI(image)
        }


        unsplash_link.text = underLine(unsplash_link.text.toString())
        unsplash_link.setOnClickListener {
            openLink(UNSPLASH_LINK)
        }
        name_link.setOnClickListener {
            openLink(USER_HTML)
        }

    }

    fun openLink(link:String){
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(browserIntent)
    }



    fun underLine(text:String): SpannableString {
        var underline = SpannableString(text)
        underline.setSpan(UnderlineSpan(),0,underline.length,0)
        return underline
    }

    @SuppressLint("SetTextI18n", "ResourceType")
    fun initView(photo : Model.Photo){

        if (photo.location.city != null || photo.location.country != null){
            location_text.text = photo.location.city + ", " + photo.location.country
        }else{
            location_text.text = "no location info"
        }
        name_link.text = underLine(photo.user.name)

        var tagBuilder = StringBuilder()
        if (photo.tags.isNotEmpty()){
            for (tag in photo.tags) {
                tagBuilder.append("#").append(tag.title).append(" ")
            }
        }else{
            tagBuilder.append(" ")
        }


        tag_info.text = tagBuilder.toString()

        save_info.text = photo.downloads.toString()
        like_info.text = photo.likes.toString()
        if (photo.exif.model != null){
            exif_model_info.text = photo.exif.model.toString()
        }else{
            exif_model_info.text = "no model info"
        }


        detailsProgress.visibility = View.VISIBLE

        image = photo.urls.full

        Glide.with(requireContext()).setDefaultRequestOptions(requestOptions).load(photo.urls.regular).listener(object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                detailsProgress.visibility = View.GONE
                e?.printStackTrace()
                Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG).show()
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                detailsProgress.visibility = View.GONE
                return false
            }

        }).into(full_screen_photo)

        USER_HTML = photo.user.links.html


    }



    fun observePhotoDetails(){
        detailsViewModel.photoData.observe(viewLifecycleOwner,{
            it.let {
                detailsProgress.visibility = View.GONE
                initView(it)
            }
        })
        detailsViewModel.photoError.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    detailsProgress.visibility = View.GONE
                    detailsError.visibility = View.VISIBLE
                }else{
                    detailsError.visibility = View.GONE
                }
            }
        })
        detailsViewModel.photoLoading.observe(viewLifecycleOwner,{
            it.let {
                if (it){
                    detailsProgress.visibility = View.VISIBLE
                    detailsError.visibility = View.GONE
                }else{
                    detailsProgress.visibility = View.GONE
                }
            }
        })
    }

  private fun askPermission(){
      if (context?.let {it1->
                  ContextCompat.checkSelfPermission(
                          it1,
                          android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                  )
              }!= PackageManager.PERMISSION_GRANTED){
          if (ActivityCompat.shouldShowRequestPermissionRationale(
                          context as Activity,
                          android.Manifest.permission.WRITE_EXTERNAL_STORAGE
          )){
              AlertDialog.Builder(context as Activity)
                      .setTitle("Permission Required")
                      .setMessage("Permission required to save photos from Photo Stock App.")
                      .setPositiveButton("Accept") { dialog, id ->
                          ActivityCompat.requestPermissions(
                                  context as Activity,
                                  arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                  123
                          )
                      }
                      .setNegativeButton("Deny") { dialog, id -> dialog.cancel() }
                      .show()
          } else{
              ActivityCompat.requestPermissions(
                      context as Activity,
                      arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                      123
              )
          }
      }else{
          downloadImage(image.toString())
      }
  }

    private fun downloadImage(url:String) {

        var dialog = ProgressDialog(requireContext())
         dialog.setMessage("Downloading..")
         dialog.show()
        val mCoordinator = requireActivity().findViewById<ConstraintLayout>(R.id.constrainDetail)

        val snackbar = Snackbar.make(mCoordinator, R.string.image_saved_success, Snackbar.LENGTH_INDEFINITE)
        var sbview = snackbar.view
        sbview.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.link_blue))
        val name = UUID.randomUUID().toString()

        Glide.with(requireContext()).asBitmap().load(url).into( object : CustomTarget<Bitmap>(){
            override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {


                var fos : OutputStream?=null
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
                    var resolver = context?.contentResolver
                    var contentValues = ContentValues()
                    contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "$name.jpg")
                    contentValues.put(MediaStore.Images.Media.MIME_TYPE,"image/jpg")
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH,Environment.DIRECTORY_PICTURES)
                    var url : Uri?=null
                    var imageUri =
                        resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues)
                    Log.d("download", "onBitmapLoaded: ${imageUri.toString()} ")
                    if (resolver != null) {
                        fos = resolver.openOutputStream(imageUri!!)
                    }
                }else{
                    var imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                    var image = File(imagesDir, "$name.jpg")
                    fos = FileOutputStream(image)
                }


                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos)
                fos?.close()

                dialog.dismiss()
                snackbar.setAction("Open"){
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_VIEW
                    intent.data = contentUri
                    context?.startActivity(Intent.createChooser(intent, "Select Gallery App"))
                    snackbar.dismiss()
                }.show()
            Handler().postDelayed({
                snackbar.dismiss()
            },5000)

            }
            override fun onLoadCleared(placeholder: Drawable?) {
            }

        })

    }


    /*fun downloadImage(url:String){

        try{
            val directory = File(Environment.DIRECTORY_PICTURES)
            if (!directory.exists()){
                directory.mkdirs()
            }

            val downloadManager = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val downloadUri = Uri.parse(url)

            val request = DownloadManager.Request(downloadUri).apply {
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setTitle(url.substring(url.lastIndexOf("/")+1))
                        .setDescription("")
                        .setDestinationInExternalPublicDir(
                                directory.toString(), url.substring(url.lastIndexOf("/")+1)
                        )
            }

            val downloadId = downloadManager.enqueue(request)
            val query = DownloadManager.Query().setFilterById(downloadId)
            Thread(Runnable {
                var downloading = true
                while (downloading){
                    val cursor : Cursor = downloadManager.query(query)
                    cursor.moveToFirst()
                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))== DownloadManager.STATUS_SUCCESSFUL){
                        downloading = false
                    }
                    val status  = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    msg = statusMessage(url,directory,status)

                    if(msg!=lastMsg){
                        requireActivity().runOnUiThread{
                            Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show()
                        }
                        lastMsg = msg ?: ""
                    }
                    cursor.close()
                }
            }).start()

        }catch (e : Exception){
            Toast.makeText(requireContext(),"Image Download Failed", Toast.LENGTH_SHORT).show()
        }



    }*/

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
       when(requestCode){
           123->{
               if ((grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                   downloadImage(image.toString())
               }else{
                   Toast.makeText(requireContext(),"Permission denied.You now have to manually give permission from settings for this task. ",Toast.LENGTH_SHORT).show()

               }
               return
           }
           else ->{
           }
       }
    }




    private fun shareImageFromURI(url:String?){
            detailsProgress.visibility = View.VISIBLE
            Glide.with(requireContext()).asBitmap().load(url).into( object : CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                   val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "image/*"
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.putExtra(Intent.EXTRA_STREAM,getBitmapFromView(resource))
                    startActivity(Intent.createChooser(intent,"Share Image"))
                    detailsProgress.visibility = View.GONE
                }
                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }




    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download Failed. Try Again."
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully"
            else -> "There's nothing to download"
        }
        return msg
    }


    fun getBitmapFromView(bmp: Bitmap?): Uri? {
        var bmpUri: Uri? = null
        try {
            val file = File(
                    requireActivity().externalCacheDir,
                    System.currentTimeMillis().toString() + ".jpg"
            )

            val out = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            bmpUri = FileProvider.getUriForFile(requireContext(), context?.applicationContext?.packageName + ".provider",file)

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bmpUri
    }


}