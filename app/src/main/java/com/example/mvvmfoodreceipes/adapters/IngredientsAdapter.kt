package com.example.mvvmfoodreceipes.adapters

import android.content.res.Resources
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mvvmfoodreceipes.R
import com.example.mvvmfoodreceipes.databinding.IngredientsRowLayoutBinding
import com.example.mvvmfoodreceipes.models.ExtendedIngredient
import com.example.mvvmfoodreceipes.utils.Constants.BASE_IMAGE_URL
import com.example.mvvmfoodreceipes.utils.RecipesDiffUtil
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.translate.Translate
import com.google.cloud.translate.TranslateOptions
import java.io.IOException
import java.util.*


class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.IngredientsViewHolder>() {

    private var ingredientsList = emptyList<ExtendedIngredient>()
    private lateinit var holder: IngredientsViewHolder
    private var translate: Translate? = null
    private var language: String = "en"


    class IngredientsViewHolder(val binding: IngredientsRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientsViewHolder {
        holder = IngredientsViewHolder(IngredientsRowLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
        return holder
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    override fun onBindViewHolder(holder: IngredientsViewHolder, position: Int) {
        holder.binding.apply {
            ingredientImageView.load(BASE_IMAGE_URL + ingredientsList[position].image) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder)
            }
            ingredientName.text = ingredientsList[position].name.capitalize(Locale.ROOT)
            ingredientAmount.text = ingredientsList[position].amount.toString()
            ingredientUnit.text = ingredientsList[position].unit
            ingredientConsistency.text = ingredientsList[position].consistency
            ingredientOriginal.text = ingredientsList[position].original
        }

        translate(language)

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

        val originalIngredientName: String = holder.binding.ingredientName.text.toString()
        val originalIngredientAmount: String = holder.binding.ingredientAmount.text.toString()
        val originalIngredientUnit: String = holder.binding.ingredientUnit.text.toString()
        val originalIngredientConsistency: String =
            holder.binding.ingredientConsistency.text.toString()
        val originalIngredientOriginal: String = holder.binding.ingredientOriginal.text.toString()


        //Get input text to be translated:
        val translationIngredientName = translate!!.translate(originalIngredientName,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationIngredientAmount = translate!!.translate(originalIngredientAmount,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationIngredientUnit = translate!!.translate(originalIngredientUnit,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationIngredientConsistency = translate!!.translate(originalIngredientConsistency,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        val translationIngredientOriginal = translate!!.translate(originalIngredientOriginal,
            Translate.TranslateOption.targetLanguage(lang),
            Translate.TranslateOption.model("base"))
        //Translated text and original text are set to TextViews:

        holder.binding.ingredientName.text = translationIngredientName.translatedText
        holder.binding.ingredientAmount.text = translationIngredientAmount.translatedText
        holder.binding.ingredientUnit.text = translationIngredientUnit.translatedText
        holder.binding.ingredientConsistency.text = translationIngredientConsistency.translatedText
        holder.binding.ingredientOriginal.text = translationIngredientOriginal.translatedText

    }


    fun setData(newIngredients: List<ExtendedIngredient>) {
        val ingredientsDiffUtil = RecipesDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }
}