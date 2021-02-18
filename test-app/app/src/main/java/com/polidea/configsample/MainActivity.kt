package com.polidea.configsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.polidea.samplelibrary.LibConfiguration

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        R.id.appVariant.setText("${BuildConfig.FLAVOR}_${BuildConfig.BUILD_TYPE}")
        R.id.appProperty.setText(Configuration.properties.property_1)
        R.id.libVariant.setText(LibConfiguration().buildVariant)
        R.id.libProperty.setText(LibConfiguration().propertyValue)
    }

    private fun Int.setText(text: String) {
        (findViewById<TextView>(this)).text = text
    }
}
