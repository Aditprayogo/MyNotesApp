package com.aditprayogo.mynotesapp.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.aditprayogo.mynotesapp.db.DatabaseContract.AUTHORITY
import com.aditprayogo.mynotesapp.db.DatabaseContract.NoteColumns.Companion.CONTENT_URI
import com.aditprayogo.mynotesapp.db.DatabaseContract.NoteColumns.Companion.TABLE_NAME
import com.aditprayogo.mynotesapp.db.NoteHelper

class NoteProvider : ContentProvider() {

    companion object {
        /**
      Integer digunakan sebagai identifier antara select all sama select by id
       */
        private const val NOTE = 1
        private const val NOTE_ID = 2

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        private lateinit var noteHelper: NoteHelper

        /**
              Uri matcher untuk mempermudah identifier dengan menggunakan integer
              misal
              uri com.dicoding.picodiploma.mynotesapp dicocokan dengan integer 1
              uri com.dicoding.picodiploma.mynotesapp/# dicocokan dengan integer 2
               */
        init {
            // content://com.dicoding.picodiploma.mynotesapp/note
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, NOTE)

            // content://com.dicoding.picodiploma.mynotesapp/note/id
            sUriMatcher.addURI(AUTHORITY,
                "$TABLE_NAME/#",
                NOTE_ID)
        }
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        val deleted: Int = when (NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        return deleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (NOTE) {
            sUriMatcher.match(uri) -> noteHelper.insert(values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun onCreate(): Boolean {
        noteHelper = NoteHelper.getInstance(context as Context)
        noteHelper.open()
        return true
    }

    /**
        Method queryAll digunakan ketika ingin menjalankan queryAll Select
        Return cursor
    */
    override fun query(
        uri: Uri, projection: Array<String>?,
        selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        /**
         * Lalu lanjut di metode query, setiap proses query,
         * insert,  update, dan delete kita wajib membuka noteHelper-nya.
         */
        val cursor: Cursor?
        when(sUriMatcher.match(uri)){
            NOTE -> cursor = noteHelper.queryAll()
            //last path segment merupakan ambil segment terakhir dari objek uri
            NOTE_ID -> cursor = noteHelper.queryById(uri.lastPathSegment.toString())
            else -> cursor = null
        }

        return cursor

    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {

        val updated: Int = when(NOTE_ID) {
            sUriMatcher.match(uri) -> noteHelper.update(uri.lastPathSegment.toString(), values)
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)

        return updated
    }
}
