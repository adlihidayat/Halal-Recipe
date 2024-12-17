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

class MenuAdapter(private val onItemClick: (MenuData) -> Unit) : RecyclerView.Adapter<MenuAdapter.MyViewHolder>() {

    private val menuList = mutableListOf<MenuData>() // List untuk menyimpan data Firestore

    // Fungsi untuk memperbarui data
    fun updateData(newMenuList: List<MenuData>) {
        menuList.clear()
        menuList.addAll(newMenuList)
        notifyDataSetChanged()
    }

    // ViewHolder
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageFood: ShapeableImageView = itemView.findViewById(R.id.imageFood)
        val imageAuthor: ShapeableImageView = itemView.findViewById(R.id.imageAuthor)
        val title: TextView = itemView.findViewById(R.id.title)
        val author: TextView = itemView.findViewById(R.id.author)
    }

    // Fungsi untuk mengambil data dari Firestore
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
                updateData(fetchedList) // Perbarui data dengan hasil yang diambil
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error fetching data", exception)
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_menu, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = menuList[position]

        holder.title.text = currentItem.title ?: "Untitled"
        holder.author.text = currentItem.author ?: "Unknown Author"

        // Gunakan Glide untuk memuat gambar makanan
        Glide.with(holder.itemView.context)
            .load(currentItem.imageFood ?: R.drawable.appetizer) // Default jika null
            .placeholder(R.drawable.appetizer) // Placeholder
            .error(R.drawable.appetizer) // Error
            .into(holder.imageFood)

        // Gunakan Glide untuk memuat gamba r author
        Glide.with(holder.itemView.context)
            .load(currentItem.imageAuthor)
            .placeholder(R.drawable.pfp)
            .error(R.drawable.pfp)
            .into(holder.imageAuthor)

        holder.itemView.setOnClickListener {
            onItemClick(currentItem) // Panggil callback saat item diklik
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}
