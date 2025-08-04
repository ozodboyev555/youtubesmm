package com.youtubesmm.app.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.youtubesmm.app.data.model.ServiceType
import com.youtubesmm.app.databinding.FragmentOrderBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        setupObservers()
        setupClickListeners()
    }

    private fun setupSpinner() {
        val services = ServiceType.values().map { it.displayName }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, services)
        binding.spinnerService.setAdapter(adapter)
        binding.spinnerService.setText(services.firstOrNull() ?: "", false)
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.orderState.collectLatest { state ->
                updateUI(state)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnSubmit.setOnClickListener {
            submitOrder()
        }
    }

    private fun submitOrder() {
        val url = binding.etUrl.text?.toString()?.trim() ?: ""
        val quantityText = binding.etQuantity.text?.toString()?.trim() ?: ""
        val comment = binding.etComment.text?.toString()?.trim() ?: ""

        if (url.isEmpty()) {
            binding.etUrl.error = "URL kiriting"
            return
        }

        if (quantityText.isEmpty()) {
            binding.etQuantity.error = "Miqdorni kiriting"
            return
        }

        val quantity = quantityText.toIntOrNull()
        if (quantity == null || quantity <= 0) {
            binding.etQuantity.error = "To'g'ri miqdor kiriting"
            return
        }

        val selectedText = binding.spinnerService.text.toString()
        val serviceType = ServiceType.values().find { it.displayName == selectedText } ?: ServiceType.VIEWS

        viewModel.submitOrder(url, quantity, serviceType, comment.takeIf { it.isNotEmpty() })
    }

    private fun updateUI(state: OrderState) {
        binding.apply {
            btnSubmit.isEnabled = !state.isLoading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        }

        if (state.isSuccess) {
            Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
            clearForm()
            viewModel.resetState()
        }
    }

    private fun clearForm() {
        binding.apply {
            etUrl.text?.clear()
            etQuantity.text?.clear()
            etComment.text?.clear()
            val services = ServiceType.values().map { it.displayName }
            spinnerService.setText(services.firstOrNull() ?: "", false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}