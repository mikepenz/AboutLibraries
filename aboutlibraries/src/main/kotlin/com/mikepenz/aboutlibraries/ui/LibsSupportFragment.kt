@file:Suppress("DEPRECATION")

package com.mikepenz.aboutlibraries.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.LibsConfiguration
import com.mikepenz.aboutlibraries.R
import com.mikepenz.aboutlibraries.ui.item.LibraryItem
import com.mikepenz.aboutlibraries.ui.item.SimpleLibraryItem
import com.mikepenz.aboutlibraries.util.doOnApplySystemWindowInsets
import com.mikepenz.aboutlibraries.util.withContext
import com.mikepenz.aboutlibraries.viewmodel.LibsViewModel
import com.mikepenz.aboutlibraries.viewmodel.LibsViewModelFactory
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.GenericItem
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * [Fragment] listing the libraries used by this project.
 *
 * Retrieves the [LibsBuilder] via the 'data' passed as argument.
 */
@Deprecated("The legacy view based UI will be deprecated in the future. Please consider moving to the compose based UI.")
open class LibsSupportFragment : Fragment(), Filterable {

    private val itemAdapter: ItemAdapter<GenericItem> = ItemAdapter()
    private val adapter: FastAdapter<GenericItem> = FastAdapter.with(itemAdapter)

    private val viewModel by activityViewModels<LibsViewModel> {
        @Suppress("DEPRECATION")
        LibsViewModelFactory(
            requireContext().applicationContext,
            arguments?.getSerializable("data") as? LibsBuilder ?: run {
                Log.i("AboutLibraries", "Fallback to default configuration, due to missing argument")
                LibsBuilder()
            },
            Libs.Builder().withContext(requireContext())
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_opensource, container, false)

        //allows to modify the view before creating
        view = LibsConfiguration.uiListener?.preOnCreateView(view) ?: view

        // init CardView
        val recyclerView: RecyclerView = if (view.id == R.id.cardListView) {
            view as RecyclerView
        } else {
            view.findViewById(R.id.cardListView) as RecyclerView
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.itemAnimator = LibsConfiguration.itemAnimator ?: DefaultItemAnimator()
        recyclerView.adapter = adapter

        //allows to modify the view after creating
        view = LibsConfiguration.uiListener?.postOnCreateView(view) ?: view

        recyclerView.doOnApplySystemWindowInsets(Gravity.BOTTOM, Gravity.START, Gravity.END)

        itemAdapter.itemFilter.filterPredicate = fun(item, constraint): Boolean {
            // Don't do any filtering if constraint is null/blank
            if (constraint.isNullOrBlank()) {
                return true
            }

            return when (item) {
                is LibraryItem -> item.library.name.contains(constraint, ignoreCase = true)
                is SimpleLibraryItem -> item.library.name.contains(constraint, ignoreCase = true)
                else -> false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            @Suppress("DEPRECATION")
            viewLifecycleOwner.whenStarted {
                withContext(Dispatchers.Main) {
                    viewModel.listItems.flowOn(Dispatchers.Main).collect {
                        itemAdapter.set(it)
                    }
                }
            }
        }

        return view
    }

    override fun getFilter(): Filter = itemAdapter.itemFilter
}
