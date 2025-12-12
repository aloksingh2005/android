package com.videoeditor.ui.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.videoeditor.R
import com.videoeditor.databinding.FragmentHomeBinding
import com.videoeditor.viewmodels.VideoViewModel

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: VideoViewModel by viewModels()
    
    // Video picker launcher
    private val videoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.loadVideoFromUri(it) }
    }
    
    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openVideoPicker()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // Upload video button
        binding.btnUploadVideo.setOnClickListener {
            checkPermissionAndPickVideo()
        }
        
        // YouTube URL fetch button
        binding.btnFetchYoutube.setOnClickListener {
            val url = binding.etYoutubeUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                viewModel.loadVideoFromYouTubeUrl(url)
            } else {
                Toast.makeText(requireContext(), "Enter YouTube URL", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun observeViewModel() {
        // Observe video project
        viewModel.videoProject.observe(viewLifecycleOwner) { project ->
            if (project != null) {
                // Video loaded successfully
                binding.layoutVideoInput.visibility = View.GONE
                binding.layoutVideoEditor.visibility = View.VISIBLE
                setupVideoEditor(project)
            } else {
                binding.layoutVideoInput.visibility = View.VISIBLE
                binding.layoutVideoEditor.visibility = View.GONE
            }
        }
        
        // Observe processing state
        viewModel.processingState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is com.videoeditor.data.models.ProcessingState.InProgress -> {
                    showProcessingDialog(state.progress, state.message)
                }
                is com.videoeditor.data.models.ProcessingState.Success -> {
                    hideProcessingDialog()
                    showSuccessDialog(state.outputPath)
                }
                is com.videoeditor.data.models.ProcessingState.Error -> {
                    hideProcessingDialog()
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
                else -> {
                    hideProcessingDialog()
                }
            }
        }
        
        // Observe errors
        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun setupVideoEditor(project: com.videoeditor.data.models.VideoProject) {
        // Setup video player
        binding.videoPlayer.setVideoPath(project.videoPath)
        
        // Setup time selection
        binding.timeSelection.setVideoDuration(project.videoDuration)
        binding.timeSelection.setTimeRange(project.startTime, project.endTime)
        binding.timeSelection.setOnTimeRangeChangedListener { startTime, endTime ->
            viewModel.updateTimeSelection(startTime, endTime)
        }
        
        // Setup aspect ratio selector
        binding.aspectRatioSelector.setSelectedRatio(project.selectedAspectRatio)
        binding.aspectRatioSelector.setOnRatioSelectedListener { ratio ->
            viewModel.updateAspectRatio(ratio)
        }
        
        // Process button
        binding.btnProcess.setOnClickListener {
            viewModel.processVideo()
        }
        
        // Cancel button
        binding.btnCancel.setOnClickListener {
            viewModel.resetProject()
        }
    }
    
    private var processingDialog: MaterialAlertDialogBuilder? = null
    
    private fun showProcessingDialog(progress: Int, message: String) {
        if (processingDialog == null) {
            processingDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Processing Video")
                .setMessage("$message\nProgress: $progress%")
                .setCancelable(false)
                .create() as? MaterialAlertDialogBuilder
        }
        // TODO: Update progress
    }
    
    private fun hideProcessingDialog() {
        processingDialog = null
    }
    
    private fun showSuccessDialog(outputPath: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Success!")
            .setMessage("Video processed successfully!\n\nSaved to: $outputPath")
            .setPositiveButton("Schedule on Instagram") { _, _ ->
                // TODO: Open schedule dialog
            }
            .setNegativeButton("Done") { _, _ ->
                viewModel.resetProject()
            }
            .show()
    }
    
    private fun checkPermissionAndPickVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses READ_MEDIA_VIDEO
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_VIDEO
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openVideoPicker()
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_VIDEO)
                }
            }
        } else {
            // Below Android 13
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openVideoPicker()
                }
                else -> {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }
    
    private fun openVideoPicker() {
        videoPickerLauncher.launch("video/*")
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
