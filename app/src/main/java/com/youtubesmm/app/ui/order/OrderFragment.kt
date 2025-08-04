package com.youtubesmm.app.ui.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
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
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, services)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerService.adapter = adapter
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
            val url = binding.etUrl.text.toString().trim()
            val quantityStr = binding.etQuantity.text.toString().trim()
            val serviceIndex = binding.spinnerService.selectedItemPosition
            val customComment = binding.etComment.text.toString().trim()
            
            if (url.isEmpty()) {
                binding.etUrl.error = "URL kiriting"
                return@setOnClickListener
            }
            
            if (quantityStr.isEmpty()) {
                binding.etQuantity.error = "Miqdorni kiriting"
                return@setOnClickListener
            }
            
            val quantity = quantityStr.toIntOrNull()
            if (quantity == null || quantity <= 0) {
                binding.etQuantity.error = "To'g'ri miqdorni kiriting"
                return@setOnClickListener
            }
            
            val serviceType = ServiceType.values()[serviceIndex]
            viewModel.createOrder(url, quantity, serviceType, customComment)
        }
    }
    
    private fun updateUI(state: OrderState) {
        binding.apply {
            btnSubmit.isEnabled = !state.isLoading
            progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
            
            if (state.isSuccess) {
                Snackbar.make(root, "Buyurtma muvaffaqiyatli yaratildi!", Snackbar.LENGTH_LONG).show()
                clearForm()
            }
            
            if (state.error != null) {
                Snackbar.make(root, "Xatolik: ${state.error}", Snackbar.LENGTH_LONG).show()
            }
        }
    }
    
    private fun clearForm() {
        binding.apply {
            etUrl.text?.clear()
            etQuantity.text?.clear()
            etComment.text?.clear()
            spinnerService.setSelection(0)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}