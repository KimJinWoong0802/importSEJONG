package com.importsejong.korwriting.fragment

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentMypageBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MypageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MypageFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentMypageBinding? = null
    private val binging get() = mBinding!!
    private var mainActivity: MainActivity? = null

    var viewId = Array(10) { -1 }

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
        mBinding = FragmentMypageBinding.inflate(inflater, container, false)
        binging.toolbar.title.text = resources.getString(R.string.mypage_menu)

        setTextSize(mainActivity!!.textSize)

        //북마크 뷰 생성
        showBookmark()

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
         * @return A new instance of fragment MypageFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MypageFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics).toInt()
    }

    private fun showBookmark() {

        val layout = binging.layBookmark
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(50f))
        lp.setMargins(0, dpToPx(10f), 0, 0)

        // TODO : 북마크 DB에서 가져오기
        for (i in 0..9) {
            val textView = TextView(requireContext()).apply {
                viewId[i] = View.generateViewId()
                id = viewId[i]
                layoutParams = lp
                setBackgroundColor(resources.getColor(R.color.gray, null))
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dpToPx(5f), 0, dpToPx(5f), 0)
                text = i.toString() + "번째 뷰"
                textSize = 10.0f + mainActivity!!.textSize* 2
                setOnClickListener {
                    //프래그먼트 이동
                    val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
                    val fragment = MypageTwoFragment()
                    val bundle = Bundle()
                    bundle.putInt("data",i)
                    fragment.arguments = bundle
                    transaction.replace(R.id.frame, fragment)
                    transaction.commit()
                }
            }
            layout.addView(textView)
        }
    }

    //글씨 크기 변경
    private fun setTextSize(textSize :Int) {
        val size20 :Float = 16.0f + textSize*2

        binging.textName.textSize = size20
    }

    private fun setButton() {

    }
}