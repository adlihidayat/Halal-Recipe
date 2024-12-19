package com.example.halalrecipe

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class ReviewAdapter(private val context: Context, private val reviewDataList: List<ReviewData>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    // ViewHolder untuk menghubungkan tampilan dengan data
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.img_profile)
        val name: TextView = itemView.findViewById(R.id.name)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val postDate: TextView = itemView.findViewById(R.id.post_date)
        val reviewDesc: TextView = itemView.findViewById(R.id.review_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.reviews_card, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviewDataList[position]
        holder.name.text = review.name
        holder.postDate.text = review.date
        holder.ratingBar.rating = review.rating
        holder.reviewDesc.text = review.desc

        // Load gambar profil menggunakan Glide
        if (review.profile!!.isNotEmpty()) {
            Glide.with(holder.itemView.context).load(review.profile).into(holder.imgProfile)
        } else {
            holder.imgProfile.setImageResource(R.drawable.pfp) // Gambar default
        }
    }

    override fun getItemCount(): Int = reviewDataList.size

    // Fungsi untuk mengambil data dari Firestore
    fun fetchData(collection: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection(collection)
            .get()
            .addOnSuccessListener { result ->
                val newReviewList = result.map { document ->
                    document.toObject(ReviewData::class.java).copy(id = document.id)
                }
                updateData(newReviewList) // Fungsi untuk memperbarui data RecyclerView
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching reviews: ", exception)
            }
    }


    private fun updateData(newList: List<ReviewData>) {
        (reviewDataList as MutableList).clear()
        (reviewDataList as MutableList).addAll(newList)
        notifyDataSetChanged()
    }
}
