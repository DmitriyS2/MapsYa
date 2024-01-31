package ru.netology.mapsya.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mapsya.R
import ru.netology.mapsya.databinding.ActivityMainBinding
import ru.netology.mapsya.viewmodel.MainViewModel


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val viewModel:MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomMenu.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mapsFragment -> {
                    viewModel.currentFavoriteMapObject.value = null
                    viewModel.pointBottomMenu.value = 0
                    findNavController(R.id.nav_host_fragment)
                        .navigate(R.id.mapsFragment)
                }

                R.id.favoriteFragment -> {
                    findNavController(R.id.nav_host_fragment)
                        .navigate(R.id.favoriteFragment)
                }

            }
            true
        }

        viewModel.pointBottomMenu.observe(this) {
            binding.bottomMenu.menu.getItem(it).isChecked = true
        }
    }
}