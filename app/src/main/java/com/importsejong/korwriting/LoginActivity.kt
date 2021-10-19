package com.importsejong.korwriting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.importsejong.korwriting.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private val binging get() = mBinding!!
    private var permissionGrant = false
    private var kakaoLogin = false

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference: DatabaseReference = firebaseDatabase.reference

    private val requiredPermissions = arrayListOf(
            Manifest.permission.CAMERA
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binging.root)

        //권한요청
        isPermissionGranted(requiredPermissions)

        // 토큰 정보 보기
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error == null && tokenInfo != null) {
                //이미 로그인 되있음
                kakaoLogin = true
                startMainActivity(permissionGrant, kakaoLogin)
            }
        }

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                        Log.d("LoginError",error.toString())
                        binging.editTextTextPersonName.visibility = View.VISIBLE
                        binging.editTextTextPersonName.setText(error.toString())
                    }
                }
            }
            else if (token != null) {   //로그인 성공
                kakaoLogin = true
                startMainActivity(permissionGrant, kakaoLogin)
                // 정보 저장하기
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Toast.makeText(this, "사용자 정보 요청 실패", Toast.LENGTH_SHORT).show()
                    } else if (user != null) {
                        databaseReference.child("사용자").child("${user.id}").child("카카오").child("ID").setValue("${user.id}","ID:")
                        databaseReference.child("사용자").child("${user.id}").child("카카오").child("닉네임").setValue("${user.kakaoAccount?.profile?.nickname}","닉네임")
                        databaseReference.child("사용자").child("${user.id}").child("카카오").child("프로필URL").setValue("${user.kakaoAccount?.profile?.thumbnailImageUrl}","프로필URL")
                    }
                }
            }
        }

        binging.btnKakaoLogin.setOnClickListener {
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }

        }
    }

    //카카오 로그인과 권한이 승인되면 액티비티 자동이동
    private fun startMainActivity(permissionGrant:Boolean, kakaoLogin:Boolean) {
        val toastText = "카메라권한:"+permissionGrant.toString()+"\n카카오로그인:"+kakaoLogin.toString()
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show()

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