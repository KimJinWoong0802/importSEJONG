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
import com.importsejong.korwriting.databinding.FragmentMypageTwoBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class MypageTwoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentMypageTwoBinding? = null
    private val binding get() = mBinding!!
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
        mBinding = FragmentMypageTwoBinding.inflate(inflater, container, false)

        val date = arguments?.getString("date")
        val title = getString(R.string.noText)

        binding.toolbar.date.text = date
        binding.toolbar.title.text = title

//        databaseReference.child("사용자").child(mainActivity!!.kakaoId).child("카카오").child("맞춤법 검사")
//            .get().addOnSuccessListener {
//                if(it.exists()){
//                    var test0 = it.child("test0").child("입력 문장 내용").value
//                    binging.textView3.text = test0.toString()
//                }
//            }

        setTextSize(mainActivity!!.textSize)

        //버튼 이벤트
        setButton()

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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MypageTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        val size14 :Float = 10.0f + textSize*2

        binding.textView2.textSize = size14
        binding.textView3.textSize = size14
        binding.textView4.textSize = size14
        binding.textView5.textSize = size14
        binding.textView.textSize = size14
    }

    //버튼 이벤트
    private fun setButton() {
        binding.toolbar.btnBack.setOnClickListener {
            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, MypageFragment())
            transaction.commit()
        }
    }
}