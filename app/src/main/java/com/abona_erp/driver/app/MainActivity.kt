package com.abona_erp.driver.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.abona_erp.driver.app.ui.RxBus
import com.abona_erp.driver.app.ui.events.RxBusEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel> ()
    private lateinit var navController : NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(R.id.nav_home, R.id.nav_activities, R.id.nav_documents, R.id.nav_delay_reason,  R.id.nav_settings), drawerLayout)

        val isTablet = resources.getBoolean(R.bool.isTabletLandscape)

        if(!isTablet){
            setupActionBarWithNavController(navController, appBarConfiguration)
        } else{
            navController.addOnDestinationChangedListener { _, destination, _ ->
                supportActionBar!!.title = destination.label
            }
        }

        navView.setupWithNavController(navController)
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_show_all).let {
            it?.setChecked(mainViewModel.getShowAll())
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_log_out -> {
                mainViewModel.resetAuthTime()
                navController.navigate(R.id.nav_login)
                true
            }
            R.id.action_show_all -> {
                item.isChecked = !item.isChecked
                mainViewModel.setShowAll(item.isChecked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
