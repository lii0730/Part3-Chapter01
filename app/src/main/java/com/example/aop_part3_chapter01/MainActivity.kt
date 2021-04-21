package com.example.aop_part3_chapter01

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging


class MainActivity : AppCompatActivity() {

    private val Tag = "FirebaseService"

    private val resultTextView: TextView by lazy {
        findViewById(R.id.resultTextView)
    }

    private val tokenTextView: TextView by lazy {
        findViewById(R.id.tokenTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getToken()
        updateResult()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //TODO: 화면에서 동일한 화면을 부를때 A Activity에서 A Activity를 또 호출할 떄 onNewIntent 호출

        setIntent(intent)
        updateResult(true)
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                tokenTextView.text = task.result
                Log.i(Tag, task.result.toString())
            }
        })
    }

    private fun updateResult(isNewIntent: Boolean = false) {
        resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
                if (isNewIntent) {
                    "(으)로 갱신했습니다."
                } else {
                    "(으)로 실행했습니다."
                }
    }
}