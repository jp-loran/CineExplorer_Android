package com.jploran.cineexplorer.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.jploran.cineexplorer.data.remote.model.FavoriteMovieDto
import com.jploran.cineexplorer.databinding.FavoriteMovieElementBinding

class FavoriteMovieViewHolder(private var binding: FavoriteMovieElementBinding) : RecyclerView.ViewHolder(binding.root){
    val ivThumbnail = binding.ivThumbnail
    fun bind(movie: FavoriteMovieDto){
        val formattedVoteAverage = String.format("%.1f", movie.voteAverage)

        binding.tvTitle.text = movie.title
        binding.tvVoteAverage.text = "$formattedVoteAverage/10"
        binding.tvLanguage.text = "LANG: ${movie.language?.uppercase()}"
        binding.tvAdults.text = if (movie.adult.toBoolean()) "ADULTS: YES" else "ADULTS: NO"
    }

}