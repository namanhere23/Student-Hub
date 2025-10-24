package com.namangulati.studenthub.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.namangulati.studenthub.databinding.ContactsRecyclerBinding
import com.namangulati.studenthub.models.ContactsModel
import com.namangulati.studenthub.models.UserDetailsModel
import com.namangulati.studenthub.userPages.MessagePage

class ChatsAdapter (
    val context: Context,
    private val contacts: ArrayList<ContactsModel>,
    private val currentUser: UserDetailsModel
) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ContactsRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ContactsRecyclerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val contact = contacts[position]
        holder.binding.ContactName.text = contact.name
        holder.binding.ContactEmail.text = contact.email

        holder.binding.root.setOnClickListener {
            val intent= Intent(context, MessagePage::class.java)
            intent.putExtra("contact", contact.name)
            intent.putExtra("uid",contact.uid)
            intent.putExtra("EXTRA_USER_DETAILS", currentUser)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = contacts.size

    fun updateList(newList: List<ContactsModel>){
        contacts.clear()
        contacts.addAll(newList)
        notifyDataSetChanged()
    }
}