package com.importsejong.korwriting.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.DialogPopupQuizbackBinding
import com.importsejong.korwriting.databinding.FragmentQuizGrammerBinding
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizGrammerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizGrammerFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentQuizGrammerBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

    //quizback팝업창 변수
    private var popupQuizbackBinding : DialogPopupQuizbackBinding? = null
    private var builderQuizBack : AlertDialog.Builder? = null
    private var popupViewQuizBack : AlertDialog? = null

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
        mBinding = FragmentQuizGrammerBinding.inflate(inflater, container, false)

        // TODO : 텍스트크기 변경
        setTextSize(mainActivity!!.textSize)

        //팝업 설정
        popupQuizbackBinding = DialogPopupQuizbackBinding.inflate(inflater, container, false)
        builderQuizBack = AlertDialog.Builder(requireContext()).setView(popupQuizbackBinding!!.root).setCancelable(false)
        popupViewQuizBack = builderQuizBack!!.create()

        //TODO : setButton
        setButton()

        val maxQuizNumber = 20  // TODO : DB에서 문제 개수 가져오기
        val quizNumberList:List<Int> = randomInt10(maxQuizNumber)

        //TODO : 번호에 맞는 퀴즈 가져오기
        setQuiz(quizNumberList[0])

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment QuizGrammerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizGrammerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    //겹치지 않는 랜덤숫자리스트 10개 생성
    private fun randomInt10(count: Int): List<Int>{
        val set = mutableSetOf<Int>()
        val random = Random()

        //입력값 10 이하는 -1 출력
        if(count <= 10) {
            return List(10) { -1 }
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

        //TODO : 팝업 글씨 크기 변경
    }

    private fun setButton() {
        //뒤로가기
        binding.toolbar.btnBack.setOnClickListener {
            popupViewQuizBack!!.show()
        }

        //뒤로가기 계속하기
        popupQuizbackBinding!!.txtContinue.setOnClickListener {
            popupViewQuizBack!!.dismiss()
        }

        //뒤로가기 끝내기
        popupQuizbackBinding!!.txtEnd.setOnClickListener {
            popupViewQuizBack!!.dismiss()

            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizFragment())
            transaction.commit()
        }
    }

    //번호에 맞는 퀴즈 가져오기
    private fun setQuiz(int :Int) {
        //TODO : 번호에 맞는 퀴즈 가져오기
    }
}