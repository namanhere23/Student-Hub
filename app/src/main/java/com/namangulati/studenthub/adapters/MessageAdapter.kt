package com.namangulati.studenthub.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.firebase.auth.FirebaseAuth
import com.namangulati.studenthub.R
import com.namangulati.studenthub.models.Message

class MessageAdapter(val context: Context,val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE=1
    val ITEM_SENT=2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if(viewType==1){
            val view:View= LayoutInflater.from(context).inflate(R.layout.receive,parent,false)
            return ReceiveViewHolder(view)
        } else{
            val view:View= LayoutInflater.from(context).inflate(R.layout.sent,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage=messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage=messageList[position]
        if(holder is SentViewHolder) {
            holder.sentMessage.text = currentMessage.message
        } else if(holder is ReceiveViewHolder) {
            holder.receiveMessage.text = currentMessage.message
        }
    }

    class SentViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val sentMessage=itemView.findViewById<TextView>(R.id.SentMessage)

    }

    class ReceiveViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val receiveMessage=itemView.findViewById<TextView>(R.id.ReceiveMessage)
    }


}