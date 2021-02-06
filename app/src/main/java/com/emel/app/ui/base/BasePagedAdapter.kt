package com.emel.app.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BasePagedAdapter<T>(diffCallBack: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, RecyclerView.ViewHolder>(diffCallBack) {

    @LayoutRes
    abstract fun layoutToInflate(viewType: Int): Int

    abstract fun defineViewHolder(viewType: Int, view: View): RecyclerView.ViewHolder

    abstract fun doOnBindViewHolder(holder: RecyclerView.ViewHolder, item: T?, position: Int)

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutToInflate(viewType), parent, false)
        return defineViewHolder(viewType, view)
    }

    final override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        doOnBindViewHolder(holder, getItem(position), position)
    }

    fun retrieveItem(position: Int) = getItem(position)
}