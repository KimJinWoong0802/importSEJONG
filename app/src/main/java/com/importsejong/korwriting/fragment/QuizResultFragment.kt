package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentQuizResultBinding

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizResultFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //바인딩
    private var mBinding: FragmentQuizResultBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

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
        // Inflate the layout for this fragment
        mBinding = FragmentQuizResultBinding.inflate(inflater, container, false)

        //전달 데이터 받기
        val name = requireArguments().getString("name", "Error")
        val scoreAnswer = requireArguments().getInt("scoreAnswer", 0)
        val scoreWorng = requireArguments().getInt("scoreWorng", 0)
        val score = requireArguments().getInt("score", 0)

        var rankscore = 0

        databaseReference = FirebaseDatabase.getInstance().getReference().child("사용자").child(mainActivity!!.kakaoId)

        databaseReference.child("rankscore").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                rankscore = score + snapshot.getValue().toString().toInt()
                databaseReference.child("rankscore").setValue(rankscore)
                binding.txtScore.text = getString(R.string.quiz_result_count, scoreAnswer, scoreWorng, score, rankscore)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        val toolbarText :String = when(name) {
            "QuizGrammer" -> getString(R.string.quiz_menu_goto_quizgrammer)
            "QuizWriting" -> getString(R.string.quiz_menu_goto_quizwriting)
            else -> getString(R.string.noText)
        }

        //텍스트 변환
        binding.toolbar.title.text = toolbarText


        //텍스트크기 변경
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
         * @return A new instance of fragment QuizResultFragment.
         */
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        val size24 :Float = 20.0f + textSize*2

        binding.textView27.textSize = size24
        binding.txtScore.textSize = size24
        binding.txtGotoQuizrank.textSize = size24
        binding.txtGotoQuiz.textSize = size24
    }

    private fun setButton() {
        //종합 랭킹 보기 버튼
        binding.layGotoQuizrank.setOnClickListener {
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizRankFragment())
            transaction.commit()
        }

        //퀴즈 선택화면으로 돌아가기 버튼
        binding.txtGotoQuiz.setOnClickListener {
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizFragment())
            transaction.commit()
        }
    }
}