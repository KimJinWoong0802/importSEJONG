package com.importsejong.korwriting.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.DialogPopupGrammerquizBinding
import com.importsejong.korwriting.databinding.DialogPopupQuizbackBinding
import com.importsejong.korwriting.databinding.FragmentQuizGrammerBinding
import java.util.*



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//DB에서 받아올 문제 클래스
data class GrammerQuiz(
    var quizText :String? = null,
    var exFalse :String? = null,
    var exTrue :String? = null
)


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

    //quizback, Grammerquiz팝업창 변수
    private var popupQuizbackBinding : DialogPopupQuizbackBinding? = null
    private var builderQuizBack : AlertDialog.Builder? = null
    private var popupViewQuizBack : AlertDialog? = null

    private var popupGrammerquizBinding : DialogPopupGrammerquizBinding? = null
    private var builderGrammerquiz : AlertDialog.Builder? = null
    private var popupViewGrammerquiz : AlertDialog? = null

    private lateinit var databaseReference : DatabaseReference

    //퀴즈에서 사용되는 변수
    private var progressNumber :Int = 0 //퀴즈의 진행상황 0~9
    private var quizList = arrayListOf<GrammerQuiz>()


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

        popupGrammerquizBinding = DialogPopupGrammerquizBinding.inflate(inflater, container, false)
        builderGrammerquiz = AlertDialog.Builder(requireContext()).setView(popupGrammerquizBinding!!.root).setCancelable(false)
        popupViewGrammerquiz = builderGrammerquiz!!.create()

        setButton()

        setQuiz()

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
        //예시1
        binding.txtExample1.setOnClickListener {
            if(false) popupGrammerquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_correct)
            else popupGrammerquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_incorrect)

            popupGrammerquizBinding!!.txtAnswer.text = quizList[progressNumber].exFalse

            //TODO : 점수판

            popupViewGrammerquiz!!.show()
        }

        //예시2
        binding.txtExample2.setOnClickListener {
            if(true) popupGrammerquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_correct)
            else popupGrammerquizBinding!!.txtMain.text = getString(R.string.quiz_popup_next_incorrect)

            popupGrammerquizBinding!!.txtAnswer.text = quizList[progressNumber].exTrue

            //TODO : 점수판

            popupViewGrammerquiz!!.show()
        }

        //popupWritingquiz 팝업 버튼
        //다음문제 버튼
        popupGrammerquizBinding!!.txtNext.setOnClickListener {
            progressNumber++

            if(progressNumber <= 9) {
                binding.txtQuiz.text = quizList[progressNumber].quizText
                binding.txtExample1.text = quizList[progressNumber].exFalse
                binding.txtExample2.text = quizList[progressNumber].exTrue
                binding.txtCount.text = getString(R.string.quiz_count,progressNumber+1)

                popupViewGrammerquiz!!.dismiss()
            }
            else {
                popupViewGrammerquiz!!.dismiss()

                //TODO : 변수 보내기
                val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, QuizResultFragment())
                transaction.commit()
            }
        }
        //끝내기 버튼
        popupGrammerquizBinding!!.txtEnd.setOnClickListener {
            popupViewQuizBack!!.show()
        }

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
            popupViewGrammerquiz!!.dismiss()
            popupViewQuizBack!!.dismiss()

            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizFragment())
            transaction.commit()
        }
    }

    //번호에 맞는 퀴즈 가져오기
    private fun setQuiz(){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("퀴즈").child("우리말 맞춤법 퀴즈")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()) {

                    for (quizSnapshot in snapshot.children) {
                        val grammerquiz = quizSnapshot.getValue(GrammerQuiz::class.java)
                        quizList.add(grammerquiz!!)
                    }

                    //TODO : 예시 2개 랜덤으로 뒤섞기
                    binding.txtQuiz.text = quizList[0].quizText
                    binding.txtExample1.text = quizList[0].exFalse
                    binding.txtExample2.text = quizList[0].exTrue
                    binding.txtCount.text = getString(R.string.quiz_count,1)

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
}