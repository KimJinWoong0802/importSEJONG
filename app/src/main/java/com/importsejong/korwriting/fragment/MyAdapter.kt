package com.importsejong.korwriting.fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.importsejong.korwriting.R

class MyAdapter(private val bookmarkList : ArrayList<bookmark>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.bookmark_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = bookmarkList[position]

        holder.inputsentence.text = currentitem.inputsentence
        holder.date.text = currentitem.date

        holder.itemView.setOnClickListener {
            Log.d("테스트", "허성원")
           //TODO:mypagetwo로 프래그먼트 이동
        }


    }


    override fun getItemCount(): Int {

        return bookmarkList.size
    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val inputsentence : TextView = itemView.findViewById(R.id.tvinputsentence)
        val date : TextView = itemView.findViewById(R.id.tvdate)


    }

}