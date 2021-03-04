package com.example.mediafilter2

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.mediafilter2.databinding.ActivityTextRecognitionBinding
import com.example.mediafilter2.databinding.ImageTextRecognitionBinding
import com.example.mediafilter2.databinding.TextRecognitionBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class TextRecognitionActivity : AppCompatActivity() {

    lateinit var currentPhotoPath: String
    private lateinit var binding3: ActivityTextRecognitionBinding
    private lateinit var binding: TextRecognitionBottomSheetBinding
    private lateinit var binding1: ImageTextRecognitionBinding
    //var imageTextRecognition = binding1.imageTextRecognition
    var bottomSheetBehavior : BottomSheetBehavior<*>? = null
    companion object{

    private val REQUEST_IMAGE_CAPTURE = 1
        private const val TAG = "TextRecognition"
        private const val TAKE_PICTURE = 1
        private const val SELECT_PICTURE = 2

        private const val PERMISSION_CAMERA = 1
        private const val PERMISSION_WRITE = 2
         const val PERMISSION_READ = 3

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding3 = ActivityTextRecognitionBinding.inflate(layoutInflater)
        setContentView(binding3.root)
        binding = TextRecognitionBottomSheetBinding.inflate(layoutInflater)
        binding1 = ImageTextRecognitionBinding.inflate(layoutInflater)
        val includedView: View = binding3.textRecog.imageTextRecog
        val bottomView:View = binding3.textRecogBott.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomView)
    }




    fun cameraTextRecognition(view : View){
        //check if we have camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            //if not request it
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                PERMISSION_CAMERA
            )

            //if yes take the picture
        }else{
            dispatchTakePictureIntent()

        }
    }


    private fun dispatchTakePictureIntent() {
        //check if we have access gallery permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            //write permission is not granted lets request
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_WRITE
            )
        }else{
            //if yes take the picture
            writeOnFile()
        }

    }
    private fun writeOnFile(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Ensure that there's a camera activity to handle the intent
        intent.resolveActivity(packageManager)
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            Log.d(TAG,"exception: $ex")
            null

        }

        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.mediafilter2.fileprovider",
            photoFile!!
        )
        Log.d(TAG,"photo uri: $photoURI")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(intent, TAKE_PICTURE)
    }

    fun galleryTextRecognition(view : View){
        //request permission read if not granted for gallery
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_READ
            )
        }else{
            val selectPicture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(selectPicture, SELECT_PICTURE)
        }
    }


    private fun createImageFile() : File{

        val timeStamp = SimpleDateFormat("yyyyMMdd_hhmmss").format(Date())

        val storageDirectory : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("image_$timeStamp",".jpg", storageDirectory).apply {
            currentPhotoPath = this.absolutePath
        }
    }

   private fun runTextRecognition(bitmap : Bitmap){

        /*val image = FirebaseVisionImage.fromBitmap(bitmap)
        val recognizer = FirebaseVision.getInstance().cloudTextRecognizer*/
       /*var myBitmap: Bitmap = BitmapFactory.decodeResource(
           getApplicationContext().getResources(),
           R.drawable.history);*/
       val image = InputImage.fromBitmap(bitmap, 0)
       val recognizer = TextRecognition.getClient()
        recognizer.process(image).addOnSuccessListener {
            processTextRecognitionResult(it)
            Toast.makeText(this, "process  succeeded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "process  failed", Toast.LENGTH_SHORT).show()
        }

    }

    private fun processTextRecognitionResult(result : Text){

        val blocks = result.textBlocks

        if (blocks.size == 0){
            Toast.makeText(this, "no text recognized", Toast.LENGTH_SHORT).show()
            return
        }

        var blockText = ""
        for (block in blocks){
//            for (line in block.lines)
//                for(element in line.elements)
            blockText += block.text
        }
        bottomSheetBehavior?.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        binding3.textRecogBott.recognizedText.text = blockText
    }

    private fun rotateImage(source : Bitmap, angle : Float) : Bitmap{

        val matrix = Matrix()
        matrix.postRotate(angle)

        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            PERMISSION_CAMERA ->{
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //camera permission is granted
                    dispatchTakePictureIntent()
                }
            }
            PERMISSION_WRITE ->{
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    writeOnFile()
                }
            }
            PERMISSION_READ -> {
                //check if permission is granted
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val selectPicture = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(selectPicture, SELECT_PICTURE)
                }

            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){

            bottomSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED

            if(requestCode == TAKE_PICTURE){

                val options = BitmapFactory.Options()
                options.inPreferredConfig = Bitmap.Config.ARGB_8888
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath, options)

                val ei = ExifInterface(currentPhotoPath)

                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)

                var rotatedBitmap : Bitmap? = null

                rotatedBitmap = when(orientation){
                    ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
                    else -> bitmap
                }

                binding3.textRecog.imageTextRecognition.setImageBitmap(rotatedBitmap)
                runTextRecognition(rotatedBitmap!!)

            }else if (requestCode == SELECT_PICTURE){

                val selectedPicture = data?.data
                val selectedPictureBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedPicture!!))
                binding3.textRecog.imageTextRecognition.setImageBitmap(selectedPictureBitmap)
                runTextRecognition(selectedPictureBitmap)
            }
        }
    }


}