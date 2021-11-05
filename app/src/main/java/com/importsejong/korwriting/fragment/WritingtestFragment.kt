package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentWritingtestBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WritingtestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class WritingtestFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentWritingtestBinding? = null
    private val binging get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private lateinit var storageReference: StorageReference
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = firebaseDatabase.reference

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
        mBinding = FragmentWritingtestBinding.inflate(inflater, container, false)
        binging.toolbar.title.text = resources.getString(R.string.writingtest_title_one)

        setTextSize(mainActivity!!.textSize)

        setButton()

        return binging.root
        //return inflater.inflate(R.layout.fragment_writingtest, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WritingtestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WritingtestFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //글씨 크기 변경
    fun setTextSize(textSize :Int) {
        val size20 :Float = 16.0f + textSize*2
        val size18 :Float = 14.0f + textSize*2
        val size14 :Float = 10.0f + textSize*2

        binging.textView8.textSize = size20
        binging.textView10.textSize = size20
        binging.textView12.textSize = size20
        binging.textView13.textSize = size20
        binging.textView14.textSize = size20
        binging.textView15.textSize = size20
        binging.edittxtWritingtest.textSize = size18
        binging.btnWritingtestMove.textSize = size14
    }

    private fun setButton() {
        binging.btnWritingtestMove.setOnClickListener {
            //프래그먼트 이동
            val sendText = binging.edittxtWritingtest.text.toString()
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            val fragment = GrammertestFragment()
            val bundle = Bundle()

            bundle.putInt("dataInt", 2)
            bundle.putString("dataString", sendText)
            fragment.arguments = bundle

            transaction.replace(R.id.frame, fragment)
            transaction.commit()
        }
    }
}