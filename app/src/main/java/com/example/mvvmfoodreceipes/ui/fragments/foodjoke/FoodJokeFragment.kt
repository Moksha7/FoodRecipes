package com.example.mvvmfoodreceipes.ui.fragments.foodjoke

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mvvmfoodreceipes.R
import com.example.mvvmfoodreceipes.databinding.FragmentFoodJokeBinding
import com.example.mvvmfoodreceipes.utils.Constants.API_KEY
import com.example.mvvmfoodreceipes.utils.NetworkResult
import com.example.mvvmfoodreceipes.viewmodels.MainViewModel
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.IOException

@AndroidEntryPoint
class FoodJokeFragment : Fragment(R.layout.fragment_food_joke) {

    private val mainViewModel by viewModels<MainViewModel>()
    private var _binding: FragmentFoodJokeBinding? = null
    private val binding get() = _binding!!
    private var translate: Translate? = null
    private var foodJoke = "No Food Joke."
    private var language: String = "en"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        loadLocale()
        _binding = FragmentFoodJokeBinding.inflate(
            inflater,
            container,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.mainViewModel = mainViewModel

        setHasOptionsMenu(true)

        mainViewModel.getFoodJoke(API_KEY)
        mainViewModel.foodJokeResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is NetworkResult.Success -> {
                    binding.foodJokeTextView.text = response.data?.text
                    response.data?.let {
                        foodJoke = it.text
                    }
                    getTranslateService()
                    translate()
                }
                is NetworkResult.Error -> {
                    loadDataFromCache()
                    getTranslateService()
                    translate()
                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    Log.d("FoodJokeFragment", "Loading")
                }
            }
        }

        return binding.root
    }

    private fun getTranslateService() {

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        try {
            resources.openRawResource(R.raw.credentials).use { `is` ->
                val myCredentials = GoogleCredentials.fromStream(`is`)
                val translateOptions =
                    TranslateOptions.newBuilder().setCredentials(myCredentials).build()
                translate = translateOptions.service
            }
        } catch (ioe: IOException) {
            ioe.printStackTrace()

        }

    }

    private fun translate() {

        //Get input text to be translated:
        val originalText: String = binding.foodJokeTextView.text.toString()
        val translation = translate!!.translate(originalText,
            Translate.TranslateOption.targetLanguage(language),
            Translate.TranslateOption.model("base"))

        //Translated text and original text are set to TextViews:
        binding.foodJokeTextView.text = translation.translatedText

    }


    private fun loadLocale() {
        val langPref = "Language"
        val prefs = context?.getSharedPreferences("CommonPrefs",
            AppCompatActivity.MODE_PRIVATE)
        language = prefs?.getString(langPref, "").toString()

    }


    private fun loadDataFromCache() {
        lifecycleScope.launch {
            mainViewModel.readFoodJoke.observe(viewLifecycleOwner) { database ->
                if (!database.isNullOrEmpty()) {
                    binding.foodJokeTextView.text = database[0].foodJoke.text
                    foodJoke = database[0].foodJoke.text
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.food_joke_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share_food_joke_menu) {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, foodJoke)
                type = "text/plain"
            }
            startActivity(shareIntent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}