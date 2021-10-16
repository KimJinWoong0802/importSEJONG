package com.importsejong.korwriting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.importsejong.korwriting.databinding.ActivityMainBinding
import com.importsejong.korwriting.fragment.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
    private var mBinding: ActivityMainBinding? = null
    private val binging get() = mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binging.root)

        binging.navigation.selectedItemId = R.id.action_3
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, ThreeFragment())
        transaction.commit()

        binging.navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.action_1 ->{
                val transaction = supportFragmentManager.beginTransaction()
                val fragment = OneFragment()
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
                transaction.replace(R.id.frame, TwoFragment())
                transaction.commit()
                return true
            }

            R.id.action_3 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, ThreeFragment())
                transaction.commit()
                return true
            }

            R.id.action_4 ->{
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame, FourFragment())
                transaction.commit()
                return true
            }
        }
        return false
    }

    //글씨교정 프래그먼트 안에서 움직임
    fun openTwoFragment(int: Int, string: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        when(int) {
            2 -> {
                val fragment = OneFragment()
                val bundle = Bundle()
                bundle.putInt("dataInt", 2)
                bundle.putString("dataString", string)
                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
            }
            3 -> {
                val fragment = TwoTwoFragment()
                val bundle = Bundle()
                bundle.putString("dataString", string)
                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
            }
            4 -> {
                transaction.replace(R.id.frame, TwoFragment())
            }
        }
        transaction.commit()
    }

    //앱정보 프래그먼트 안에서 움직임
    fun openFourFragment(int: Int){
        val transaction = supportFragmentManager.beginTransaction()
        when(int) {
            1 -> transaction.replace(R.id.frame, FourTwoFragment())
            2 -> transaction.replace(R.id.frame, FourFragment())
        }
        transaction.commit()
    }

    //마이페이지 프개르먼트 안에서 움직임
    fun openThreeFragment(int: Int, data: Int) {
        val transaction = supportFragmentManager.beginTransaction()
        when(int) {
            1 -> {
                val fragment = ThreeTwoFragment()
                val bundle = Bundle()
                bundle.putInt("data",data)
                fragment.arguments = bundle

                transaction.replace(R.id.frame, fragment)
            }
            2 -> transaction.replace(R.id.frame, ThreeFragment())
        }
        transaction.commit()
    }
}