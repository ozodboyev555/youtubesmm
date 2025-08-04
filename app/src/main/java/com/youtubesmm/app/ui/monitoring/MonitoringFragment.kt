package com.youtubesmm.app.ui.monitoring

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.databinding.FragmentMonitoringBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MonitoringFragment : Fragment() {

    private var _binding: FragmentMonitoringBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MonitoringViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonitoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.monitoringState.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnRefresh.setOnClickListener {
                viewModel.refreshData()
            }

            btnClearHistory.setOnClickListener {
                viewModel.clearHistory()
            }
        }
    }

    private fun updateUI(state: MonitoringState) {
        binding.apply {
            // Progress
            progressBar.progress = state.progressPercentage.toInt()
            tvProgress.text = "${state.progressPercentage.toInt()}%"

            // Statistika
            tvTotalTasks.text = state.totalTasks.toString()
            tvCompletedTasks.text = state.completedTasks.toString()
            tvFailedTasks.text = state.failedTasks.toString()
            tvRemainingTasks.text = state.remainingTasks.toString()

            // Vaqt
            tvETA.text = state.estimatedTimeRemaining ?: "Noma'lum"
            tvStartTime.text = state.startTime ?: "Boshlanmagan"
            tvEndTime.text = state.endTime ?: "Tugallanmagan"

            // Status
            // tvStatus.text = state.status
            // tvStatus.setTextColor(state.statusColor)

            // Loading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}