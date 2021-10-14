package com.importsejong.korwriting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.importsejong.korwriting.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private val binging get() = mBinding!!
    private var permissionGrant = false
    private var kakaoLogin = false

    private val requiredPermissions = arrayListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binging.root)

        //권한요청
        isPermissionGranted(requiredPermissions)
    }

    //버튼 클릭 이벤트
    fun onClick(v:View) {
        when(v.id) {
            R.id.button -> {
                kakaoLogin = true
                Toast.makeText(this, resources.getString(R.string.login_kakaologin), Toast.LENGTH_SHORT).show()
                startMainActivity(permissionGrant, kakaoLogin)
            }
        }
    }

    //카카오 로그인과 권한이 승인되면 액티비티 자동이동
    private fun startMainActivity(permissionGrant:Boolean, kakaoLogin:Boolean) {
        if(permissionGrant && kakaoLogin) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    //권한 요청
    private fun isPermissionGranted(requiredPermissions:ArrayList<String>): Boolean {
        val preference = getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        val array = arrayOfNulls<String>(requiredPermissions.size)
        var checkPermission = false

        // 승인되지 않은 권한이 존재하는지 찾기
        for (permission in requiredPermissions) {
            if(ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                checkPermission = true
            }
        }

        if (checkPermission) {
            // 거절한 권한이 존재하는지 찾기
            var permissionRationable = false
            for(permission in requiredPermissions) {
                if(ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    permissionRationable = true
                }
            }

            if (permissionRationable) {
                // 권한이 거절됨
                val snackBar = Snackbar.make(binging.root, resources.getString(R.string.login_permission1), Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this, requiredPermissions.toArray(array), 1)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    // 권한 요청을 처음 시도함
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    ActivityCompat.requestPermissions(this, requiredPermissions.toArray(array), 1)
                } else {
                    // 권한 요청을 다시 묻지 않음
                    val snackBar = Snackbar.make(binging.root, resources.getString(R.string.login_permission2), Snackbar.LENGTH_INDEFINITE)
                    snackBar.setAction("확인") {
                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivityForResult(intent, 100)
                    }
                    snackBar.show()
                }
            }
        } else {
            //권한이 전부 승인됨
            permissionGrant = true
            startMainActivity(permissionGrant, kakaoLogin)
        }

        return permissionGrant
    }

    // 팝업권한 요청결과
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //권한을 승인할 때까지 반복
        isPermissionGranted(requiredPermissions)
    }

    //설정이동 권한 요청 결과
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //권한을 승인할 때까지 반복
        isPermissionGranted(requiredPermissions)
    }
}