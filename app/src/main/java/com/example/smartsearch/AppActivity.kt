package com.example.smartsearch

import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.android.synthetic.main.activity_app.*

class AppActivity : AppCompatActivity() {
    lateinit var image : InputImage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
    }

    fun onReadImage(v: View)
    {
        var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(i, 123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 123)
        {
            var bmp = data?.extras?.get("data") as Bitmap
            //imageView.setImageBitmap(bmp)
            image = InputImage.fromBitmap(bmp, 0)

            if (image != null)
            {
                getTextRecognizer().process(image)
                    .addOnSuccessListener { visionText ->
                        convertTextToButton(visionText)

                    }
                    .addOnFailureListener { e ->
                        // Task failed with an exception
                        // ...
                    }
            }
        }
    }

    private fun convertTextToButton(myText: Text)
    {
        val layout = findViewById<LinearLayout>(R.id.buttonsScrollLayout)

        val allWords = myText.text.split(" ", "\n").toTypedArray()

        for (word in allWords)
        {
            val button = Button(this)
            // setting layout_width and layout_height using layout parameters
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button.text = word
            button.setOnClickListener { Toast.makeText(this@AppActivity, word, Toast.LENGTH_LONG).show() }
            // add Button to LinearLayout
            layout.addView(button)
        }

    }


    // Instance of text recognizer
    private fun getTextRecognizer(): TextRecognizer {
        return TextRecognition.getClient()
    }
}