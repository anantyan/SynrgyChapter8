package id.anantyan.foodapps.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun setTranslate(value: Boolean)
    fun getTranslate(): Flow<Boolean>
    suspend fun setTheme(value: Boolean)
    fun getTheme(): Flow<Boolean>
    suspend fun setLogin(value: Boolean)
    fun getLogin(): Flow<Boolean>
    suspend fun setUserId(value: Int)
    fun getUserId(): Flow<Int>
}