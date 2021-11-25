package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.databinding.FragmentQuizWritingBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizWritingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizWritingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentQuizWritingBinding? = null
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
        mBinding = FragmentQuizWritingBinding.inflate(inflater, container, false)

        // TODO : 텍스트크기 변경
        setTextSize(mainActivity!!.textSize)

        //TODO : setButton
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
         * @return A new instance of fragment QuizWritingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizWritingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //DB에서 퀴즈 가져오기
    private fun getQuizWriting() {
        //TODO : DB에서 손글씨 퀴즈 가져오기
    }

    //겹치지 않는 랜덤숫자리스트 10개 생성
    private fun listRandom(count: Int): List<Int>?{
        val set = mutableSetOf<Int>()
        val random = Random()

        //입력값 10 이하는 오류 출력
        if(count <= 10) {
            return null
        }

        while (true) {
            set.add(random.nextInt(count) + 1)

            //Set을 List로 변환, 정렬, 출력
            if(set.size >= 10) {
                val list = ArrayList(set)
                list.sort()

                return list
            }
        }
    }

    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        //TODO : 글씨 크기 변경
    }

    private fun setButton() {
        //TODO : 버튼 생성
    }
}