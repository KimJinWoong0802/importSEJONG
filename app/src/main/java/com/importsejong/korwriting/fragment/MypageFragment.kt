package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentMypageBinding
import com.kakao.sdk.common.util.SdkLog

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MypageFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentMypageBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private lateinit var bookmarkRecyclerview : RecyclerView
    private lateinit var bookmarkArrayList : ArrayList<bookmark>

    private lateinit var databaseReference : DatabaseReference

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
        mBinding = FragmentMypageBinding.inflate(inflater, container, false)
        binding.toolbar.title.text = getString(R.string.mypage_menu)
        setTextSize(mainActivity!!.textSize)

        bookmarkRecyclerview = binding.bookmarkList
        bookmarkRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        bookmarkRecyclerview.setHasFixedSize(true)

        bookmarkArrayList = arrayListOf<bookmark>()
        //????????? ??? ??????

        showBookmark(requireContext())



        //?????? ?????????
        setButton()

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MypageFragment.
         */
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MypageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showBookmark(mContext: Context) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("?????????").child(mainActivity!!.kakaoId)

        databaseReference.child("kakao").child("profileurl").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                Glide.with(this@MypageFragment)
                    .load(snapshot.getValue().toString())
                    .into(binding.imageProfile)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        databaseReference.child("kakao").child("nickname").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.textName.text = snapshot.getValue().toString()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


        databaseReference.child("????????? ??????").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    if(snapshot.exists()){

                        for(bookmarkSnapshot in snapshot.children){

                            val bookmark = bookmarkSnapshot.getValue(bookmark::class.java)
                            bookmarkArrayList.add(bookmark!!)

                        }

                        bookmarkRecyclerview.adapter = MyAdapter(mContext,bookmarkArrayList)

                    }


                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


    }

    //?????? ?????? ??????
    private fun setTextSize(textSize :Int) {
        val size34 :Float = 30.0f + textSize*2
        val size20 :Float = 16.0f + textSize*2

        binding.textName.textSize = size34
        binding.textView12.textSize = size20
        binding.textView13.textSize = size34
    }

    private fun setButton() {

    }
}