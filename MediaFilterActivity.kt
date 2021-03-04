package com.example.mediafilter2

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit

class MediaFilterActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivityVM"

        /** The request code for requesting [Manifest.permission.READ_EXTERNAL_STORAGE] permission. */
        private const val READ_EXTERNAL_STORAGE_REQUEST = 0x1045
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_filter)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                MainActivity.PERMISSION_READ
            )
        }else{
            val images = getAllImagesFromStorage()
            Toast.makeText(this, images!!.size.toString(), Toast.LENGTH_SHORT).show()
        }

    }



         fun getAllImagesFromStorage(): MutableList<MediaImage> {
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
            return imageList
        }

}