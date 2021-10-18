package com.importsejong.korwriting.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.LoginActivity
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentSettingBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentSettingBinding? = null
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
    ): View {
        // Inflate the layout for this fragment
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        binging.toolbar.title.text = resources.getString(R.string.setting_menu)

        setButton()

        return binging.root
        //return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setButton() {
        //앱 테마 설정 스위치
        binging.spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.itemList, android.R.layout.simple_spinner_item)
        binging.spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // TODO : 테마 전환
                when(position) {
                    //밝은 테마
                    0 -> { }
                    //어두운 테마
                    1 -> { }
                    //시스템 테마 적용
                    2 -> { }
                }
            }
        }

        //로그아웃 버튼
        binging.toolbar.logout.setOnClickListener {
            Toast.makeText(requireContext(), "카카오 로그아웃", Toast.LENGTH_SHORT).show()
            // TODO : 로그아웃 이벤트
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        //앱정보보기 버튼
        binging.txtInfo.setOnClickListener {
                mainActivity!!.openSettingFragment(1)
        }
    }
}