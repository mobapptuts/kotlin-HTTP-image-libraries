package com.mobapptuts.httpimagedownloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.support.constraint.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.Ref
import org.jetbrains.anko.coroutines.experimental.asReference
import org.jetbrains.anko.coroutines.experimental.bg
import java.io.BufferedInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val imageUrl = "https://images.pexels.com/photos/163065/mobile-phone-android-apps-phone-163065.jpeg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            startChronometer()
            imageView.setImageResource(android.R.color.transparent)
            setImageViewDimensions()
            downloadBackground()
        }
    }

    private fun downloadBackground() {
        val weakRef: Ref<MainActivity> = this.asReference()
        async(UI) {
            val data: Deferred<Bitmap?> = bg {
                loadImageUsingHttpUrlConnection()
            }
            val bitmap = data.await()
            if (bitmap != null) {
                weakRef().imageView.setImageBitmap(bitmap)
                weakRef().chronometer.stop()
            }
        }
    }

    private fun loadImageUsingHttpUrlConnection(): Bitmap? {
        val url = URL(imageUrl)
        val urlConnection = url.openConnection() as HttpURLConnection
        var bitmap: Bitmap? = null
        try {
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            bitmap = BitmapFactory.decodeStream(inputStream)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            urlConnection.disconnect()
        }
        return bitmap
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
