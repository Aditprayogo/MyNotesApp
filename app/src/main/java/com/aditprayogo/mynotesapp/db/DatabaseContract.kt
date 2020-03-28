package com.aditprayogo.mynotesapp.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORY = "com.aditprayogo.mynotesapp"
    const val SCHEME = "content"

    internal class NoteColumns : BaseColumns {

        companion object {
            const val TABLE_NAME = "note"
            const val _ID = "_id"
            const val TITLE = "title"
            const val DESCRIPTION = "description"
            const val DATE = "date"

            /**
             * Untuk membuat URI content
             */
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}