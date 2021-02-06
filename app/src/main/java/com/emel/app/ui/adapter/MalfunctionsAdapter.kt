package com.emel.app.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.emel.app.R
import com.emel.app.network.model.Malfunction
import kotlinx.android.synthetic.main.task_item.view.*
import java.util.ArrayList

class MalfunctionsAdapter(
    private val itemsCells: List<Malfunction>
) :
    RecyclerView.Adapter<MalfunctionsAdapter.ViewHolder>() {

    private lateinit var context: Context

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val v =
            LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return itemsCells.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.addressTask.text = itemsCells[position].id.toString()
        holder.itemView.descriptionTask.text = itemsCells[position].description.toString()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        holder.itemView.directionsButton.setOnClickListener {
            itemsCells[position].latitude
            itemsCells[position].longitude

            val gmmIntentUri =
                Uri.parse("geo:" + itemsCells[position].latitude + itemsCells[position].longitude)
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }
    }
}