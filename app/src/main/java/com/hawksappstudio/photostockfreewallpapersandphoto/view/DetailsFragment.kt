package com.hawksappstudio.photostockfreewallpapersandphoto.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.UNSPLASH_LINK
import kotlinx.android.synthetic.main.fragment_details.*


class DetailsFragment : Fragment() {


    lateinit var navController: NavController
    private lateinit var imageId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageId = arguments?.getString("imageId").toString()
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

        var image =  view.findViewById<ImageView>(R.id.full_screen_photo)
        //Glide.with(requireContext()).load(imageId).centerCrop().into(image)

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
        }

        Handler().postDelayed({
            linear_btn.visibility = View.GONE
            backButton.visibility = View.GONE
        },2000)

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
            //navController.navigate(R.id.action_detailsFragment_to_feedFragment)
            requireActivity().onBackPressed()
        }

        download.setOnClickListener {
            Snackbar.make(view, "Download", Snackbar.LENGTH_LONG).show()
        }

        share.setOnClickListener {

            Snackbar.make(view, "Share", Snackbar.LENGTH_LONG).show()
        }

        val unsplash = unsplash_link.text.toString()
        val nameLnk = name_link.text.toString()
        name_link.text = underLine(nameLnk)
        unsplash_link.text = underLine(unsplash)
        unsplash_link.setOnClickListener {
            openLink(UNSPLASH_LINK)
        }

        name_link.text = underLine(nameLnk)

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

}