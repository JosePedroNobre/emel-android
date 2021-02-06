package com.emel.app.ui.flows.main

import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emel.app.R
import com.emel.app.network.api.adapter.Status
import com.emel.app.network.model.Malfunction
import com.emel.app.network.model.ParkingMeter
import com.emel.app.ui.adapter.MalfunctionsAdapter
import com.emel.app.ui.base.BaseFragment
import com.emel.app.utils.LoadingUtils
import com.emel.app.utils.getToken
import kotlinx.android.synthetic.main.fragment_tasks.*
import javax.inject.Inject

class TasksFragment : BaseFragment<TasksFragmentVM>() {

    private lateinit var adapter: MalfunctionsAdapter

    companion object {
        fun newInstance() = TasksFragment()
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun layoutToInflate() = R.layout.fragment_tasks

    override fun defineViewModel() =
        ViewModelProviders.of(this, viewModelFactory).get(TasksFragmentVM::class.java)

    private var malFunctions: List<Malfunction> = emptyList()

    override fun doOnCreated() {
        viewModel.getAsssignedMalfunctions(requireActivity().getToken()!!).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    malFunctions = it.data!!
                    setRvActives()
                    setRvFinished()
                    LoadingUtils.dismiss()
                }
                Status.LOADING -> LoadingUtils.showLoading(childFragmentManager)
                Status.ERROR -> {
                    LoadingUtils.dismiss()
                    Toast.makeText(
                        requireActivity(),
                        "Error while fetching the malfunctions",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
    }

    private fun setRvActives() {

        val activeMalfunctions = malFunctions.filter {
            it.status == 0 || it.status == 1 || it.status == 2
        }

        activesRv.layoutManager = LinearLayoutManager(requireContext())
        adapter = MalfunctionsAdapter(activeMalfunctions)
        adapter.notifyDataSetChanged()
        activesRv.adapter = adapter
    }

    private fun setRvFinished() {

        val inactiveMalfunctions = malFunctions.filter {
            it.status == 3
        }

        finishedRv.layoutManager = LinearLayoutManager(requireContext())
        adapter = MalfunctionsAdapter(inactiveMalfunctions)
        adapter.notifyDataSetChanged()
        finishedRv.adapter = adapter
    }
}