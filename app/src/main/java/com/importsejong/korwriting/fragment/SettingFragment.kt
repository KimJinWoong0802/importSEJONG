package com.importsejong.korwriting.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.importsejong.korwriting.LoginActivity
import com.importsejong.korwriting.MainActivity
import com.importsejong.korwriting.R
import com.importsejong.korwriting.databinding.FragmentSettingBinding
import com.kakao.sdk.user.UserApiClient

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

        setTextSize(mainActivity!!.textSize)
        binging.seekBar.progress = mainActivity!!.textSize

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

    //글씨 크기 변경
    fun setTextSize(textSize :Int) {
        val size20 :Float = 16.0f + textSize*2
        val size24 :Float = 20.0f + textSize*2
        val size30 :Float = 26.0f + textSize*2

        binging.textView2.textSize = size20
        binging.textView3.textSize = size20
        binging.textView7.textSize = size20
        binging.textView6.textSize = size20
        binging.textView9.textSize = size20
        binging.switch1.textSize = size20
        binging.textView4.textSize = size24
        binging.textView5.textSize = size24
        binging.txtInfo.textSize = size30
    }

    private fun setButton() {
        //글씨 크기 조절 게이지 바
        binging.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mainActivity!!.textSize = progress

                //텍스트크기 변경
                setTextSize(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //앱에 저장
                val pref = requireActivity().getPreferences(0)
                val editor=pref.edit()
                editor.putInt("textSize",mainActivity!!.textSize)
                    .apply()
            }
        })

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

        //TODO : 전환 애니메이션 키고 끄기

        //로그아웃 버튼
        binging.toolbar.logout.setOnClickListener {
            UserApiClient.instance.logout {error ->
                if(error != null) {
                    Toast.makeText(requireContext(), "로그아웃 실패\n$error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        //앱정보보기 버튼
        binging.txtInfo.setOnClickListener {
                mainActivity!!.openSettingFragment(1)
        }
    }
}