package com.hawksappstudio.photostockfreewallpapersandphoto.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.VerticalTextView

class TopicsAdapter(private var topicList: ArrayList<Model.Topic>) : RecyclerView.Adapter<TopicsAdapter.TopicHolder>() {

    class TopicHolder(itemview:View) : RecyclerView.ViewHolder(itemview) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.topics_item,parent,false)
        return TopicHolder(view)
    }

    override fun onBindViewHolder(holder: TopicHolder, position: Int) {
        var topicText = holder.itemView.findViewById<VerticalTextView>(R.id.vertical_topics_text)

        topicText.text = topicList[position].title
    }

    override fun getItemCount(): Int {
        return  topicList.size
    }

    fun updateTopicsList(newTopicsList:List<Model.Topic>){
        topicList.clear()
        topicList.addAll(newTopicsList)
        notifyDataSetChanged()
    }
}