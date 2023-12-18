package id.anantyan.foodapps.domain.repository

import id.anantyan.foodapps.common.UIState
import id.anantyan.foodapps.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(user: UserModel): UserModel?
    fun register(user: UserModel): Flow<UIState<Int>>
    fun profile(id: Int?): Flow<UIState<UserModel>>
    suspend fun changeProfile(user: UserModel)
}