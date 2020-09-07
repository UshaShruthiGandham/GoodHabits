package com.shruti.habit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shruti.habit.Habits
import com.shruti.habit.R
import kotlinx.android.synthetic.main.single_item.view.*

class MyHabitsAdapter(val habits: List<Habits>) :
    RecyclerView.Adapter<MyHabitsAdapter.MyHabitsViewHolder>() {


    class MyHabitsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHabitsViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_item, parent,false)

        return MyHabitsViewHolder(view)
    }

    // create a viewholder
    override fun onBindViewHolder(holder: MyHabitsViewHolder, position: Int) {

            holder.view.textView.text = habits[position].name
            holder.view.textView1.text = habits[position].des
            holder.view.image.setImageBitmap(habits[position].image)
    }

    override fun getItemCount() = habits.size
}


