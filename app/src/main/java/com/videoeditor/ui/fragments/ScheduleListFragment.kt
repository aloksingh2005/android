package com.videoeditor.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.videoeditor.databinding.FragmentScheduleListBinding

class ScheduleListFragment : Fragment() {
    
    private var _binding: FragmentScheduleListBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Show empty state (no schedules yet)
        binding.layoutEmptyState.visibility = android.view.View.VISIBLE
        binding.rvSchedules.visibility = android.view.View.GONE
        
        // FAB click to add schedule
        binding.fabAddSchedule.setOnClickListener {
            android.widget.Toast.makeText(
                requireContext(),
                "Schedule feature coming soon!",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
