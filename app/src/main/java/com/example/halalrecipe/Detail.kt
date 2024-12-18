package com.example.halalrecipe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide

class Detail : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail, container, false)

        // Ambil data yang dikirim melalui arguments
        val title = arguments?.getString("title")
        val author = arguments?.getString("author")
        val imageFood = arguments?.getString("imageFood")
        val ingredients = arguments?.getStringArrayList("ingredients")
        val caramasak = arguments?.getStringArrayList("tutorial")


        // Tampilkan data ke dalam view
        view.findViewById<TextView>(R.id.title).text = title
        view.findViewById<TextView>(R.id.author).text = author

        view.findViewById<TextView>(R.id.ingredients).text =
            ingredients?.mapIndexed { index, item -> "${index + 1}. $item" }?.joinToString("\n")

        view.findViewById<TextView>(R.id.tutorial).text =
            caramasak?.mapIndexed { index, item -> "${index + 1}. $item" }?.joinToString("\n")

        val imageFoodView = view.findViewById<ImageView>(R.id.imageFood)
        Glide.with(this).load(imageFood).into(imageFoodView)

        // Setup Back Button
        val backButton = view.findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            // Kembali ke fragment sebelumnya
            parentFragmentManager.popBackStack()
        }

        return view
    }
}
