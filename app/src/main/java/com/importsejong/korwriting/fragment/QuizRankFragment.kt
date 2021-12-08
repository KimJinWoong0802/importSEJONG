package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentQuizGrammerBinding
import com.importsejong.korwriting.databinding.FragmentQuizRankBinding
import retrofit2.http.Query

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [QuizRankFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuizRankFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentQuizRankBinding? = null
    private val binding get() = mBinding!!
    private var mainActivity: MainActivity? = null

    private lateinit var quizrankRecyclerview : RecyclerView
    private lateinit var quizrankArrayList : ArrayList<quizrank>

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
        mBinding = FragmentQuizRankBinding.inflate(inflater, container, false)
        binding.toolbar.title.text = getString(R.string.quiz_rank_menu)

        // TODO : 텍스트크기 변경
        setTextSize(mainActivity!!.textSize)

        quizrankRecyclerview = binding.quizrankList
        quizrankRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        quizrankRecyclerview.setHasFixedSize(true)

        quizrankArrayList = arrayListOf<quizrank>()

        showQuizRank(requireContext())

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
         * @return A new instance of fragment QuizRankFragment.
         */
        //Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            QuizRankFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun showQuizRank(mContext: Context){

        databaseReference = FirebaseDatabase.getInstance().getReference().child("사용자")
        val QuizRankQuery: com.google.firebase.database.Query = databaseReference.orderByChild("rankscore")

        QuizRankQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if(snapshot.exists()){

                    for(quizrankSnapshot in snapshot.children){

                        val quizrank = quizrankSnapshot.getValue(quizrank::class.java)
                        quizrankArrayList.add(0,quizrank!!)

                    }

                    Log.d("허", quizrankArrayList.toString())

                    quizrankRecyclerview.adapter = QuizAdapter(mContext,quizrankArrayList)

                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        //TODO : 글씨 크기 변경
    }

    private fun setButton() {
        binding.toolbar.btnBack.setOnClickListener {
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, QuizFragment())
            transaction.commit()
        }
    }
}