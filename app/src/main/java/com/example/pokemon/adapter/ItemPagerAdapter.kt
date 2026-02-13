package com.example.pokemon.adapter

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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

class ItemPagerAdapter (
    private val context: Context,
    private val items: MutableList<Item>
) : RecyclerView.Adapter<ItemPagerAdapter.ViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_pager, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = items[position]
        holder.bind(item)
        holder.ivNameIcon.setOnClickListener {
            updateItem(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateItem(position: Int) {
        val item = items[position]
        item.read = !item.read
        context.contentResolver.update(
            ContentUris.withAppendedId(POKEMON_PROVIDER_CONTENT_URI, item._id!!),
            ContentValues().apply {
                put(Item::read.name, item.read)
            },
            null,
            null
        )
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int = items.count()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivItem = itemView.findViewById<ImageView>(R.id.ivItem)
        val ivNameIcon = itemView.findViewById<ImageView>(R.id.ivRead)
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvPokedexNumber = itemView.findViewById<TextView>(R.id.tvPokedexNumber)
        private val tvHeight = itemView.findViewById<TextView>(R.id.tvHeight)
        private val tvWeight = itemView.findViewById<TextView>(R.id.tvWeight)
        private val tvType1 = itemView.findViewById<TextView>(R.id.tvType1)
        private val tvType2 = itemView.findViewById<TextView>(R.id.tvType2)

        // Stats TextViews
        private val tvHp = itemView.findViewById<TextView>(R.id.tvHp)
        private val tvAttack = itemView.findViewById<TextView>(R.id.tvAttack)
        private val tvDefense = itemView.findViewById<TextView>(R.id.tvDefense)
        private val tvSpecialAttack = itemView.findViewById<TextView>(R.id.tvSpecialAttack)
        private val tvSpecialDefense = itemView.findViewById<TextView>(R.id.tvSpecialDefense)
        private val tvSpeed = itemView.findViewById<TextView>(R.id.tvSpeed)

        // Stats ProgressBars
        private val pbHp = itemView.findViewById<ProgressBar>(R.id.pbHp)
        private val pbAttack = itemView.findViewById<ProgressBar>(R.id.pbAttack)
        private val pbDefense = itemView.findViewById<ProgressBar>(R.id.pbDefense)
        private val pbSpecialAttack = itemView.findViewById<ProgressBar>(R.id.pbSpecialAttack)
        private val pbSpecialDefense = itemView.findViewById<ProgressBar>(R.id.pbSpecialDefense)
        private val pbSpeed = itemView.findViewById<ProgressBar>(R.id.pbSpeed)

        fun bind(item: Item){
            // Load sprite image
            Picasso.get()
                .load(File(item.spriteImagePath))
                .error(R.drawable.pokeball_icon)
                .into(ivItem)

            ivNameIcon.setImageResource(if(item.read) R.drawable.red_flag else R.drawable.green_flag)

            // Basic info
            tvName.text = item.name.uppercase()
            tvPokedexNumber.text = String.format("#%03d", item.pokedexNumber)
            tvHeight.text = item.height.toString()
            tvWeight.text = item.weight.toString()

            // Types
            tvType1.text = item.type1.uppercase()
            if (item.type2 == "None") {
                tvType2.visibility = View.GONE
            } else {
                tvType2.visibility = View.VISIBLE
                tvType2.text = item.type2.uppercase()
            }

            // Stats - TextViews
            tvHp.text = item.hpStat.toString()
            tvAttack.text = item.attackStat.toString()
            tvDefense.text = item.defenseStat.toString()
            tvSpecialAttack.text = item.specialAttackStat.toString()
            tvSpecialDefense.text = item.specialDefenseStat.toString()
            tvSpeed.text = item.speedStat.toString()

            // Stats - ProgressBars
            pbHp.progress = item.hpStat
            pbAttack.progress = item.attackStat
            pbDefense.progress = item.defenseStat
            pbSpecialAttack.progress = item.specialAttackStat
            pbSpecialDefense.progress = item.specialDefenseStat
            pbSpeed.progress = item.speedStat
        }
    }
}