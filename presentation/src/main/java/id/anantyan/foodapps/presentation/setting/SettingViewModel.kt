package id.anantyan.foodapps.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.anantyan.foodapps.domain.repository.PreferencesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val preferencesUseCase: PreferencesUseCase
) : ViewModel() {
    val getTheme: Flow<Boolean> = preferencesUseCase.executeGetTheme()
    val getTranslate: Flow<Boolean> = preferencesUseCase.executeGetTranslate()

    fun setTheme(value: Boolean) {
        viewModelScope.launch {
            preferencesUseCase.executeSetTheme(value)
        }
    }

    fun setTranslate(value: Boolean) {
        viewModelScope.launch {
            preferencesUseCase.executeSetTranslate(value)
        }
    }
}
