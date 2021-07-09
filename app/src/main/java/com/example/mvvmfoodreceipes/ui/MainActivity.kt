package com.example.mvvmfoodreceipes.ui

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.akexorcist.localizationactivity.core.LocalizationActivityDelegate
import com.akexorcist.localizationactivity.core.OnLocaleChangedListener
import com.example.mvvmfoodreceipes.R
import com.example.mvvmfoodreceipes.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnLocaleChangedListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController
    private val currentLanguage: Locale
        get() = localizationDelegate.getLanguage(this)

    private val localizationDelegate = LocalizationActivityDelegate(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        saveLocale(currentLanguage.toString())
        localizationDelegate.addOnLocaleChangedListener(this)
        localizationDelegate.onCreate()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(R.style.AppTheme)
        setContentView(binding.root)

        navController = findNavController(R.id.navHostFragment)
        val appBarConfig = AppBarConfiguration(
            setOf(
                R.id.recipesFragment,
                R.id.favouriteRecipesFragment,
                R.id.foodJokeFragment
            )
        )

        binding.bottomNavigationView.setupWithNavController(navController)
        setupActionBarWithNavController(navController, appBarConfig)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_language -> {
                // Do Activity menu item stuff here
                languageDialog()
                Toast.makeText(this, "mmmdmd", Toast.LENGTH_LONG).show()
                return true
            }

        }
        return false
    }

    public override fun onResume() {
        super.onResume()
        localizationDelegate.onResume(this)
    }

    override fun attachBaseContext(newBase: Context) {
        applyOverrideConfiguration(localizationDelegate.updateConfigurationLocale(newBase))
        super.attachBaseContext(newBase)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    override fun getResources(): Resources {
        return localizationDelegate.getResources(super.getResources())
    }

    fun setLanguage(language: String?) {
        localizationDelegate.setLanguage(this, language!!)
    }


    // Just override method locale change event
    override fun onBeforeLocaleChanged() {}
    override fun onAfterLocaleChanged() {}


    private fun languageDialog() {
        val d = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.spinner_dialog, null)
        d.setTitle(R.string.unitTitle)
        d.setView(dialogView)
        val spinner = dialogView.findViewById<AppCompatSpinner>(R.id.spinner02)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                when (position) {
                    0 -> {
                        setLanguage("en")
                    }
                    1 -> {
                        setLanguage("ar")
                    }
                    2 -> {
                        setLanguage("gu")
                    }
                    3 -> {
                        setLanguage("hi")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val alertDialog = d.create()
        alertDialog.show()
    }

    private fun saveLocale(lang: String?) {
        val langPref = "Language"
        val prefs = getSharedPreferences("CommonPrefs",
            MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(langPref, lang)
        editor.apply()
    }


}