package com.jploran.cineexplorer.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jploran.cineexplorer.data.remote.model.MovieDto
import com.jploran.cineexplorer.databinding.MovieElementBinding
import com.jploran.cineexplorer.utils.Constants

class MovieAdapter(private val movies: List<MovieDto>, private val onMovieClicked: (MovieDto)->Unit): RecyclerView.Adapter<MovieViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = MovieElementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)

        Glide.with(holder.itemView.context)
            .load(Constants.BASE_IMAGE_URL+movie.posterPath)
            .into(holder.ivThumbnail)

        holder.itemView.setOnClickListener {
            onMovieClicked(movie)
        }
    }
}