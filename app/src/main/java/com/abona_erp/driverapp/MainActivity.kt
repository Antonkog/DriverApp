package com.abona_erp.driverapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.abona_erp.driverapp.data.Constant.OPEN_DOC_REQUEST_CODE
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.simpleName
    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragment_container_view) as NavHostFragment

        navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_activities,
                R.id.nav_documents,
                R.id.nav_settings,
                R.id.nav_protocol
            ), drawerLayout
        )

        val isTablet = resources.getBoolean(R.bool.isTabletLandscape)

        if (!isTablet) {
            setupActionBarWithNavController(navController, appBarConfiguration)
        } else {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                supportActionBar!!.title = destination.label
            }
        }

        mainViewModel.vechicle.observe(this, Observer {
            navView.getHeaderView(0).let {v->
                v.findViewById<TextView>(R.id.vehicle_num).text = it.registrationNumber
                v.findViewById<TextView>(R.id.client_name).text = it.clientName
            }
        })


        setupErrorHandling()
        navView.setupWithNavController(navController)
    }

    private fun setupErrorHandling() {
        val linContainer = findViewById<LinearLayout>(R.id.status_container)
        val bottomSheetBehavior =
            BottomSheetBehavior.from(linContainer)
        mainViewModel.requestStatus.observe(this, Observer {
            when (it.type) {
                MainViewModel.StatusType.LOADING -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    linContainer.findViewById<ProgressBar>(R.id.progress).visibility = View.VISIBLE
                }
                MainViewModel.StatusType.ERROR -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    linContainer.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
                    linContainer.findViewById<TextView>(R.id.status_text).text = it.message
                }
                MainViewModel.StatusType.COMPLETE -> {
                    linContainer.findViewById<ProgressBar>(R.id.progress).visibility = View.GONE
                    linContainer.findViewById<TextView>(R.id.status_text).clearComposingText()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_send_doc).let {
            it?.setVisible(false)
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_log_out -> {
                mainViewModel.doLogOutActions()
                navController.navigate(R.id.nav_login)
                true
            }
            R.id.action_send_doc -> {
                openDocumentPicker()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, OPEN_DOC_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == OPEN_DOC_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
        ) {
            Log.e(TAG, " got result: $resultData ")
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Log.e(TAG, " got uri: $uri ")
                // Perform operations on the document using its URI.
                RxBus.publish(RxBusEvent.DocumentMessage(uri))
            }
        }
    }
}
