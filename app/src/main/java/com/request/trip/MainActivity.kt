package com.request.trip

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.request.trip.databinding.ActivityMainBinding
import com.request.trip.profile.User
import com.request.trip.utils.PrefManager
import com.request.trip.utils.setGone
import com.request.trip.utils.setLocale
import com.request.trip.utils.setVisible

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.navHostFragment)
        binding.bottomNavView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.profileFragment -> {
                    showBottomNav()
                }

                R.id.loginFragment -> {
                    hideBottomNav()
                }

                R.id.chatListFragment -> {
                    showBottomNav()
                }

                R.id.chatFragment -> {
                    hideBottomNav()
                }

                R.id.searchFragment -> {
                    showBottomNav()
                }

                R.id.notificationFragment -> {
                    showBottomNav()
                }
            }
        }
    }

    private fun showBottomNav() = binding.bottomNavView.setVisible()
    private fun hideBottomNav() = binding.bottomNavView.setGone()

    fun navigateToLogin() {
        val action = NavGraphDirections.actionGlobalLoginFragment()
        navController.navigate(action)
    }

    fun setCurrentUser(user: User?) {
        currentUser = user
    }

    fun getCurrentUser() = currentUser

    override fun attachBaseContext(newBase: Context?) {
        val locale = PrefManager.getLocale()
        super.attachBaseContext(newBase.setLocale(locale))
    }
}