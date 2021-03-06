package com.example.mvvmfoodreceipes.ui.fragments.favourites

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmfoodreceipes.R
import com.example.mvvmfoodreceipes.adapters.FavouriteRecipesAdapter
import com.example.mvvmfoodreceipes.databinding.FragmentFavouriteRecipesBinding
import com.example.mvvmfoodreceipes.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavouriteRecipesFragment : Fragment() {

    private var language: String = "en"
    private val mainViewModel: MainViewModel by viewModels()
    private val mAdapter: FavouriteRecipesAdapter by lazy { FavouriteRecipesAdapter(requireActivity(), mainViewModel) }

    private var _binding: FragmentFavouriteRecipesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        loadLocale()
        _binding = FragmentFavouriteRecipesBinding.inflate(
            inflater,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.mainViewModel = mainViewModel
        binding.mAdapter = mAdapter
        setHasOptionsMenu(true)
        setUpRecyclerView(binding.favouriteRecipesRecyclerView)
        mAdapter.getTranslateService(resources, language)
        return binding.root
    }

    private fun setUpRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun showSnackBar() {
        Snackbar.make(
            binding.root,
            "All recipes removed",
            Snackbar.LENGTH_SHORT
        )
            .setAction("Ok") {}
            .show()
    }

    private fun loadLocale() {
        val langPref = "Language"
        val prefs = context?.getSharedPreferences("CommonPrefs",
            AppCompatActivity.MODE_PRIVATE)
        language = prefs?.getString(langPref, "").toString()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(
            R.menu.favourites_recipes_menu,
            menu
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deleteAll_favourite_recipes_menu) {
            mainViewModel.deleteAllFavouriteRecipes()
            showSnackBar()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mAdapter.clearContextualActionMode()
    }

}