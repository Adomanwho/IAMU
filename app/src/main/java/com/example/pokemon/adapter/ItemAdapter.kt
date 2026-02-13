package com.example.pokemon.adapter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pokemon.ITEM_POS
import com.example.pokemon.ItemPagerActivity
import com.example.pokemon.POKEMON_PROVIDER_CONTENT_URI
import com.example.pokemon.R
import com.example.pokemon.framework.startActivity
import com.example.pokemon.model.Item
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.File

class ItemAdapter (
    private val context: Context,
    private val items: MutableList<Item>
) : RecyclerView.Adapter<ItemAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            context.startActivity<ItemPagerActivity>(
                ITEM_POS,
                position)
        }

        holder.itemView.setOnLongClickListener {
            Log.d("DELETE LOG", "Entering delete")
            deleteItem(position)
            true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun deleteItem(position: Int) {
        val item = items[position]
        context.contentResolver.delete(
            ContentUris.withAppendedId(POKEMON_PROVIDER_CONTENT_URI, item._id!!),
            null,
            null
        )
        File(item.spriteImagePath)
        items.removeAt(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.count()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItem = itemView.findViewById<TextView>(R.id.tvItem)
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItem)

        fun bind(item: Item){
            tvItem.text = item.name
            Picasso.get()
                .load(File(item.spriteImagePath))
                .error(R.drawable.pokeball_icon)
                .transform(RoundedCornersTransformation(50, 5))
                .into(ivItem)
        }
    }
}