package com.importsejong.korwriting.fragment

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentMypageTwoBinding

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class MypageTwoFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentMypageTwoBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private lateinit var storageReference: StorageReference
    //private lateinit var firebaseDatabase: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentMypageTwoBinding.inflate(inflater, container, false)

        val date = arguments?.getString("date")

        val dateText = "${date?.substring(2,4)}.${date?.substring(6,8)}.${date?.substring(10,12)}"
        binding.toolbar.date.text = dateText

        val database_input = FirebaseDatabase.getInstance().getReference("?????????").child(mainActivity!!.kakaoId)
            .child("????????? ??????").child(date!!).child("inputsentence")
        val database_fixedpart = FirebaseDatabase.getInstance().getReference("?????????").child(mainActivity!!.kakaoId)
            .child("????????? ??????").child(date!!).child("fixedpart")
        val database_parttofix = FirebaseDatabase.getInstance().getReference("?????????").child(mainActivity!!.kakaoId)
            .child("????????? ??????").child(date!!).child("parttofix")
        val database_photourl = FirebaseDatabase.getInstance().getReference("?????????").child(mainActivity!!.kakaoId)
            .child("????????? ??????").child(date!!).child("photourl")
        val database_help = FirebaseDatabase.getInstance().getReference("?????????").child(mainActivity!!.kakaoId)
            .child("????????? ??????").child(date!!).child("help")


        database_input.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val text = snapshot.getValue().toString()
                binding.textView3.text = text

                if(text.length >= 10) {
                    binding.toolbar.title.text = text.substring(0, 10).plus("...")
                } else {
                    binding.toolbar.title.text = text
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        //parttofix
        database_parttofix.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView10.text = snapshot.getValue().toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        database_fixedpart.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView5.text = snapshot.getValue().toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        database_help.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textView.text = snapshot.getValue().toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        database_photourl.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Glide.with(this@MypageTwoFragment)
                    .load(snapshot.getValue().toString())
                    .into(binding.imageView)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



        setTextSize(mainActivity!!.textSize)

        //?????? ?????????
        setButton(date)

        return binding.root
        //return inflater.inflate(R.layout.fragment_mypage_two, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MypageTwoFragment.
         */
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MypageTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    //?????? ?????? ??????
    private fun setTextSize(textSize :Int) {
        val size20 :Float = 16.0f + textSize*2
        val size24 :Float = 20.0f + textSize*2

        binding.txtResultContent.textSize = size24
        binding.textView3.textSize = size20
        binding.textView11.textSize = size24
        binding.textView5.textSize = size20
        binding.textView24.textSize = size24
        binding.textView10.textSize = size20
        binding.textView.textSize = size20
    }

    //?????? ?????????
    private fun setButton(date : String) {
        binding.button.setOnClickListener{
            val databaseReference = FirebaseDatabase.getInstance().getReference("?????????").child(mainActivity!!.kakaoId)
                .child("????????? ??????").child(date)

            databaseReference.removeValue()

            storageReference = FirebaseStorage.getInstance().reference.child("images/${mainActivity!!.kakaoId}/????????? ??????/$date.jpg")

            storageReference.delete().addOnSuccessListener {

            }.addOnFailureListener{

            }

            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, MypageFragment())
            transaction.commit()
        }

        binding.toolbar.btnBack.setOnClickListener {
            //??????????????? ??????
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, MypageFragment())
            transaction.commit()
        }
    }
}