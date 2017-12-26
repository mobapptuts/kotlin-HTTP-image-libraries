package com.mobapptuts.httpimagedownloader

import android.graphics.Bitmap
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://images.pexels.com/photos/163065/mobile-phone-android-apps-phone-163065.jpeg"
    private val requestQueue by lazy {
        Volley.newRequestQueue(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startChronometer()
            imageView.setImageResource(android.R.color.transparent)
            setImageViewDimensions()
            loadImageUsingVolley()
        }
    }

    private fun loadImageUsingVolley() {
        val imageRequest = ImageRequest(
                imageUrl,
                Response.Listener {
                    chronometer.stop()
                    imageView.setImageBitmap(it)
                },
                imageView.layoutParams.width, imageView.layoutParams.height,
                ImageView.ScaleType.CENTER_CROP,
                Bitmap.Config.ARGB_8888,
                Response.ErrorListener {
                    Toast.makeText(this, "Could not download image", Toast.LENGTH_SHORT).show()
                }
        )
        requestQueue.add(imageRequest)
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

}
