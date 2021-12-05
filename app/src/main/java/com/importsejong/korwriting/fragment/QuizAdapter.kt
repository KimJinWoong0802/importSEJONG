package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R

class QuizAdapter(val context : Context, private val quizrankList : ArrayList<quizrank>) : RecyclerView.Adapter<QuizAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.quizrank_item,
            parent,false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentitem = quizrankList[position]

        holder.rankscore.text = currentitem.rankscore.toString()
        holder.kakaonickname.text = currentitem.kakao!!.nickname
        holder.ranknum.text = (position+1).toString()


        Glide.with(context)
            .load(currentitem.kakao!!.profileurl)
            .into(holder.kakaoprofile)

    }


    override fun getItemCount(): Int {

        return quizrankList.size

    }


    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val rankscore : TextView = itemView.findViewById(R.id.txt_rankscore)
        val kakaonickname : TextView = itemView.findViewById(R.id.txt_kakaonickname)
        val kakaoprofile : ImageView = itemView.findViewById(R.id.image_kakaoprofile)
        val ranknum : TextView = itemView.findViewById(R.id.txt_ranknum)

    }

}