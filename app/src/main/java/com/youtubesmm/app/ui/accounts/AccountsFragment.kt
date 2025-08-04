package com.youtubesmm.app.ui.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.databinding.FragmentAccountsBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AccountsFragment : Fragment() {

    private var _binding: FragmentAccountsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.accountsState.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun setupClickListeners() {
        binding.apply {
            btnImport.setOnClickListener {
                viewModel.importAccounts()
            }

            btnExport.setOnClickListener {
                viewModel.exportAccounts()
            }

            btnClear.setOnClickListener {
                viewModel.clearAccounts()
            }
        }
    }

    private fun updateUI(state: AccountsState) {
        binding.apply {
            // Statistika
            tvTotalAccounts.text = "Jami: ${state.totalAccounts}"
            tvActiveAccounts.text = "Faol: ${state.activeAccounts}"
            tvBlockedAccounts.text = "Bloklangan: ${state.blockedAccounts}"

            // Status
            tvStatus.text = state.status
            tvStatus.setTextColor(state.statusColor)

            // Loading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

            // Buttons
            btnImport.isEnabled = !state.isLoading
            btnExport.isEnabled = !state.isLoading && state.totalAccounts > 0
            btnClear.isEnabled = !state.isLoading && state.totalAccounts > 0
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