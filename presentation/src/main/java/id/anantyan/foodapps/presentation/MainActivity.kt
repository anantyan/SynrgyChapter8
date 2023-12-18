package id.anantyan.foodapps.presentation

import android.Manifest
import android.app.UiModeManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import id.anantyan.foodapps.common.permissionDialog
import id.anantyan.foodapps.presentation.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val viewModel: MainViewModel by viewModels()
    private val permissionNotificationLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindView()
    }

    private fun bindView() {
        val host = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = host.navController
        navController.addOnDestinationChangedListener(this)

        binding.bottomNav.setupWithNavController(navController)

        if (viewModel.getLogin()) {
            val destination = NavGraphMainDirections.actionRootToHomeFragment()
            navController.navigate(destination)
        } else {
            val destination = NavGraphMainDirections.actionRootToLoginFragment()
            navController.navigate(destination)
        }

        viewModel.getTheme().onEach {
            val legacy = if (it) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            val latest = if (it) UiModeManager.MODE_NIGHT_YES else UiModeManager.MODE_NIGHT_NO

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                    uiModeManager.setApplicationNightMode(latest)
                }
                Build.VERSION.SDK_INT < Build.VERSION_CODES.S -> {
                    AppCompatDelegate.setDefaultNightMode(legacy)
                }
            }
        }.flowWithLifecycle(lifecycle).launchIn(lifecycleScope)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> binding.bottomNav.isVisible = true
            R.id.profileFragment -> {
                requestPermissionOptions()
                binding.bottomNav.isVisible = true
            }
            R.id.favoriteFragment -> binding.bottomNav.isVisible = true
            R.id.changeProfileFragment -> binding.bottomNav.isVisible = true
            R.id.settingFragment -> binding.bottomNav.isVisible = true
            else -> binding.bottomNav.isVisible = false
        }
    }

    private fun requestPermissionOptions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    permissionDialog(
                        "Notification Permission Required",
                        "Without the notification it is not possible to Information Upload Photo..."
                    )
                }
                else -> {
                    permissionNotificationLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }
}
