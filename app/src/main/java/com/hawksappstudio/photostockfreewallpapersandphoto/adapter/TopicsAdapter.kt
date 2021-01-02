package com.hawksappstudio.photostockfreewallpapersandphoto.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hawksappstudio.photostockfreewallpapersandphoto.R
import com.hawksappstudio.photostockfreewallpapersandphoto.model.Model
import com.hawksappstudio.photostockfreewallpapersandphoto.utils.VerticalTextView

class TopicsAdapter(private var topicList: ArrayList<Model.Topic>,var selectedTopic: SelectedTopic) : RecyclerView.Adapter<TopicsAdapter.TopicHolder>() {

    private var isSelected = false
    var selectedPosition  = -1
    var lastSelectedItemPos = -1
    private var selectedSlug:String? = null

    inner class TopicHolder(itemview:View) : RecyclerView.ViewHolder(itemview) {
            var cardBg : CardView = itemview.findViewById(R.id.cardBg)
        init {
            itemview.setOnClickListener {
                selectedPosition = adapterPosition
                selectedTopic.selectedTopic(topicList[selectedPosition])
                if (lastSelectedItemPos == -1) {
                    lastSelectedItemPos = selectedPosition
                }else{
                    notifyItemChanged(lastSelectedItemPos)
                    lastSelectedItemPos = selectedPosition
                }
                notifyItemChanged(selectedPosition)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicHolder {
        var view : View = LayoutInflater.from(parent.context).inflate(R.layout.topics_item,parent,false)
        return TopicHolder(view)
    }

    override fun onBindViewHolder(holder: TopicHolder, position: Int) {
        var topicText = holder.itemView.findViewById<VerticalTextView>(R.id.vertical_topics_text)

        topicText.text = topicList[position].title


        if (position == selectedPosition){
            holder.cardBg.setCardBackgroundColor(holder.itemView.resources.getColor(R.color.white))
            isSelected = false
            selectedSlug = topicList[selectedPosition].slug
            Log.d("selectedSlug", "onBindViewHolder: $selectedSlug")
        }else{
            holder.cardBg.setCardBackgroundColor(holder.itemView.resources.getColor(R.color.bg_btn))
        }
    }

    override fun getItemCount(): Int {
        return  topicList.size
    }
    interface SelectedTopic{
        fun selectedTopic(topic: Model.Topic)
    }

    fun updateTopicsList(newTopicsList:List<Model.Topic>){
        topicList.clear()
        topicList.addAll(newTopicsList)
        notifyDataSetChanged()
    }

    fun setSelectedTopic()  {
        selectedPosition = -1
        //selectedTopic.selectedTopic(topicList[selectedPosition])
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}