package com.importsejong.korwriting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Layout
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

//로그아웃시 뒤로가기 막기
class LoginActivity : AppCompatActivity() {
    val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        isLocationPermissionGranted()
    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.login_button -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        val permission = Manifest.permission.CAMERA
        val layout = findViewById<ConstraintLayout>(R.id.activity_login)
        val preference = getPreferences(Context.MODE_PRIVATE)
        val isFirstCheck = preference.getBoolean("isFirstPermissionCheck", true)
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
                // 거부만 한 경우 사용자에게 왜 필요한지 이유를 설명해주는게 좋다
                val snackBar = Snackbar.make(layout, "필요한 이유 설명", Snackbar.LENGTH_INDEFINITE)
                snackBar.setAction("권한승인") {
                    ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                }
                snackBar.show()
            } else {
                if (isFirstCheck) {
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
                    // 처음 물었는지 여부를 저장
                    preference.edit().putBoolean("isFirstPermissionCheck", false).apply()
                    // 권한요청
                    ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
                } else {
                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()
                    // 사용자가 권한을 거부하면서 다시 묻지않음 옵션을 선택한 경우
                    // requestPermission을 요청해도 창이 나타나지 않기 때문에 설정창으로 이동한다.
                    val snackBar = Snackbar.make(layout, "필요한 이유 설명", Snackbar.LENGTH_INDEFINITE)
                        snackBar.setAction("확인") {
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri
                            startActivity(intent)
                        }
                    snackBar.show()
                }
            }
            return false
        } else {
            Toast.makeText(this, "4", Toast.LENGTH_SHORT).show()
            return true
        }
    }
}