package com.importsejong.korwriting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.importsejong.korwriting.databinding.ActivityMainBinding
import com.importsejong.korwriting.fragment.*
import android.view.ViewGroup
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var mBinding: ActivityMainBinding? = null
    private val binding get() = mBinding!!
    lateinit var kakaoId : String
    var kakaoProfile : String? = null
    var kakaoNickname : String? = null
    var textSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kakaoId = intent.getStringExtra("kakaoId")!!
        kakaoProfile = intent.getStringExtra("kakaoProfile")
        kakaoNickname = intent.getStringExtra("kakaoNickname")

        //바텀네비게이션 텍스트 가운데정렬
        adjustGravity(binding.navigation)

        //저장된 설정값 가져오기
        val pref=this.getPreferences(0)
        textSize=pref.getInt("textSize",2)

        binding.navigation.selectedItemId = R.id.action_3
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, MypageFragment())
        transaction.commit()

        binding.navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_1 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, ChooseFragment())
                transaction.commit()
                return true
            }

            R.id.action_2 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, QuizFragment())
                transaction.commit()
                return true
            }

            R.id.action_3 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, MypageFragment())
                transaction.commit()
                return true
            }

            R.id.action_4 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, SettingFragment())
                transaction.commit()
                return true
            }
        }
        return false
    }

    //바텀네비게이션 텍스트 가운데정렬
    private fun adjustGravity(v: View) {
        if (v.id == com.google.android.material.R.id.smallLabel) {
            val parent = v.parent as ViewGroup
            parent.setPadding(0, 0, 0, 0)
            val params = parent.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.CENTER
            parent.layoutParams = params
        }
        if (v is ViewGroup) {
            val vg = v
            for (i in 0 until vg.childCount) {
                adjustGravity(vg.getChildAt(i))
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}