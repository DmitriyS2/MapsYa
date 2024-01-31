package ru.netology.mapsya.activity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.mapsya.R
import ru.netology.mapsya.adapter.Listener
import ru.netology.mapsya.adapter.MapObjectAdapter
import ru.netology.mapsya.databinding.FragmentFavoriteBinding
import ru.netology.mapsya.dto.DataMapObject
import ru.netology.mapsya.viewmodel.MainViewModel


@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    lateinit var binding: FragmentFavoriteBinding

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        val adapter = MapObjectAdapter(object : Listener {
            override fun deleteMapObject(id: Long) {
                viewModel.removeMapObject(id)
            }

            override fun editMapObject(dataMapObject: DataMapObject, newDescription: String) {
                viewModel.editMapObject(dataMapObject, newDescription)
            }

            override fun goToMapObject(dataMapObject: DataMapObject) {
                viewModel.goToPoint(dataMapObject)
                viewModel.pointBottomMenu.value = 0
                findNavController()
                    .navigate(R.id.mapsFragment)
            }
        })

        binding = FragmentFavoriteBinding.inflate(layoutInflater, container, false)

        binding.rwFavorite.layoutManager = LinearLayoutManager(activity)
        binding.rwFavorite.adapter = adapter

        viewModel.allFavoriteMapObject.observe(viewLifecycleOwner) {
            Log.d("MyLog", "observe it=$it")
            adapter.mapObjectList = it
            adapter.submitList(it)
            binding.buttonShowAllFavorite.isEnabled = !it.isNullOrEmpty()
            binding.buttonDeleteAllFavorite.isEnabled = !it.isNullOrEmpty()
        }

        binding.buttonDeleteAllFavorite.setOnClickListener {
            areYouSure()
        }

        binding.buttonShowAllFavorite.setOnClickListener {
            viewModel.showAll()
            findNavController()
                .navigate(R.id.mapsFragment)
        }

        return binding.root
    }

    private fun areYouSure() {
        val menuDialog = MyDialogFragment()
        val manager = childFragmentManager
        menuDialog.show(manager, "Remove all")
    }

    companion object {
        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }
}