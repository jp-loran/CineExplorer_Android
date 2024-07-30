package com.jploran.cineexplorer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jploran.cineexplorer.data.remote.model.FavoriteMovieDto
import com.jploran.cineexplorer.databinding.FavoriteMovieElementBinding
import com.jploran.cineexplorer.utils.Constants

class FavoriteMovieAdapter(
    private var movies: MutableList<FavoriteMovieDto>,
    private val onMovieClicked: (FavoriteMovieDto) -> Unit
) : RecyclerView.Adapter<FavoriteMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteMovieViewHolder {
        val binding = FavoriteMovieElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteMovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: FavoriteMovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)

        Glide.with(holder.itemView.context)
            .load(Constants.BASE_IMAGE_URL + movie.posterPath)
            .into(holder.ivThumbnail)

        holder.itemView.setOnClickListener {
            onMovieClicked(movie)
        }
    }

    fun updateMovies(newMovies: List<FavoriteMovieDto>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }
}

