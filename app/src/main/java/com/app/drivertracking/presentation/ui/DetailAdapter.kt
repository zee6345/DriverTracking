package com.app.drivertracking.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.drivertracking.data.models.response.success.StopX
import com.app.drivertracking.databinding.ItemDetailsBinding

class DetailAdapter(
    private val driverList: List<StopX>,
    private val onItemClick: (StopX) -> Unit
) :
    RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    class ViewHolder(_binding: ItemDetailsBinding) : RecyclerView.ViewHolder(_binding.root) {

        private val binding: ItemDetailsBinding

        init {
            binding = _binding
        }

        fun bind(driverDetail: StopX, onItemClick: (StopX) -> Unit) {

            binding.tvTime.visibility =
                if (driverDetail.stop_time.isEmpty()) View.GONE else View.VISIBLE


            binding.tvTime.text = driverDetail.stop_time
            binding.tvDriver.text = driverDetail.stop_title
            binding.tvRoute.text = driverDetail.direction

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
        holder.bind(driverList[position]) {
            onItemClick(it)
        }

    }
}