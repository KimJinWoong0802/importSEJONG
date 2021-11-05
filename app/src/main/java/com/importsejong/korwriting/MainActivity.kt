package com.importsejong.korwriting

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.importsejong.korwriting.databinding.ActivityMainBinding
import com.importsejong.korwriting.fragment.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var mBinding: ActivityMainBinding? = null
    private val binging get() = mBinding!!
    lateinit var kakaoId : String
    var kakaoProfile : String? = null
    var kakaoNickname : String? = null
    var textSize : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binging.root)

        kakaoId = intent.getStringExtra("kakaoId")!!
        kakaoProfile = intent.getStringExtra("kakaoProfile")
        kakaoNickname = intent.getStringExtra("kakaoNickname")

        //저장된 설정값 가져오기
        val pref=this.getPreferences(0)
        textSize=pref.getInt("textSize",2)

        binging.navigation.selectedItemId = R.id.action_3
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, MypageFragment())
        transaction.commit()

        binging.navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_1 ->{
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = GrammertestFragment()
                val bundle = Bundle()
                bundle.putInt("dataInt", 1)
                bundle.putString("dataString", "")
                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
                transaction.commit()
                return true
            }

            R.id.action_2 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, WritingtestFragment())
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

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}