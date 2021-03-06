package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentSettingTwoBinding

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingTwoFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentSettingTwoBinding? = null
    private val binding get() = mBinding!!
    var mainActivity: MainActivity? = null

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
        mBinding = FragmentSettingTwoBinding.inflate(inflater, container, false)
        binding.toolbar.title.text = resources.getString(R.string.setting_title_2)

        setTextSize(mainActivity!!.textSize)

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
         * @return A new instance of fragment SettingTwoFragment.
         */
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //?????? ?????? ??????
    private fun setTextSize(textSize :Int) {
        val size18 :Float = 14.0f + textSize*2
        val size30 :Float = 26.0f + textSize*2

        binding.textView14.textSize = size18
        binding.textView15.textSize = size30
    }

    //???????????????
    private fun setButton() {
        //???????????? ??????
        binding.textView15.setOnClickListener {
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, SettingThreeFragment())
            transaction.commit()
        }

        //????????????
        binding.toolbar.btnBack.setOnClickListener {
            //??????????????? ??????
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, SettingFragment())
            transaction.commit()
        }
    }
}