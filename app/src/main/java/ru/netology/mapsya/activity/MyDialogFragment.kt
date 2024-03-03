package ru.netology.mapsya.activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import ru.netology.mapsya.R
import ru.netology.mapsya.databinding.FragmentMyDialogBinding
import ru.netology.mapsya.viewmodel.MainViewModel

class MyDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentMyDialogBinding.inflate(layoutInflater)
        val builder = AlertDialog.Builder(requireContext())

        return builder
            .setView(binding.root)
            .setTitle("Удаление всех объектов!")
            .setIcon(R.drawable.baseline_warning_24)
            .setCancelable(true)
            .setPositiveButton("Удалить") { _, _ ->
                dialog?.cancel()
                viewModel.removeAll()
            }
            .setNegativeButton("Отмена") { _, _ ->
                dialog?.cancel()
            }
            .create()
    }
}