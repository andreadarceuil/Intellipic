package com.example.mediafilter2

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import java.util.*
import java.util.concurrent.TimeUnit

class MediaFilterActivity : AppCompatActivity() {
    private lateinit var imageAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var recyclerView:RecyclerView

    companion object {
        private const val TAG = "MainActivityVM"
        var myDataset = mutableListOf<String>()
        var myDatasetBackup = mutableListOf<String>()
        /** The request code for requesting [Manifest.permission.READ_EXTERNAL_STORAGE] permission. */
        private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_filter)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                TextRecognitionActivity.PERMISSION_READ
            )
        }else{
           loadImages()
            Toast.makeText(this, myDataset.size.toString(), Toast.LENGTH_SHORT).show()
        }




    }

        fun loadImages(){
            myDataset = getAllImagesFromStorage(this)
            myDatasetBackup.addAll(myDataset)
            imageAdapter = ImageAdapter(this, myDataset)
            viewManager = GridLayoutManager(this, 3)
            recyclerView.apply{
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = imageAdapter
            }

        }

      fun getAllImagesFromStorage(context: Context): MutableList<String> {
            val imageList = mutableListOf<MediaImage>()


            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
            )
            val selection = null
            val selectionArgs = null
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

             applicationContext.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                while (cursor.moveToNext()) {
                    // Use an ID column from the projection to get
                    /**
                     * In order to retrieve the data from the [Cursor] that's returned, we need to
                     * find which index matches each column that we're interested in.
                     *
                     * There are two ways to do this. The first is to use the method
                     * [Cursor.getColumnIndex] which returns -1 if the column ID isn't found. This
                     * is useful if the code is programmatically choosing which columns to request,
                     * but would like to use a single method to parse them into objects.
                     *
                     * In our case, since we know exactly which columns we'd like, and we know
                     * that they must be included (since they're all supported from API 1), we'll
                     * use [Cursor.getColumnIndexOrThrow]. This method will throw an
                     * [IllegalArgumentException] if the column named isn't found.
                     *
                     * In either case, while this method isn't slow, we'll want to cache the results
                     * to avoid having to look them up for each row.
                     */
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val dateModifiedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                    Log.i(TAG, "Found ${cursor.count} images")
                    while (cursor.moveToNext()) {

                        // Here we'll use the column indexs that we found above.
                        val id = cursor.getLong(idColumn)
                        val dateModified =
                            Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                        val displayName = cursor.getString(displayNameColumn)


                        /**
                         * This is one of the trickiest parts:
                         *
                         * Since we're accessing images (using
                         * [MediaStore.Images.Media.EXTERNAL_CONTENT_URI], we'll use that
                         * as the base URI and append the ID of the image to it.
                         *
                         * This is the exact same way to do it when working with [MediaStore.Video] and
                         * [MediaStore.Audio] as well. Whatever `Media.EXTERNAL_CONTENT_URI` you
                         * query to get the items is the base, and the ID is the document to
                         * request there.
                         */
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        val image = MediaImage(id, displayName, dateModified, contentUri)
                        imageList += image

                        // For debugging, we'll output the image objects we create to logcat.
                        Log.v(TAG, "Added image: $image")
                    }
                }
            }

            Log.v(TAG, "Found ${imageList.size} images")
             val contentUris: MutableList<String> = mutableListOf()
             for (mediaImage in imageList) {

                 contentUris.add(mediaImage.contentUri.toString())

             }
            return contentUris
        }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val menuItem = menu!!.findItem(R.id.search)
            if(menuItem !=null){
              val searchView = menuItem.actionView as SearchView

                searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {

                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        println("new text is: "+newText+"ok")
                        if(newText!!.isNotEmpty() && newText !=""){

                            val search = newText.toLowerCase(Locale.getDefault())
                            myDatasetBackup.forEach{
                                textRecognitionfromStringUri(it, search)

                            }

                        }else if(newText.isNullOrBlank() || newText ==""){

                            if(myDataset.size == myDatasetBackup.size){
                                println(null)
                                println(myDataset.size)
                                println(myDatasetBackup.size)
                                return true
                            }else{
                                println(myDataset.size)
                                println(myDatasetBackup.size)
                                myDataset.clear()
                                myDataset.addAll(myDatasetBackup)
                                recyclerView.adapter!!.notifyDataSetChanged()
                            }

                        }else{
                            println("need to deal with this")
                        }
                        return true
                    }

                })
            }


        return super.onCreateOptionsMenu(menu)
    }

    fun textRecognitionfromStringUri(uri:String, search:String){
        val contentUri = uri.toUri()
        val image = InputImage.fromFilePath(applicationContext, contentUri)
        val recognizer = TextRecognition.getClient()
        recognizer.process(image).addOnSuccessListener {
            processTextRecognitionResult(it, search, uri)

            Toast.makeText(this, "process  succeeded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            processTextRecognitionResult(null, search, uri)
            Toast.makeText(this, "process  failed", Toast.LENGTH_SHORT).show()
        }
    }
    private fun processTextRecognitionResult(result : Text?, search:String, uri:String){

        val blocks = result?.textBlocks

        if (blocks?.size == 0){
            myDataset.remove(uri)
            recyclerView.adapter!!.notifyDataSetChanged()
            Toast.makeText(this, "no text recognized", Toast.LENGTH_SHORT).show()
            return
        }

        var blockText = ""
        for (block in blocks!!){
//            for (line in block.lines)
//                for(element in line.elements)
            blockText += block.text
        }
        if(!blockText.toLowerCase(Locale.getDefault()) .contains(search)){
            myDataset.remove(uri)
            //myDataset.addAll(myDatasetBackup)
            recyclerView.adapter!!.notifyDataSetChanged()
        }


    }
}


