package com.kalios.seaftysync

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(
    private val mList: MutableList<Contact>,
    private val itemClickListener: MainActivity
) : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    // Interface for item click events
    interface OnItemClickListener {
        fun onItemClick(contact: Contact)
    }
    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = mList[position]
        holder.bind(contact)
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val itemNumber: TextView = itemView.findViewById(R.id.item_number)
        private lateinit var currentContact: Contact

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(contact: Contact) {
            currentContact = contact
            itemNumber.text = contact.Number
        }

        override fun onClick(view: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val clickedContact = mList[position]
                itemClickListener.onItemClick(clickedContact)
            }
        }
    }

    // Remove item from the list
    fun removeItem(position: Int): Contact? {
        if (position != RecyclerView.NO_POSITION && position < mList.size) {
            val removedContact = mList.removeAt(position)
            notifyItemRemoved(position)
            return removedContact
        }
        return null
    }
}
