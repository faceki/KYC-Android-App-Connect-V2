package com.palm.connectapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import com.faceki.connectapp.R

import com.faceki.connectapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val RECORDREQUESTCODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setUpPermission()
        }
    supportActionBar?.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                supportFragmentManager.fragments.forEach{
                    it.childFragmentManager.fragments.forEach { fragment ->
                        if(fragment is FirstFragment){
                           fragment.goToSetting()
                            return true
                        }
                    }
                }

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    /**
     * check if user has permission to use camera >=23
     */
    private fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)
        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    /**
     * request for camera permission for >=23 if not granted
     */
    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),RECORDREQUESTCODE)
    }
}