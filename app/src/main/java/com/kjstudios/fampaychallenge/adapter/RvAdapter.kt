package com.kjstudios.fampaychallenge.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kjstudios.fampaychallenge.R
import com.kjstudios.fampaychallenge.models.CardGroup

class RvAdapter(
    private val context: Context,
    private val cardGroupList: List<CardGroup>,
) :
    RecyclerView.Adapter<RvAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.nestedrv, parent, false))
    }

    private lateinit var cardGroupAdapter: CardGroupAdapter

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardGroup = cardGroupList[position]

        holder.recyclerView.apply {
            setHasFixedSize(true)
            if (cardGroup.design_type == "HC1" && cardGroup.is_scrollable == false) {
                layoutManager = GridLayoutManager(context, 2)
            } else {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            cardGroupAdapter = CardGroupAdapter(
                context,
                cardGroup.design_type,
                cardGroup.height
            )
            adapter = cardGroupAdapter
            if (!cardGroup.cards.isNullOrEmpty()) {
                cardGroupAdapter.setCards(cardGroup.cards)
            }
        }
    }

    override fun getItemCount(): Int = cardGroupList.size

    inner class ViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {
        val recyclerView: RecyclerView by lazy {
            itemView.findViewById(R.id.cardGroupRv)
        }
    }
}