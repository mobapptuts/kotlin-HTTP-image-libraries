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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
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

    private fun convertInputStreamToByteArray(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val byteSize = 1024
        val buffer = ByteArray(byteSize)

        while (true) {
            val remainingBytes = inputStream.read(buffer)
            if (remainingBytes < 0) {
                break
            }
            byteBuffer.write(buffer, 0, remainingBytes)
        }
        return byteBuffer.toByteArray()
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val origWidth = options.outWidth
        val origHeight = options.outHeight
        val targetWidth = imageView.layoutParams.width
        val targetHeight = imageView.layoutParams.height
        var inSampleSize = 1

        if (origWidth > targetWidth || origHeight > targetHeight) {
            val halfWidth = origWidth / 2
            val halfHeight = origHeight / 2
            while (halfWidth / inSampleSize >= targetWidth && halfHeight / inSampleSize >= targetHeight)
                inSampleSize *= 2
        }
        return inSampleSize
    }

    private fun decodeSampleBitmapFromByteArray(byteArray: ByteArray): Bitmap {
        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, option)
        option.inSampleSize = calculateInSampleSize(option)
        option.inScaled = true
        option.inDensity = option.outWidth
        option.inTargetDensity = imageView.layoutParams.width * option.inSampleSize
        option.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, option)
    }

    private fun loadImageUsingHttpUrlConnection(): Bitmap? {
        val url = URL(imageUrl)
        val urlConnection = url.openConnection() as HttpURLConnection
        var bitmap: Bitmap? = null
        try {
            val inputStream = BufferedInputStream(urlConnection.inputStream)
            val byteArray = convertInputStreamToByteArray(inputStream)
            bitmap = decodeSampleBitmapFromByteArray(byteArray)
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
