package com.app.drivertracking.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.drivertracking.databinding.ItemDetailsBinding
import com.app.drivertracking.presentation.model.DriverDetail

class DetailAdapter(
    private val driverList: List<DriverDetail>,
    private val onItemClick: (DriverDetail) -> Unit
) :
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    class ViewHolder(_binding: ItemDetailsBinding) : RecyclerView.ViewHolder(_binding.root) {

        private val binding: ItemDetailsBinding

        init {
            binding = _binding
        }

        fun bind(driverDetail: DriverDetail, onItemClick: (DriverDetail) -> Unit) {
            binding.tvTime.text = driverDetail.time
            binding.tvDriver.text = driverDetail.driver
            binding.tvRoute.text = driverDetail.route

            binding.root.setOnClickListener {
                onItemClick(driverDetail)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemDetailsBinding.inflate(inflater, parent, false))
    }

    override fun getItemCount(): Int {
        return driverList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(driverList[position]){
            onItemClick(it)
        }

    }
}