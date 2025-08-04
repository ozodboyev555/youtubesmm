package com.youtubesmm.app.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    
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
        
        setupObservers()
        setupClickListeners()
    }
    
    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeState.collectLatest { state ->
                updateUI(state)
            }
        }
    }
    
    private fun setupClickListeners() {
        binding.apply {
            // YouTube ichida kezish uchun WebView ochish
            btnOpenYouTube.setOnClickListener {
                // WebView activity yoki dialog ochish
            }
            
            // Tezkor buyurtma
            btnQuickOrder.setOnClickListener {
                // Buyurtma sahifasiga o'tish
            }
            
            // Ishni boshlash
            btnStartWork.setOnClickListener {
                (activity as? MainActivity)?.startYouTubeService()
            }
            
            // Ishni to'xtatish
            btnStopWork.setOnClickListener {
                (activity as? MainActivity)?.stopYouTubeService()
            }
        }
    }
    
    private fun updateUI(state: HomeState) {
        binding.apply {
            // Status
            tvStatus.text = state.status
            tvStatus.setTextColor(state.statusColor)
            
            // Statistika
            tvActiveOrders.text = state.activeOrders.toString()
            tvCompletedToday.text = state.completedToday.toString()
            tvTotalAccounts.text = state.totalAccounts.toString()
            tvAvailableAccounts.text = state.availableAccounts.toString()
            
            // Progress
            progressBar.progress = state.progressPercentage.toInt()
            tvProgress.text = "${state.progressPercentage.toInt()}%"
            
            // Ish holati
            btnStartWork.isEnabled = !state.isWorking
            btnStopWork.isEnabled = state.isWorking
            
            // Loading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}