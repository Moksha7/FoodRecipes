package com.example.mvvmfoodreceipes.ui.fragments.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmfoodreceipes.adapters.IngredientsAdapter
import com.example.mvvmfoodreceipes.databinding.FragmentIngredientsBinding
import com.example.mvvmfoodreceipes.models.Result
import com.example.mvvmfoodreceipes.utils.Constants.RECIPE_RESULT_KEY

class IngredientsFragment : Fragment() {
    private var language: String = "en"
    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }
    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadLocale()
        // Inflate the layout for this fragment
        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)

        val args = arguments
        val myBundle: Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        setUpRecyclerView()
        myBundle?.extendedIngredients?.let {
            mAdapter.setData(it)
        }
        mAdapter.getTranslateService(resources, language)
        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.ingredientsRecyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

    }

    fun loadLocale() {
        val langPref = "Language"
        val prefs = context?.getSharedPreferences("CommonPrefs",
            AppCompatActivity.MODE_PRIVATE)
        language = prefs?.getString(langPref, "").toString()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}