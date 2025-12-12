package com.videoeditor.ui.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.videoeditor.R
import com.videoeditor.data.models.AspectRatio
import com.videoeditor.databinding.ItemAspectRatioBinding
import com.videoeditor.databinding.ViewAspectRatioSelectorBinding

class AspectRatioSelectorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    private val binding: ViewAspectRatioSelectorBinding
    private var selectedRatio: AspectRatio = AspectRatio.VERTICAL_9_16
    private var onRatioSelected: ((AspectRatio) -> Unit)? = null
    
    init {
        binding = ViewAspectRatioSelectorBinding.inflate(LayoutInflater.from(context), this, true)
        orientation = VERTICAL
        setupRecyclerView()
    }
    
    private fun setupRecyclerView() {
        val adapter = AspectRatioAdapter(AspectRatio.values().toList()) { ratio ->
            selectedRatio = ratio
            onRatioSelected?.invoke(ratio)
        }
        
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            this.adapter = adapter
        }
    }
    
    fun setSelectedRatio(ratio: AspectRatio) {
        selectedRatio = ratio
    }
    
    fun setOnRatioSelectedListener(listener: (AspectRatio) -> Unit) {
        onRatioSelected = listener
    }
    
    private inner class AspectRatioAdapter(
        private val ratios: List<AspectRatio>,
        private val onItemClick: (AspectRatio) -> Unit
    ) : RecyclerView.Adapter<AspectRatioAdapter.ViewHolder>() {
        
        inner class ViewHolder(val binding: ItemAspectRatioBinding) : RecyclerView.ViewHolder(binding.root)
        
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemAspectRatioBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }
        
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val ratio = ratios[position]
            
            holder.binding.apply {
                tvRatio.text = ratio.ratio
                tvDescription.text = ratio.description
                
                // Visual representation of aspect ratio
                val aspectRatioFloat = ratio.width.toFloat() / ratio.height.toFloat()
                val maxSize = 80 // dp
                
                if (aspectRatioFloat > 1) {
                    // Landscape
                    viewRatioPreview.layoutParams.width = maxSize
                    viewRatioPreview.layoutParams.height = (maxSize / aspectRatioFloat).toInt()
                } else {
                    // Portrait or square
                    viewRatioPreview.layoutParams.height = maxSize
                    viewRatioPreview.layoutParams.width = (maxSize * aspectRatioFloat).toInt()
                }
                
                // Highlight if selected
                cardView.isChecked = ratio == selectedRatio
                
                root.setOnClickListener {
                    val oldSelected = selectedRatio
                    onItemClick(ratio)
                    // Refresh both items
                    notifyItemChanged(ratios.indexOf(oldSelected))
                    notifyItemChanged(position)
                }
            }
        }
        
        override fun getItemCount() = ratios.size
    }
}
