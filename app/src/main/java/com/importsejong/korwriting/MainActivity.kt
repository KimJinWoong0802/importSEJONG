package com.importsejong.korwriting

import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.importsejong.korwriting.databinding.ActivityMainBinding
import com.importsejong.korwriting.fragment.*
import java.security.MessageDigest

@RequiresApi(Build.VERSION_CODES.O)
class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var mBinding: ActivityMainBinding? = null
    private val binging get() = mBinding!!
    lateinit var kakaoId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binging.root)

        kakaoId = intent.getStringExtra("kakaoId")!!

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

    //글씨교정 프래그먼트 안에서 움직임


    fun openWritingtestFragment(int: Int, string: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        when(int) {
            2 -> {
                val fragment = GrammertestFragment()
                val bundle = Bundle()
                bundle.putInt("dataInt", 2)
                bundle.putString("dataString", string)
                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
            }
            3 -> {
                val fragment = WritingtestTwoFragment()
                val bundle = Bundle()
                bundle.putString("dataString", string)

                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
            }
            4 -> {
                transaction.replace(R.id.frame, WritingtestFragment())
            }
        }
        transaction.commit()
    }

    //앱정보 프래그먼트 안에서 움직임
    fun openSettingFragment(int: Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(int) {
            1 -> transaction.replace(R.id.frame, SettingTwoFragment())
            2 -> transaction.replace(R.id.frame, SettingFragment())
        }
        transaction.commit()
    }

    //마이페이지 프개르먼트 안에서 움직임
    fun openMypageFragment(int: Int, data: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when(int) {
            1 -> {
                val fragment = MypageTwoFragment()
                val bundle = Bundle()
                bundle.putInt("data",data)
                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
            }
            2 -> transaction.replace(R.id.frame, MypageFragment())
        }
        transaction.commit()
    }
}