package com.abona_erp.driverapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.abona_erp.driverapp.data.Constant.OPEN_DOC_REQUEST_CODE
import com.abona_erp.driverapp.ui.RxBus
import com.abona_erp.driverapp.ui.events.RxBusEvent
import com.abona_erp.driverapp.ui.utils.DeviceUtils
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.simpleName
    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)


        val  firebaseAnalytics = Firebase.analytics

        setSupportActionBar(toolbar)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_activities,
                R.id.nav_documents,
                R.id.nav_settings
            ), drawerLayout
        )

        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN){
            param("DeviceId", DeviceUtils.getUniqueID(baseContext))
        }


        val isTablet = resources.getBoolean(R.bool.isTabletLandscape)

        if (!isTablet) {
            setupActionBarWithNavController(navController, appBarConfiguration)
        } else {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                supportActionBar!!.title = destination.label
            }
        }

        navView.setupWithNavController(navController)

        mainViewModel.navigateToLogin.observe(this, Observer {
            navController.navigate(R.id.nav_login)
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
                true
            }
            R.id.action_send_doc -> {
                openDocumentPicker()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun openDirectory(pickerInitialUri: Uri?) {
        // Choose a directory using the system's file picker.
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            // Provide read access to files and sub-directories in the user-selected
            // directory.
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            if (pickerInitialUri != null) putExtra(
                DocumentsContract.EXTRA_INITIAL_URI,
                pickerInitialUri
            )
        }

        startActivityForResult(intent, OPEN_DOC_REQUEST_CODE)
    }

    private fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            /**
             * It's possible to limit the types of files by mime-type. Since this
             * app displays pages from a PDF file, we'll specify `application/pdf`
             * in `type`.
             * See [Intent.setType] for more details.
             */
            type = "application/pdf"

            /**
             * Because we'll want to use [ContentResolver.openFileDescriptor] to read
             * the data of whatever file is picked, we set [Intent.CATEGORY_OPENABLE]
             * to ensure this will succeed.
             */
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

//                    val takeFlags = (intent.flags
//                            and (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
//// Check for the freshest data.
//// Check for the freshest data.
//                    contentResolver.takePersistableUriPermission(uri, takeFlags)
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
