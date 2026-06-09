package com.example.gamevault.home.account

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.gamevault.databinding.FragmentAccountBinding
import com.example.gamevault.onboarding.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        loadUserData()
        setupClickListeners()
        return binding.root
    }

    private fun loadUserData() {
        val user = auth.currentUser ?: return
        binding.tvUserEmail.text = user.email ?: ""

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val doc = db.collection("users").document(user.uid).get().await()
                val firstName = doc.getString("firstName") ?: ""
                val lastName = doc.getString("lastName") ?: ""
                val userName = doc.getString("userName") ?: ""
                val phone = doc.getString("phone") ?: ""
                val birthDate = doc.getString("birthDate") ?: ""

                binding.tvUserName.text = "$firstName $lastName"
                binding.tvFullName.text = "$firstName $lastName"
                binding.tvUsername.text = "@$userName"
                binding.tvPhone.text = phone
                binding.tvBirthDate.text = birthDate
            } catch (e: Exception) {
                binding.tvUserName.text = user.email ?: "Gamer"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCerrarSesion.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres salir?")
                .setPositiveButton("Salir") { _, _ ->
                    auth.signOut()
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}