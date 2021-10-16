package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.databinding.FragmentThreeBinding
import com.importsejong.korwriting.databinding.FragmentThreeTwoBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ThreeTwoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ThreeTwoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentThreeTwoBinding? = null
    private val binging get() = mBinding!!
    private var mainActivity: MainActivity? = null

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
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentThreeTwoBinding.inflate(inflater, container, false)
        val getData = arguments?.getInt("data")

        val title = "${getData.toString()}번째 뷰"
        binging.toolbar.title.text = title


        //버튼 이벤트
        setButton()

        return binging.root
        //return inflater.inflate(R.layout.fragment_two, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ThreeTwoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ThreeTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //버튼 이벤트
    private fun setButton() {
        binging.toolbar.btnBack.setOnClickListener {
            mainActivity!!.openThreeFragment(2, -1)
        }
    }
}