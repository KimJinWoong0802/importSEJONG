package com.importsejong.korwriting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

//파이어베이스 개수만큼 뷰 생성 및 내용넣기
//북마크2로 전달데이터 변경하기
class BookmarkActivity : AppCompatActivity() {
    var viewId = Array(10) { -1 }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bookmark)

        //setTitle
        val toolbar = findViewById<TextView>(R.id.title)
        toolbar.text = resources.getString(R.string.bookmark_title)
        val btn_back = findViewById<ImageView>(R.id.btn_back)
        btn_back.setOnClickListener{onClick(btn_back)}

        //show bookmark
        showBookmark()
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics).toInt()
    }

    private fun showBookmark() {
        val layout = findViewById<LinearLayout>(R.id.lay_bookmark)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(50f))
        lp.setMargins(0, dpToPx(10f), 0, 0)

        if (true) {
            for (i in 0..9) {
                val textView = TextView(this).apply {
                    viewId[i] = View.generateViewId()
                    id = viewId[i]
                    layoutParams = lp
                    setBackgroundColor(resources.getColor(R.color.gray, null))
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dpToPx(5f), 0, dpToPx(5f), 0)
                    text = i.toString() + "번째 뷰"
                    setOnClickListener { onClick(this) }
                }
                layout.addView(textView)
            }
        }
    }

    fun onClick(v: View) {
        when(v.id) {
            R.id.btn_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            R.id.btn_logout -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }

        for(i in 0..9) {
            if(viewId[i] == v.id) {
                val intent = Intent(this, Bookmark2Activity::class.java)
                val textView = findViewById<TextView>(v.id)
                intent.putExtra("title", textView.text.toString())
                intent.putExtra("date", "2021/09/27")
                startActivity(intent)
            }
        }
    }
}