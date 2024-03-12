package com.example.songbook.ui.login

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.songbook.R
import com.example.songbook.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var googleAuthClient: GoogleAuthClient

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                viewLifecycleOwner.lifecycleScope.launch {
                    val uid = googleAuthClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )

                    if (uid != null) {
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hideNavigationElements()

        binding.buttonContinueWithGoogle.setOnClickListener {
            launchSignIn()
        }
    }

    private fun launchSignIn() {
        viewLifecycleOwner.lifecycleScope.launch {
            val signInIntentSender = googleAuthClient.signIn()
            launcher.launch(
                IntentSenderRequest.Builder(
                    signInIntentSender ?: return@launch
                ).build()
            )
        }
    }

    private fun hideNavigationElements() {
        if (isAdded) {
            val activity = this.requireActivity()

            val bottomNavBar = activity.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            bottomNavBar?.visibility = View.GONE

            val topNavBar = activity.findViewById<Toolbar>(R.id.toolbar)
            topNavBar?.visibility = View.GONE
        }
    }

    private fun showNavigationElements() {
        val activity = this.requireActivity()

        val bottomNavBar = activity.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavBar?.visibility = View.VISIBLE

        val topNavBar = activity.findViewById<Toolbar>(R.id.toolbar)
        topNavBar?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showNavigationElements()
    }
}