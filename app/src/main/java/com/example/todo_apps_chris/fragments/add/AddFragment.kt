package com.example.todo_apps_chris.fragments.add

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.todo_apps_chris.R
import com.example.todo_apps_chris.data.viewmodel.ToDoViewModel
import com.example.todo_apps_chris.data.models.ToDoData
import com.example.todo_apps_chris.databinding.FragmentAddBinding
import com.example.todo_apps_chris.fragments.SharedViewModel

class AddFragment : Fragment() {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    // Data binding
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentAddBinding.inflate(layoutInflater, container, false)


        // Set Menu
        setHasOptionsMenu(true)

        // For change spinner color
        binding.prioritiesSpinner.onItemSelectedListener = mSharedViewModel.listener

        return binding.root
    }

    // Set Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    // clickListener for check icon
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_add) {
            insertDataToDb()
        }
        return super.onOptionsItemSelected(item)
    }

    // Insert Data to DB
    private fun insertDataToDb() {
        val mTitle = binding.titleEt.text.toString()
        val mPriority = binding.prioritiesSpinner.selectedItem.toString()
        val mDescription = binding.descriptionEt.text.toString()

        val validation = mSharedViewModel.verifyDataFromUser(mTitle, mDescription)
        if(validation){
            // Insert Data to Database
            val newData = ToDoData(
                0,
                mTitle,
                mSharedViewModel.parsePriority(mPriority),
                mDescription
            )
            mToDoViewModel.insertData(newData)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()
            // Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }else{
            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
