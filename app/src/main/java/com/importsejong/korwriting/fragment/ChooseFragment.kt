package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentChooseBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentChooseBinding? = null
    private val binding get() = mBinding!!
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
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentChooseBinding.inflate(inflater, container, false)
        binding.txtTitle.text = mainActivity!!.kakaoNickname.plus(getString(R.string.choose_title))

        // TODO : 텍스트크기 변경
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
         * @return A new instance of fragment ChooseFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChooseFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {

    }

    private fun setButton() {
        //맞춤법 검사하기 버튼
        binding.txtGotoGrammertest.setOnClickListener {
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            val fragment = GrammertestFragment()
            val bundle = Bundle()
            bundle.putInt("dataInt", 1)
            bundle.putString("dataString", "")
            fragment.arguments = bundle

            transaction.replace(R.id.frame, fragment)
            transaction.commit()
        }

        //손글씨 교정하기 버튼
        binding.txtGotoWritingtest.setOnClickListener {
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, WritingtestFragment())
            transaction.commit()
        }
    }


}