package com.example.mvvmfoodreceipes.ui.fragments.overview

import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.example.mvvmfoodreceipes.R
import com.example.mvvmfoodreceipes.databinding.FragmentOverviewBinding
import com.example.mvvmfoodreceipes.models.Result
import com.example.mvvmfoodreceipes.utils.Constants.RECIPE_RESULT_KEY
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import org.jsoup.Jsoup
import java.io.IOException

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding get() = _binding!!
    private var translate: Translate? = null
    private var language: String = "en"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        loadLocale()
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        myBundle?.let {
            binding.mainImageView.load(
                it.image
            )
            binding.titleTextView.text = it.title
            binding.likesTextView.text = it.aggregateLikes.toString()
            binding.timeTextView.text = it.readyInMinutes.toString()
            val summary = Jsoup.parse(myBundle.summary).text()
            binding.summaryTextView.text = summary

            if (myBundle.vegetarian) {
                binding.vegetarianImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                binding.vegetarianTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            if (myBundle.vegan) {
                binding.veganImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                binding.veganTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            if (myBundle.glutenFree) {
                binding.glutenFreeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                binding.glutenFreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            if (myBundle.dairyFree) {
                binding.dairyFreeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                binding.dairyFreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            }
            if (myBundle.veryHealthy) {
                binding.healthyImageView.setColorFilter(ContextCompat.getColor(requireContext(),
                    R.color.green))
                binding.healthyTextView.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.green))
            }
            if (myBundle.cheap) {
                binding.cheapImageView.setColorFilter(ContextCompat.getColor(requireContext(),
                    R.color.green))
                binding.cheapTextView.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.green))
            }
        }

        getTranslateService()
        translate()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    fun loadLocale() {
        val langPref = "Language"
        val prefs = context?.getSharedPreferences("CommonPrefs",
            AppCompatActivity.MODE_PRIVATE)
        language = prefs?.getString(langPref, "").toString()

    }

    private fun translate() {

        val originalTitle: String = binding.titleTextView.text.toString()
        val originalLikes: String = binding.likesTextView.text.toString()
        val originalTime: String = binding.timeTextView.text.toString()
        val originalSummary: String = binding.summaryTextView.text.toString()


        //Get input text to be translated:
        val translationTitle = translate!!.translate(originalTitle,
            Translate.TranslateOption.targetLanguage(language),
            Translate.TranslateOption.model("base"))
        val translationLikes = translate!!.translate(originalLikes,
            Translate.TranslateOption.targetLanguage(language),
            Translate.TranslateOption.model("base"))
        val translationTime = translate!!.translate(originalTime,
            Translate.TranslateOption.targetLanguage(language),
            Translate.TranslateOption.model("base"))
        val translationSummary = translate!!.translate(originalSummary,
            Translate.TranslateOption.targetLanguage(language),
            Translate.TranslateOption.model("base"))
        //Translated text and original text are set to TextViews:

        binding.titleTextView.text = translationTitle.translatedText
        binding.likesTextView.text = translationLikes.translatedText
        binding.timeTextView.text = translationTime.translatedText
        binding.summaryTextView.text = translationSummary.translatedText

    }

}