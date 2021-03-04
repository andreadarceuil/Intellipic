package com.example.mediafilter2

import android.net.Uri
import java.util.*


    data class MediaImage(
        val id: Long,
        val displayName: String,
        val dateAdded: Date,
        val contentUri: Uri
    )
