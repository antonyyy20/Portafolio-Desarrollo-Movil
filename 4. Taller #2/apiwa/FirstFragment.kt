package com.example.apiwa

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.apiwa.databinding.FragmentFirstBinding
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val binding get() = _binding!!
    private var selectedCountry: CountryOption? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCountryDropdown()
        setupFieldConstraints()

        binding.buttonFirst.setOnClickListener {
            sendWhatsAppMessage()
        }
    }

    private fun setupCountryDropdown() {
        val labels = COUNTRY_OPTIONS.map { it.label }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, labels)
        binding.countrySelectorInput.setAdapter(adapter)
        binding.countrySelectorInput.setOnItemClickListener { _, _, position, _ ->
            selectedCountry = COUNTRY_OPTIONS[position]
            binding.countrySelectorInputLayout.error = null
        }
    }

    private fun setupFieldConstraints() {
        binding.phoneInput.filters = arrayOf(InputFilter.LengthFilter(15))
        binding.messageInput.filters = arrayOf(InputFilter.LengthFilter(500))
    }

    private fun sendWhatsAppMessage() {
        val rawPhone = binding.phoneInput.text?.toString()?.trim().orEmpty()
        val message = binding.messageInput.text?.toString()?.trim().orEmpty()
        val country = selectedCountry

        if (country == null) {
            binding.countrySelectorInputLayout.error = getString(R.string.country_not_selected)
            return
        }

        if (rawPhone.isBlank() || message.isBlank()) {
            Toast.makeText(requireContext(), getString(R.string.fill_required_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val phoneNumber = rawPhone.filter { it.isDigit() }
        if (phoneNumber.length < 6) {
            Toast.makeText(requireContext(), getString(R.string.invalid_phone), Toast.LENGTH_SHORT).show()
            return
        }
        if (country.isoCode == "PA" && phoneNumber.length > 8) {
            Toast.makeText(requireContext(), getString(R.string.invalid_panama_phone), Toast.LENGTH_SHORT).show()
            return
        }

        if (message.length < 2) {
            Toast.makeText(requireContext(), getString(R.string.invalid_message), Toast.LENGTH_SHORT).show()
            return
        }

        val phone = "${country.dialCode}$phoneNumber"
        val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
        val uri = Uri.parse("https://api.whatsapp.com/send?phone=$phone&text=$encodedMessage")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.whatsapp")
        }

        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(requireContext(), getString(R.string.whatsapp_not_installed), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private data class CountryOption(
        val name: String,
        val isoCode: String,
        val dialCode: String
    ) {
        val label: String
            get() = "${isoToFlag(isoCode)} $name (+$dialCode)"
    }

    private companion object {
        val COUNTRY_OPTIONS = listOf(
            CountryOption("Argentina", "AR", "54"),
            CountryOption("Bolivia", "BO", "591"),
            CountryOption("Brasil", "BR", "55"),
            CountryOption("Chile", "CL", "56"),
            CountryOption("Colombia", "CO", "57"),
            CountryOption("Costa Rica", "CR", "506"),
            CountryOption("Ecuador", "EC", "593"),
            CountryOption("El Salvador", "SV", "503"),
            CountryOption("Espana", "ES", "34"),
            CountryOption("Estados Unidos", "US", "1"),
            CountryOption("Guatemala", "GT", "502"),
            CountryOption("Honduras", "HN", "504"),
            CountryOption("Mexico", "MX", "52"),
            CountryOption("Nicaragua", "NI", "505"),
            CountryOption("Panama", "PA", "507"),
            CountryOption("Paraguay", "PY", "595"),
            CountryOption("Peru", "PE", "51"),
            CountryOption("Republica Dominicana", "DO", "1"),
            CountryOption("Uruguay", "UY", "598"),
            CountryOption("Venezuela", "VE", "58")
        )

        private fun isoToFlag(isoCode: String): String {
            val base = 0x1F1E6
            val first = base + (isoCode[0].uppercaseChar().code - 'A'.code)
            val second = base + (isoCode[1].uppercaseChar().code - 'A'.code)
            return String(Character.toChars(first)) + String(Character.toChars(second))
        }
    }
}