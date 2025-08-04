package com.youtubesmm.app.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.settingsState.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnSave.setOnClickListener {
                saveSettings()
            }

            btnClearCache.setOnClickListener {
                viewModel.clearCache()
            }

            btnAbout.setOnClickListener {
                viewModel.showAbout()
            }
        }
    }

    private fun saveSettings() {
        val ipRotation = binding.switchIpRotation.isChecked
        val delayBetweenAccounts = binding.etDelayBetween.text?.toString()?.toIntOrNull() ?: 30
        val taskDelay = binding.etTaskDelay.text?.toString()?.toIntOrNull() ?: 60
        val userAgentRotation = binding.switchUserAgentRotation.isChecked

        viewModel.saveSettings(
            ipRotation = ipRotation,
            delayBetweenAccounts = delayBetweenAccounts,
            taskDelay = taskDelay,
            userAgentRotation = userAgentRotation
        )
    }

    private fun updateUI(state: SettingsState) {
        binding.apply {
            // Settings values
            switchIpRotation.isChecked = state.ipRotation
            etDelayBetween.setText(state.delayBetweenAccounts.toString())
            etTaskDelay.setText(state.taskDelay.toString())
            switchUserAgentRotation.isChecked = state.userAgentRotation

            // Status
            tvStatus.text = state.status
            tvStatus.setTextColor(state.statusColor)

            // Loading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Buttons
            btnSave.isEnabled = !state.isLoading
            btnClearCache.isEnabled = !state.isLoading
            btnAbout.isEnabled = !state.isLoading
        }

        if (state.message.isNotEmpty()) {
            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}