package com.jploran.cineexplorer.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.jploran.cineexplorer.data.remote.model.MovieDto
import com.jploran.cineexplorer.databinding.MovieElementBinding

class MovieViewHolder(private var binding: MovieElementBinding): RecyclerView.ViewHolder(binding.root) {

    val ivThumbnail = binding.ivThumbnail
    fun bind(movie: MovieDto){
        val formattedVoteAverage = String.format("%.1f", movie.voteAverage)

        binding.tvTitle.text = movie.title
        binding.tvVoteAverage.text = "$formattedVoteAverage/10"
    }

}