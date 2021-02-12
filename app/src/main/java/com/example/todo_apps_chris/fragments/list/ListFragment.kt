package com.example.todo_apps_chris.fragments.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import com.example.todo_apps_chris.R
import com.example.todo_apps_chris.data.viewmodel.ToDoViewModel
import com.example.todo_apps_chris.data.models.ToDoData
import com.example.todo_apps_chris.databinding.FragmentListBinding
import com.example.todo_apps_chris.fragments.SharedViewModel
import com.example.todo_apps_chris.fragments.adapter.ListAdapter
import com.example.todo_apps_chris.utils.SwipeToDelete
import com.example.todo_apps_chris.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar
import jp.wasabeef.recyclerview.animators.LandingAnimator
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator


class ListFragment : Fragment(), SearchView.OnQueryTextListener {

    private val mToDoViewModel: ToDoViewModel by viewModels()
    private val mSharedViewModel: SharedViewModel by viewModels()

    // Data Binding
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val adapter: ListAdapter by lazy { ListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Data Binding
        _binding = FragmentListBinding.inflate(inflater, container, false)

        // for emptyDatabase in Binding Adapter
        binding.lifecycleOwner = this
        binding.mSharedViewModel = mSharedViewModel

        // Setup Recyclerview
        setupRecyclerView()

        // LiveData Observer for empty Database icon
        mToDoViewModel.getAllData.observe(viewLifecycleOwner, Observer { data ->
            mSharedViewModel.checkIfDatabaseEmpty(data)
            adapter.setData(data)
        })

        // Set Menu
        setHasOptionsMenu(true)

        // Hide Soft Keyboard
        hideKeyboard(requireActivity())

        return binding.root
    }
    //----------------------------------------------------------------------------------------------

    // Set Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_fragment_menu, menu)

        // Added for search
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }

    // Setup RecyclerView
    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        // For Animation
        recyclerView.itemAnimator = LandingAnimator().apply {
            addDuration = 300
        }

        // swipe to delete
        swipeToDelete(recyclerView)
    }

    // For Search fun in recyclerview
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            searchThroughQuery(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if (query != null) {
            searchThroughQuery(query)
        }
        return true
    }

    private fun searchThroughQuery(query: String) {

        val searchQuery = "%$query%"

        mToDoViewModel.searchDatabase(searchQuery).observe(this, Observer { list ->
            list?.let {
                adapter.setData(it)
            }
        })
    }

    // Swipe to delete
    private fun swipeToDelete(recyclerView: RecyclerView) {
        val swipeToDeleteCallback = object : SwipeToDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem = adapter.dataList[viewHolder.adapterPosition]
                mToDoViewModel.deleteItem(deletedItem)
                adapter.notifyItemRemoved(viewHolder.adapterPosition)

                // Restored deletedItem
                restoredDelete(viewHolder.itemView, deletedItem)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // Undo Delete
    private fun restoredDelete(view: View, deletedItem: ToDoData) {
        val snackBar = Snackbar.make(
            view, "Deleted '${deletedItem.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo") {
            mToDoViewModel.insertData(deletedItem)
        }
        snackBar.show()
    }

    // Delete All click listener & sorting
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete_all -> confirmRemoval()
            // Added for sorting priority
            R.id.menu_priority_high -> mToDoViewModel.sortByHighPriority.observe(
                this,
                { adapter.setData(it) })
            R.id.menu_priority_low -> mToDoViewModel.sortByLowPriority.observe(
                this,
                { adapter.setData(it) })
        }
        return super.onOptionsItemSelected(item)
    }

    // Delete All Alert Dialog
    private fun confirmRemoval() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setPositiveButton("Yes") { _, _ ->
            mToDoViewModel.deleteAll()
            Toast.makeText(requireContext(), "Successfully Removed Everything!", Toast.LENGTH_SHORT)
                .show()
        }
        builder.setNegativeButton("No") { _, _ -> }
        builder.setTitle("Delete Everything?")
        builder.setMessage("Are you sure you want to remove everything?")
        builder.create().show()
    }

    // Set binding to null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}