package id.anantyan.foodapps.presentation.change_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.anantyan.foodapps.common.UIState
import id.anantyan.foodapps.domain.model.UserModel
import id.anantyan.foodapps.domain.repository.PreferencesUseCase
import id.anantyan.foodapps.domain.repository.UserUseCase
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@HiltViewModel
class ChangeProfileViewModel @Inject constructor(
    private val userUseCase: UserUseCase,
    private val preferencesUseCase: PreferencesUseCase
) : ViewModel() {
    private var _changeProfile: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var _checkProfile: MutableStateFlow<UIState<UserModel>> = MutableStateFlow(
        UIState.Loading()
    )

    val getUserId: Int = runBlocking { preferencesUseCase.executeGetUserId().first() }
    val changeProfile: StateFlow<Boolean> = _changeProfile
    val checkProfile: StateFlow<UIState<UserModel>> = _checkProfile

    fun changeProfile(userModel: UserModel) {
        viewModelScope.launch {
            userUseCase.executeChangeProfile(userModel)
            _changeProfile.value = true
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun checkProfile() {
        viewModelScope.launch {
            preferencesUseCase.executeGetUserId().flatMapLatest {
                userUseCase.executeProfile(it)
            }.collect { state ->
                _checkProfile.value = when (state) {
                    is UIState.Loading -> { UIState.Loading() }
                    is UIState.Success -> { UIState.Success(state.data ?: UserModel()) }
                    is UIState.Error -> { UIState.Error(null, state.message!!) }
                }
            }
        }
    }
}
