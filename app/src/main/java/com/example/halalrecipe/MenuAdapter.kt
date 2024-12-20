package com.example.halalrecipe

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.FirebaseFirestore

class MenuAdapter(
    private val onItemClick: (MenuData) -> Unit,
    private val useBiggerLayout: Boolean = false // Default value to false
) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private val originalList = mutableListOf<MenuData>() // Original list to store Firestore data
    private val filteredList = mutableListOf<MenuData>() // Filtered list for SearchView

    // Function to update data
    private fun updateFilteredList(newList: List<MenuData>) {
        filteredList.clear()
        filteredList.addAll(newList)
        notifyDataSetChanged()
    }

    // Function to update data from Firestore
    fun updateData(newMenuList: List<MenuData>) {
        originalList.clear()
        originalList.addAll(newMenuList)
        updateFilteredList(originalList)
    }

    // ViewHolder
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageFood: ShapeableImageView = itemView.findViewById(R.id.imageFood)
        val imageAuthor: ShapeableImageView = itemView.findViewById(R.id.imageAuthor)
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
    }

    // Function to fetch data from Firestore
    fun fetchData(collection: String, category: String? = null) {
        val db = FirebaseFirestore.getInstance()
        val query = if (category != null) {
            db.collection(collection).whereEqualTo("category", category)
        } else {
            db.collection(collection)
        }

        query.get()
            .addOnSuccessListener { result ->
                val fetchedList = mutableListOf<MenuData>()
                for (document in result) {
                    val menu = document.toObject(MenuData::class.java).copy(id = document.id)
                    fetchedList.add(menu)
                }
                updateData(fetchedList) // Update data with the fetched results
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching data", exception)
            }
    }

    // Function to filter data based on query
    fun filterData(query: String) {
        val filtered = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.title?.contains(query, ignoreCase = true) ?: false
            }
        }
        updateFilteredList(filtered)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Choose layout based on useBiggerLayout flag
        val layoutRes = if (useBiggerLayout) {
            R.layout.viewholder_menubigger // Use bigger layout
        } else {
            R.layout.viewholder_menu // Use regular layout
        }

        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = filteredList[position]

        holder.title.text = currentItem.title ?: "Untitled"
        holder.author.text = currentItem.author ?: "Unknown Author"

        // Load food image using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.imageFood ?: R.drawable.appetizer) // Default if null
            .placeholder(R.drawable.appetizer) // Placeholder
            .error(R.drawable.appetizer) // Error
            .into(holder.imageFood)

        // Load author image using Glide
        Glide.with(holder.itemView.context)
            .load(currentItem.imageAuthor)
            .placeholder(R.drawable.pfp)
            .error(R.drawable.pfp)
            .into(holder.imageAuthor)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem) // Call the callback when an item is clicked
        }
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }
}
