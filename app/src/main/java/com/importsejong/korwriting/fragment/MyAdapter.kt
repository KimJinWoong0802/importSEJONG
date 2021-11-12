package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R

class MyAdapter(val context : Context, private val bookmarkList : ArrayList<bookmark>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


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
            //프래그먼트 이동
            val mainActivity :MainActivity = context as MainActivity
            val transaction = mainActivity.supportFragmentManager.beginTransaction()
            val fragment = MypageTwoFragment()
            val bundle = Bundle()
            bundle.putString("date", currentitem.date)
            fragment.arguments = bundle

            transaction.replace(R.id.frame, fragment)
            transaction.commit()
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