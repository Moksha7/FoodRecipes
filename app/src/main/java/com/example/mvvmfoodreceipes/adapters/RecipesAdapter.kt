package com.example.mvvmfoodreceipes.adapters

import android.content.res.Resources
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmfoodreceipes.R
import com.example.mvvmfoodreceipes.databinding.RecipesRowLayoutBinding
import com.example.mvvmfoodreceipes.models.FoodRecipe
import com.example.mvvmfoodreceipes.models.Result
import com.example.mvvmfoodreceipes.utils.RecipesDiffUtil
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.io.IOException

class RecipesAdapter : RecyclerView.Adapter<RecipesAdapter.RecipesViewHolder>() {

    private lateinit var holder: RecipesViewHolder
    private var translate: Translate? = null
    private var language: String = "en"

    private var recipes = emptyList<Result>()

    class RecipesViewHolder(
        val binding: RecipesRowLayoutBinding, // This class generates automatically when we convert our view to a binding layout
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(result: Result) {
            binding.result = result
            binding.executePendingBindings()
        }

        companion object {

            fun from(parent: ViewGroup): RecipesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecipesRowLayoutBinding.inflate(
                    layoutInflater,
                    parent,
                    false)
                return RecipesViewHolder(binding)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipesViewHolder {
        holder = RecipesViewHolder.from(parent)
        return holder
    }

    override fun onBindViewHolder(holder: RecipesViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)
        translate(language)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    fun getTranslateService(resources: Resources, lang: String) {

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

        language = lang
    }

    fun translate(lang: String) {

        val originalTitle: String = holder.binding.titleTextView.text.toString()
        val originalDescription: String = holder.binding.descriptionTextView.text.toString()
        val originalHeart: String = holder.binding.heartTextView.text.toString()
        val originalClock: String = holder.binding.clockTextView.text.toString()
        val originalLeaf: String = holder.binding.leafTextView.text.toString()


        //Get input text to be translated:
        val translationTitle = translate!!.translate(originalTitle,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationDescription = translate!!.translate(originalDescription,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationHeart = translate!!.translate(originalHeart,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationClock = translate!!.translate(originalClock,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationLeaf = translate!!.translate(originalLeaf,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        //Translated text and original text are set to TextViews:

        holder.binding.titleTextView.text = translationTitle.translatedText
        holder.binding.descriptionTextView.text = translationDescription.translatedText
        holder.binding.heartTextView.text = translationHeart.translatedText
        holder.binding.clockTextView.text = translationClock.translatedText
        holder.binding.leafTextView.text = translationLeaf.translatedText

    }

    fun setData(newData: FoodRecipe) {
        val recipesDiffUtil = RecipesDiffUtil(recipes, newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }


}