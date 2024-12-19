package com.example.halalrecipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Saved : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedRecipesAdapter
    private val savedRecipesList = mutableListOf<SavedRecipe>()
    private val filteredRecipesList = mutableListOf<SavedRecipe>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)

        recyclerView = view.findViewById(R.id.viewmenu)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = SavedRecipesAdapter(filteredRecipesList) { recipe ->
            navigateToDetail(recipe)
        }
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.queryHint = "Search your recipes"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterRecipes(newText.orEmpty())
                return true
            }
        })

        fetchSavedRecipes()

        return view
    }

    private fun fetchSavedRecipes() {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users")
            .document(userId)
            .collection("bookmarkedRecipes")
            .get()
            .addOnSuccessListener { documents ->
                savedRecipesList.clear()
                filteredRecipesList.clear()

                for (document in documents) {
                    try {
                        val recipe = document.toObject(SavedRecipe::class.java)
                        if (recipe.title.isNotEmpty()) { // Validasi minimum
                            savedRecipesList.add(recipe)
                        }
                    } catch (e: Exception) {
                        Log.e("Saved", "Error parsing recipe: ${e.message}")
                    }
                }
                filteredRecipesList.addAll(savedRecipesList)
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch recipes: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun navigateToDetail(recipe: SavedRecipe) {
        if (recipe.title.isNotEmpty() && recipe.ingredients.isNotEmpty() && recipe.tutorial.isNotEmpty()) {
            val intent = Intent(requireContext(), Detail::class.java).apply {
                putExtra("title", recipe.title)
                putExtra("author", recipe.author)
                putExtra("imageFood", recipe.imageFood)
                putStringArrayListExtra("ingredients", ArrayList(recipe.ingredients))
                putStringArrayListExtra("tutorial", ArrayList(recipe.tutorial))
            }
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), "Incomplete recipe data", Toast.LENGTH_SHORT).show()
        }
    }


    private fun filterRecipes(query: String) {
        val lowerCaseQuery = query.lowercase()
        filteredRecipesList.clear()

        if (query.isEmpty()) {
            filteredRecipesList.addAll(savedRecipesList)
        } else {
            filteredRecipesList.addAll(savedRecipesList.filter { recipe ->
                recipe.title.lowercase().contains(lowerCaseQuery)
            })
        }

        adapter.notifyDataSetChanged()
    }
}

// Data model for saved recipes
data class SavedRecipe(
    val title: String = "",
    val author: String = "",
    val imageFood: String = "",
    val ingredients: List<String> = emptyList(),
    val tutorial: List<String> = emptyList()
)

// Adapter for RecyclerView
class SavedRecipesAdapter(
    private val savedRecipes: List<SavedRecipe>,
    private val onItemClick: (SavedRecipe) -> Unit
) : RecyclerView.Adapter<SavedRecipesAdapter.SavedRecipeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedRecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_menubigger, parent, false)
        return SavedRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedRecipeViewHolder, position: Int) {
        val recipe = savedRecipes[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener { onItemClick(recipe) }
    }

    override fun getItemCount(): Int = savedRecipes.size

    class SavedRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val author = itemView.findViewById<TextView>(R.id.author)
        private val imageFood = itemView.findViewById<ImageView>(R.id.imageFood)

        fun bind(recipe: SavedRecipe) {
            title.text = recipe.title
            author.text = recipe.author
            Glide.with(itemView.context)
                .load(recipe.imageFood)
                .into(imageFood)
        }
    }
}