package com.example.weather360

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.weather360.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        binding.appBarMain.ibDrawer.setOnClickListener {
            binding.drawerLayout.open()
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_favorite, R.id.nav_alert
            ), drawerLayout
        )

        navView.setupWithNavController(navController)

//        binding.navView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_home -> {
//                    binding.appBarMain.tvFragmentName.text = "Home"
//                }
//                R.id.nav_gallery -> {
//                    binding.appBarMain.tvFragmentName.text = "Gallery"
//                }
//                R.id.nav_slideshow -> {
//                    binding.appBarMain.tvFragmentName.text = "Slideshow"
//                }
//            }
//            drawerLayout.closeDrawer(GravityCompat.START)
//            true
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}