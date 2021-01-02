package com.hawksappstudio.photostockfreewallpapersandphoto.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import kotlinx.android.synthetic.main.staggered_item.view.*
import xyz.belvi.blurhash.BlurHash
import xyz.belvi.blurhash.blurPlaceHolder

class StaggeredTopicPhotoAdapter(var context: Context, var selectedPhoto: SelectedPhoto) : RecyclerView.Adapter<StaggeredTopicPhotoAdapter.StaggeredTopicPhotoHolder>() {

    val blurHash : BlurHash = BlurHash(context,lruSize = 20,punch = 1f)
    private val set = ConstraintSet()

    private val requestOptions = RequestOptions().placeholder(R.drawable.splashscreen)

     var photoList: ArrayList<Model.Photo> = ArrayList()



    private val differCallback = object : DiffUtil.ItemCallback<Model.Photo>(){
        override fun areItemsTheSame(oldItem: Model.Photo, newItem: Model.Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Model.Photo, newItem: Model.Photo): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallback)

    inner class StaggeredTopicPhotoHolder(itemview: View): RecyclerView.ViewHolder(itemview) {

        init {
            itemview.setOnClickListener {
                selectedPhoto.selectedPhoto(photoList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredTopicPhotoHolder{
        var view : View = LayoutInflater.from(context).inflate(R.layout.staggered_item,parent,false)
        return StaggeredTopicPhotoHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: StaggeredTopicPhotoHolder, position: Int) {
        //val photo = differ.currentList[position]
        val photo = photoList[position]
        holder.itemView.apply {
            if(photo.blurHash !=null){
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(photo.urls.thumb)
                    .blurPlaceHolder(photo.blurHash,imageStaggeredView,blurHash){
                        it.into(imageStaggeredView)
                    }
            }else{
                Glide.with(context).setDefaultRequestOptions(requestOptions).load(photo.urls.thumb).centerCrop().fitCenter().thumbnail(0.3f)
                    .into(imageStaggeredView)
            }

            //staggeredText.text = string
        }
        val ratio = String.format("%d:%d",photo.width,photo.height)
        set.clone(holder.itemView.parentConstraint)
        set.setDimensionRatio(holder.itemView.imageStaggeredView.id,ratio)
        set.applyTo(holder.itemView.parentConstraint)

    }

    override fun getItemCount(): Int {
        return photoList.size
    }
    interface SelectedPhoto{
        fun selectedPhoto(image: Model.Photo)
    }

    fun updatePhotoList(newPhotoList:List<Model.Photo>){
        photoList.clear()
        photoList.addAll(newPhotoList)
        //notifyItemRangeInserted(photoList.size,newPhotoList.size)
        notifyDataSetChanged()
    }
    fun addPhotoList(newPhotoList:List<Model.Photo>){
        photoList.addAll(newPhotoList)
        notifyItemRangeInserted(photoList.size,newPhotoList.size)

    }
    //centerCrop().fitCenter().thumbnail(0.3f)
}
