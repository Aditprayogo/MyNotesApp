package com.aditprayogo.mynotesapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aditprayogo.mynotesapp.adapter.NoteAdapter
import com.aditprayogo.mynotesapp.db.NoteHelper
import com.aditprayogo.mynotesapp.entity.Note
import com.aditprayogo.mynotesapp.helper.MappingHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val EXTRA_STATE = "EXTRA_STATE"
    }

    private lateinit var adapter: NoteAdapter
    private lateinit var noteHelper: NoteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = "Notes"

        rv_notes.layoutManager = LinearLayoutManager(this)
        rv_notes.setHasFixedSize(true)
        adapter = NoteAdapter(this)
        rv_notes.adapter = adapter

        noteHelper = NoteHelper.getInstance(applicationContext)
        noteHelper.open()

        if (savedInstanceState == null) {
            //proses ambil data secara async
            loadNotesAsync()
        }else {
            val list = savedInstanceState.getParcelableArrayList<Note>(EXTRA_STATE)
            if (list != null) {
                adapter.listNotes = list
            }
        }

        fab_add.setOnClickListener(this)


    }

    /*
        Mengambil data secara async
        ditampilkan dalam bentuk list
     */
    private fun loadNotesAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            progressBar.visibility = View.VISIBLE
            val defferedNotes = async(Dispatchers.IO) {
                val cursor = noteHelper.queryAll()
                /*
                    Traslate menjadi arraylist
                    dari object cursor
                 */
                MappingHelper.mapCursorToArrayList(cursor)
            }
            progressBar.visibility = View.INVISIBLE
            val notes = defferedNotes.await()
            if (notes.size > 0) {
                adapter.listNotes = notes
            }else {
                adapter.listNotes = ArrayList()
                showSnackBarMessage("Tidak ada data saat ini")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listNotes)
    }

    override fun onDestroy() {
        super.onDestroy()
        noteHelper.close()
    }

    override fun onClick(v: View) {
        when(v.id) {
            R.id.fab_add -> {
                val intent = Intent(this, NoteAddUpdateActivity::class.java)
                startActivityForResult(intent, NoteAddUpdateActivity.REQUEST_ADD)
            }
        }
    }
    /*
        Dapetin data yang di kirim lewat notes add and update
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            when(requestCode) {
                NoteAddUpdateActivity.REQUEST_ADD -> if (resultCode == NoteAddUpdateActivity.RESULT_ADD) {
                    //get data yang di kirimkan lewat notesadd
                    val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                    adapter.addItem(note)

                    rv_notes.smoothScrollToPosition(adapter.itemCount -1)
                    showSnackBarMessage("Satu Item berhasil di tambahkan")
                }
                NoteAddUpdateActivity.REQUEST_UPDATE ->
                    when(resultCode) {
                        /*
                            Akan dipanggil jika result codenya  UPDATE
                            Semua data di load kembali dari awal
                        */
                        NoteAddUpdateActivity.RESULT_UPDATE -> {
                            val note = data.getParcelableExtra<Note>(NoteAddUpdateActivity.EXTRA_NOTE)
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)

                            adapter.updateItem(position, note)
                            rv_notes.smoothScrollToPosition(position)

                            showSnackBarMessage("Data berhasil di ubah")
                        }
                        /*
                          Akan dipanggil jika result codenya DELETE
                          Delete akan menghapus data dari list berdasarkan dari position
                        */
                        NoteAddUpdateActivity.RESULT_DELETE -> {
                            val position = data.getIntExtra(NoteAddUpdateActivity.EXTRA_POSITION, 0)
                            adapter.removeItem(position)
                            showSnackBarMessage("Data berhasil di hapus")
                        }
                    }
            }
        }
    }

    private fun showSnackBarMessage(message: String){
        Snackbar.make(rv_notes, message, Snackbar.LENGTH_SHORT).show()
    }
}
