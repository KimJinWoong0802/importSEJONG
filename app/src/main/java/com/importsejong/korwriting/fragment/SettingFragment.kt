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

//Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    //Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mBinding: FragmentSettingBinding? = null
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
        mBinding = FragmentSettingBinding.inflate(inflater, container, false)
        binding.toolbar.title.text = resources.getString(R.string.setting_menu)

        setTextSize(mainActivity!!.textSize)
        binding.seekBar.progress = mainActivity!!.textSize

        setButton()

        return binding.root
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
        //Rename and change types and number of parameters
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
    private fun setTextSize(textSize :Int) {
        val size20 :Float = 16.0f + textSize*2
        val size24 :Float = 20.0f + textSize*2
        val size30 :Float = 26.0f + textSize*2

        binding.textView2.textSize = size20
        binding.textView3.textSize = size20
        binding.textView7.textSize = size20
        binding.textView6.textSize = size20
        binding.textView9.textSize = size20
        binding.switch1.textSize = size20
        binding.textView4.textSize = size24
        binding.textView5.textSize = size24
        binding.txtInfo.textSize = size30
    }

    private fun setButton() {
        //글씨 크기 조절 게이지 바
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
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
        binding.spinner.adapter = ArrayAdapter.createFromResource(requireContext(), R.array.itemList, android.R.layout.simple_spinner_item)
        binding.spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener {
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
        binding.toolbar.logout.setOnClickListener {
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
        binding.txtInfo.setOnClickListener {
            //프래그먼트 이동
            val transaction = mainActivity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame, SettingTwoFragment())
            transaction.commit()
        }
    }
}