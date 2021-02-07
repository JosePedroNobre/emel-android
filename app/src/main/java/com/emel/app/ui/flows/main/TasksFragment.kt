package com.emel.app.ui.flows.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emel.app.R
import com.emel.app.network.api.adapter.Status
import com.emel.app.network.api.requests.TokenRequest
import com.emel.app.network.model.Malfunction
import com.emel.app.network.model.ParkingMeter
import com.emel.app.ui.adapter.MalfunctionsAdapter
import com.emel.app.ui.base.BaseFragment
import com.emel.app.utils.*
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
        getMalfunctions()
    }

    private fun getMalfunctions() {
        viewModel.getAsssignedMalfunctions(requireActivity().getToken()!!).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    malFunctions = it.data!!
                    setRvActives()
                    setRvFinished()
                }
                Status.LOADING -> Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT)
                    .show()
                Status.ERROR -> {
                    if (it.code == 401) {
                        val refreshTokenRequest =
                            TokenRequest(requireActivity().getRefreshToken().toString())

                        viewModel.refreshToken(
                            requireActivity().getToken().toString(),
                            refreshTokenRequest
                        )
                            .observeForever {
                                when (it.status) {
                                    Status.SUCCESS -> {
                                        requireActivity().clearSharedPreferences()
                                        requireActivity().setToken("Bearer ${it?.data?.token}")
                                        requireActivity().setRefreshToken("${it?.data?.refreshToken}")
                                        getMalfunctions()
                                    }
                                    Status.LOADING -> Toast.makeText(
                                        requireContext(),
                                        "Refreshing Token",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    Status.ERROR -> Toast.makeText(
                                        requireContext(),
                                        "Error while refreshing the token, logout and login again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Error while fetching the malfunctions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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