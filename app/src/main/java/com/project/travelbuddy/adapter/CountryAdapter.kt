package com.project.travelbuddy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.travelbuddy.databinding.ItemCountryBinding
import com.project.travelbuddy.db.CountryEntity
import com.squareup.picasso.Picasso

class CountryAdapter : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countries: List<CountryEntity> = emptyList()

    fun submitList(newList: List<CountryEntity>) {
        countries = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val binding = ItemCountryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    override fun getItemCount() = countries.size

    class CountryViewHolder(private val binding: ItemCountryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(country: CountryEntity) {
            binding.tvCountryName.text = country.name
            binding.tvCountryCapital.text = "Capital: ${country.capital}"
            binding.tvCountryRegion.text = "Region: ${country.region}"
            binding.tvCountryPopulation.text = "Population: ${country.population}"

            Picasso.get().load(country.flag).into(binding.ivCountryFlag)
        }
    }
}