package com.orbit.viewmodel

import androidx.lifecycle.ViewModel
import com.web3auth.core.types.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _userInfo = MutableStateFlow<UserInfo?>(null)
    val userInfo: StateFlow<UserInfo?> = _userInfo

    fun setUserInfo(userInfo: UserInfo?) {
        _userInfo.value = userInfo
    }

    fun logout(onLogout: () -> Unit) {
        _userInfo.value = null
        onLogout()
    }
}
