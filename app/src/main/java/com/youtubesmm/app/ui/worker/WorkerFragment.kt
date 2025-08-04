package com.youtubesmm.app.ui.worker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.databinding.FragmentWorkerBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WorkerFragment : Fragment() {

    private var _binding: FragmentWorkerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.workerState.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnStart.setOnClickListener {
                viewModel.startWork()
            }

            btnStop.setOnClickListener {
                viewModel.stopWork()
            }

            btnPause.setOnClickListener {
                viewModel.pauseWork()
            }

            btnResume.setOnClickListener {
                viewModel.resumeWork()
            }
        }
    }

    private fun updateUI(state: WorkerState) {
        binding.apply {
            // Ish holati
            btnStart.isEnabled = !state.isWorking
            btnStop.isEnabled = state.isWorking
            btnPause.isEnabled = state.isWorking && !state.isPaused
            btnResume.isEnabled = state.isWorking && state.isPaused

            // Progress
            progressBar.progress = state.progressPercentage.toInt()
            tvProgress.text = "${state.progressPercentage.toInt()}%"

            // Joriy vazifa
            tvCurrentTask.text = state.currentTask ?: "Vazifa yo'q"
            tvCompletedTasks.text = "Tugallangan: ${state.completedTasks}"
            tvRemainingTasks.text = "Qoldi: ${state.remainingTasks}"

            // Status
            tvStatus.text = state.status
            tvStatus.setTextColor(state.statusColor)

            // Loading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}