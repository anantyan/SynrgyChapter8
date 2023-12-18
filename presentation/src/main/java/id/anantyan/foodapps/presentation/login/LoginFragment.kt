package id.anantyan.foodapps.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import id.anantyan.foodapps.common.UIState
import id.anantyan.foodapps.common.emailValid
import id.anantyan.foodapps.common.passwordValid
import id.anantyan.foodapps.domain.model.UserModel
import id.anantyan.foodapps.presentation.NavGraphMainDirections
import id.anantyan.foodapps.presentation.databinding.FragmentLoginBinding
import io.github.anderscheow.validator.Validator
import io.github.anderscheow.validator.constant.Mode
import io.github.anderscheow.validator.validator
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModels()

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
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback())
        bindObserver()
        bindView()
    }

    private fun bindObserver() {
        viewModel.getTheme().onEach {
            binding.btnTheme.isChecked = it
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)

        viewModel.login.onEach { state ->
            if (state is UIState.Success) {
                val destination = NavGraphMainDirections.actionRootToHomeFragment()
                findNavController().navigate(destination)
            }
            if (state is UIState.Error) {
                Toast.makeText(requireContext(), getString(state.message!!), Toast.LENGTH_LONG).show()
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    private fun bindView() {
        binding.btnLogin.setOnClickListener {
            onValidation()
        }

        binding.btnRegister.setOnClickListener {
            val destination = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(destination)
        }

        binding.btnTheme.setOnCheckedChangeListener { _, b -> viewModel.setTheme(b) }
    }

    private fun onValidation() {
        validator(requireContext()) {
            mode = Mode.SINGLE
            listener = onValidationListener()
            validate(
                binding.txtInputEmail.emailValid(),
                binding.txtInputPassword.passwordValid()
            )
        }
    }

    private fun onValidationListener() = object : Validator.OnValidateListener {
        override fun onValidateFailed(errors: List<String>) { }

        override fun onValidateSuccess(values: List<String>) {
            val result = UserModel(
                email = binding.txtEmail.text.toString(),
                password = binding.txtPassword.text.toString()
            )
            viewModel.login(result)
        }
    }

    private fun onBackPressedCallback() = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
