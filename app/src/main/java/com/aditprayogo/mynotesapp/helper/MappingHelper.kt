package com.aditprayogo.mynotesapp.helper

import android.database.Cursor
import android.provider.ContactsContract
import com.aditprayogo.mynotesapp.db.DatabaseContract
import com.aditprayogo.mynotesapp.entity.Note
import java.lang.reflect.Array.getInt

/*
 class membantu untuk translate dari object cursor menjadi array list
 */
object MappingHelper {
    fun mapCursorToArrayList(notesCursor: Cursor?): ArrayList<Note> {
        val notesList = arrayListOf<Note>()

        notesCursor?.apply {
            while (moveToNext()){
                val id = getInt(getColumnIndexOrThrow(DatabaseContract.NoteColumns._ID))
                val title = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.TITLE))
                val description = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DESCRIPTION))
                val date = getString(getColumnIndexOrThrow(DatabaseContract.NoteColumns.DATE))

                notesList.add(Note(id,title,description, date))
            }
        }
        return notesList
    }
}