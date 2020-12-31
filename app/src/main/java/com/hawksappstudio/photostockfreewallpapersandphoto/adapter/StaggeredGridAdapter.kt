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
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import kotlinx.android.synthetic.main.staggered_item.view.*
import xyz.belvi.blurhash.BlurHash
import xyz.belvi.blurhash.blurPlaceHolder

class StaggeredGridAdapter(private var photoList: ArrayList<Model.Photo>,var context: Context, var selectedPhoto: SelectedPhoto) : RecyclerView.Adapter<StaggeredGridAdapter.StaggeredHolder>() {

    val blurHash : BlurHash = BlurHash(context,lruSize = 20,punch = 1f)
    private val set = ConstraintSet()
    private val differCallback = object : DiffUtil.ItemCallback<Model.Photo>(){
        override fun areItemsTheSame(oldItem: Model.Photo, newItem: Model.Photo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Model.Photo, newItem: Model.Photo): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallback)


    inner class StaggeredHolder(itemview: View): RecyclerView.ViewHolder(itemview) {

        init {
            itemview.setOnClickListener {
                selectedPhoto.selectedPhoto(photoList[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaggeredHolder {
        var view : View = LayoutInflater.from(context).inflate(R.layout.staggered_item,parent,false)
        return StaggeredHolder(view)
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: StaggeredHolder, position: Int) {
        //val photo = differ.currentList[position]
       val photo = photoList[position]
        val string = "Photo by" + photo.user.name + "on Unsplash"
        holder.itemView.apply {
            if(photo.blurHash !=null){
                Glide.with(context).load(photo.urls.thumb).centerCrop().fitCenter().thumbnail(0.3f)
                    .blurPlaceHolder(photo.blurHash,imageStaggeredView,blurHash){
                        it.into(imageStaggeredView)
                    }
            }else{
                Glide.with(context).load(photo.urls.thumb).centerCrop().fitCenter().thumbnail(0.3f)
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
        photoList.addAll(newPhotoList)
        notifyItemRangeInserted(photoList.size,newPhotoList.size)
    }
}