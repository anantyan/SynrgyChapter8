package id.anantyan.foodapps.domain.repository

import id.anantyan.foodapps.common.UIState
import id.anantyan.foodapps.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

class UserUseCase(private val userRepository: UserRepository) {
    suspend fun executeLogin(user: UserModel): UserModel? = userRepository.login(user)
    fun executeRegister(user: UserModel): Flow<UIState<Int>> = userRepository.register(user)
    fun executeProfile(id: Int?): Flow<UIState<UserModel>> = userRepository.profile(id)
    suspend fun executeChangeProfile(user: UserModel) = userRepository.changeProfile(user)
}