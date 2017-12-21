package com.mobapptuts.httpimagedownloader

import android.graphics.Point
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://images.pexels.com/photos/163065/mobile-phone-android-apps-phone-163065.jpeg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startChronometer()
            imageView.setImageResource(android.R.color.transparent)
            setImageViewDimensions()
            loadImageUsingPicasso()
        }
    }

    private fun startChronometer() {
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    private fun setImageViewDimensions() {
        val aspectRatio = 2.toFloat() / 3.toFloat()
        val screenDimensions = Point()
        windowManager.defaultDisplay.getSize(screenDimensions)
        val imageViewWidth = screenDimensions.x
        val imageViewHeight = (screenDimensions.x * aspectRatio).toInt()
        val params = ConstraintLayout.LayoutParams(imageViewWidth, imageViewHeight)
        imageView.layoutParams = params
    }

    private fun loadImageUsingPicasso() {
        Picasso.with(this)
                .load(Uri.parse(imageUrl))
                .resize(imageView.layoutParams.width, imageView.layoutParams.height)
                .into(imageView, object: com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        chronometer.stop()
                    }

                    override fun onError() {
                        Toast.makeText(applicationContext, "Could not download image", Toast.LENGTH_SHORT).show()
                    }
                })
    }
}
