package com.example.smartsearch

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.squareup.picasso.Picasso
import org.json.JSONException
import java.io.InputStream
import java.net.URL


class AppActivity : AppCompatActivity() {
    lateinit var image : InputImage
    private var requestQueue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)
        requestQueue = Volley.newRequestQueue(this)

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
                val result = getTextRecognizer().process(image)
                    .addOnSuccessListener { visionText ->
                        handleText(visionText)
                    }
                    .addOnFailureListener { e ->

                    }

            }
        }
    }

    private fun handleText(myText: Text)
    {
        val layout = findViewById<LinearLayout>(R.id.buttonsScrollLayout)

        val allWords = myText.text.split(" ", "\n").toTypedArray()

        for (word in allWords)
        {
            val button = Button(this)
            // setting layout_width and layout_height using layout parameters
            button.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            button.text = word
            button.setOnClickListener {
                Toast.makeText(this@AppActivity, word, Toast.LENGTH_LONG).show()
                jsonParse(word)
            }
            // add Button to LinearLayout
            layout.addView(button)
        }
    }

    private fun jsonParse(string: String) {
        val url =
            "https://pixabay.com/api/?key=17493344-7e8b2b7a2997526e8a57feb9e&q=$string&image_type=photo&pretty=true"
        val request = JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                response ->try {

            val jsonArray = response.getJSONArray("hits")

            jsonArray[0]

            val item = jsonArray.getJSONObject(0)

            val url = item.getString("previewURL")

            val scrollView = findViewById<ScrollView>(R.id.scrollViewId)

            scrollView.visibility = View.INVISIBLE

            val imageView = findViewById<ImageView>(R.id.imageViewId)


            imageView.visibility = View.VISIBLE
            Picasso.get().load(url).into(imageView)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        }, Response.ErrorListener { error -> error.printStackTrace() })
        requestQueue?.add(request)
    }

    // Instance of text recognizer
    private fun getTextRecognizer(): TextRecognizer {
        return TextRecognition.getClient()
    }
}