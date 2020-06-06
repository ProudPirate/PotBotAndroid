package com.plantdisease.plantdiseasedetection.ui.splash

import android.os.Bundle
import android.os.Handler
import com.plantdisease.plantdiseasedetection.base.BaseActivity
import com.plantdisease.plantdiseasedetection.R
import com.plantdisease.plantdiseasedetection.ui.main.MainActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed({
            MainActivity.start(this)
            finish()
        }, 1000)
    }
}
