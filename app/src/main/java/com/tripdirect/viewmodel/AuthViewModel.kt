package com.tripdirect.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tripdirect.data.UserPreferencesManager
import com.tripdirect.data.model.User
import com.tripdirect.data.repository.AuthRepository
import com.tripdirect.data.repository.DataStoreAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private var authRepository: AuthRepository? = null

    fun getRepository(context: Context): AuthRepository {
        return authRepository ?: synchronized(this) {
            val repo = DataStoreAuthRepository(UserPreferencesManager.getInstance(context))
            authRepository = repo
            repo
        }
    }

    // Login State
    private val _loginEmail = MutableStateFlow("")
    val loginEmail: StateFlow<String> = _loginEmail.asStateFlow()

    private val _loginPassword = MutableStateFlow("")
    val loginPassword: StateFlow<String> = _loginPassword.asStateFlow()

    private val _isLoginLoading = MutableStateFlow(false)
    val isLoginLoading: StateFlow<Boolean> = _isLoginLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    // Register State
    private val _registerFullName = MutableStateFlow("")
    val registerFullName: StateFlow<String> = _registerFullName.asStateFlow()

    private val _registerEmail = MutableStateFlow("")
    val registerEmail: StateFlow<String> = _registerEmail.asStateFlow()

    private val _registerPassword = MutableStateFlow("")
    val registerPassword: StateFlow<String> = _registerPassword.asStateFlow()

    private val _registerConfirmPassword = MutableStateFlow("")
    val registerConfirmPassword: StateFlow<String> = _registerConfirmPassword.asStateFlow()

    private val _isRegisterLoading = MutableStateFlow(false)
    val isRegisterLoading: StateFlow<Boolean> = _isRegisterLoading.asStateFlow()

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError.asStateFlow()

    private val _registerSuccess = MutableStateFlow(false)
    val registerSuccess: StateFlow<Boolean> = _registerSuccess.asStateFlow()

    // Forgot Password State
    private val _forgotPasswordEmail = MutableStateFlow("")
    val forgotPasswordEmail: StateFlow<String> = _forgotPasswordEmail.asStateFlow()

    private val _isForgotPasswordLoading = MutableStateFlow(false)
    val isForgotPasswordLoading: StateFlow<Boolean> = _isForgotPasswordLoading.asStateFlow()

    private val _forgotPasswordSuccess = MutableStateFlow(false)
    val forgotPasswordSuccess: StateFlow<Boolean> = _forgotPasswordSuccess.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Startup Session Check
    fun checkAuthState(context: Context, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val repo = getRepository(context)
            val loggedIn = repo.isLoggedIn()
            onResult(loggedIn)
        }
    }

    // --- Login Actions ---
    fun onLoginEmailChange(email: String) {
        _loginEmail.value = email
        _loginError.value = null
    }

    fun onLoginPasswordChange(password: String) {
        _loginPassword.value = password
        _loginError.value = null
    }

    fun login(context: Context) {
        if (!validateLogin()) return

        viewModelScope.launch {
            _isLoginLoading.value = true
            _loginError.value = null

            val repo = getRepository(context)
            val result = repo.login(_loginEmail.value.trim(), _loginPassword.value)

            _isLoginLoading.value = false

            result.onSuccess { user ->
                _currentUser.value = user
                _loginSuccess.value = true
            }.onFailure { exception ->
                _loginError.value = exception.message ?: "Invalid email or password"
            }
        }
    }

    private fun validateLogin(): Boolean {
        val emailInput = _loginEmail.value.trim()
        if (emailInput.isEmpty()) {
            _loginError.value = "Please enter your email"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            _loginError.value = "Please enter a valid email address"
            return false
        }
        if (_loginPassword.value.isEmpty()) {
            _loginError.value = "Please enter your password"
            return false
        }
        if (_loginPassword.value.length < 6) {
            _loginError.value = "Password must contain at least 6 characters"
            return false
        }
        return true
    }

    fun resetLoginState() {
        _loginPassword.value = ""
        _loginError.value = null
        _loginSuccess.value = false
    }

    // --- Register Actions ---
    fun onRegisterFullNameChange(name: String) {
        _registerFullName.value = name
        _registerError.value = null
    }

    fun onRegisterEmailChange(email: String) {
        _registerEmail.value = email
        _registerError.value = null
    }

    fun onRegisterPasswordChange(password: String) {
        _registerPassword.value = password
        _registerError.value = null
    }

    fun onRegisterConfirmPasswordChange(password: String) {
        _registerConfirmPassword.value = password
        _registerError.value = null
    }

    fun register(context: Context) {
        if (!validateRegister()) return

        viewModelScope.launch {
            _isRegisterLoading.value = true
            _registerError.value = null

            val repo = getRepository(context)
            val result = repo.register(
                name = _registerFullName.value.trim(),
                email = _registerEmail.value.trim().lowercase(),
                password = _registerPassword.value
            )

            _isRegisterLoading.value = false

            result.onSuccess {
                // Pre-fill login email for convenience
                _loginEmail.value = _registerEmail.value.trim().lowercase()
                _registerSuccess.value = true
            }.onFailure { exception ->
                _registerError.value = exception.message ?: "Failed to create account"
            }
        }
    }

    private fun validateRegister(): Boolean {
        if (_registerFullName.value.trim().isEmpty()) {
            _registerError.value = "Please enter your full name"
            return false
        }
        if (_registerEmail.value.trim().isEmpty()) {
            _registerError.value = "Please enter your email"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_registerEmail.value.trim()).matches()) {
            _registerError.value = "Please enter a valid email address"
            return false
        }
        if (_registerPassword.value.isEmpty() || _registerPassword.value.length < 6) {
            _registerError.value = "Password must contain at least 6 characters"
            return false
        }
        if (_registerPassword.value != _registerConfirmPassword.value) {
            _registerError.value = "Passwords do not match"
            return false
        }
        return true
    }

    fun resetRegisterState() {
        _registerFullName.value = ""
        _registerEmail.value = ""
        _registerPassword.value = ""
        _registerConfirmPassword.value = ""
        _registerError.value = null
        _registerSuccess.value = false
    }

    // --- Logout Action ---
    fun logout(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            val repo = getRepository(context)
            repo.logout()
            _currentUser.value = null
            _loginSuccess.value = false
            onComplete()
        }
    }

    // --- Dev Reset Helper ---
    fun clearAccountData(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            val repo = getRepository(context)
            repo.clearAccountData()
            _currentUser.value = null
            _loginEmail.value = ""
            _loginPassword.value = ""
            _registerFullName.value = ""
            _registerEmail.value = ""
            _registerPassword.value = ""
            _registerConfirmPassword.value = ""
            onComplete()
        }
    }

    // --- Forgot Password Actions ---
    fun onForgotPasswordEmailChange(email: String) {
        _forgotPasswordEmail.value = email
    }

    fun sendResetLink(context: Context) {
        viewModelScope.launch {
            _isForgotPasswordLoading.value = true
            val repo = getRepository(context)
            repo.forgotPassword(_forgotPasswordEmail.value.trim())
            _isForgotPasswordLoading.value = false
            _forgotPasswordSuccess.value = true
        }
    }

    fun resetForgotPasswordState() {
        _forgotPasswordEmail.value = ""
        _forgotPasswordSuccess.value = false
    }
}
