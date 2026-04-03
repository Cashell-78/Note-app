package com.example.smartnotereminder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.smartnotereminder.data.NoteDatabase
import com.example.smartnotereminder.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()

        val database = NoteDatabase.getDatabase(this)
        val noteDao = database.noteDao()

        lifecycleScope.launch {
            noteDao.getAllNotes().collectLatest { notes ->
                noteAdapter.submitList(notes)
            }
        }

        binding.buttonCreateNote.setOnClickListener {
            showCreateNoteDialog()
        }

        binding.buttonMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation view item clicks here.
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun showCreateNoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_note, null)
        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextNoteTitle)
        val buttonCreate = dialogView.findViewById<Button>(R.id.buttonCreate)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        buttonCreate.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            if (title.isNotEmpty()) {
                val intent = Intent(this, AddEditNoteActivity::class.java).apply {
                    putExtra("NOTE_TITLE", title)
                }
                startActivity(intent)
                dialog.dismiss()
            } else {
                editTextTitle.error = "Title cannot be empty"
            }
        }

        dialog.show()
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter { note ->
            val intent = Intent(this, AddEditNoteActivity::class.java).apply {
                putExtra("NOTE_ID", note.id)
            }
            startActivity(intent)
        }
        binding.recyclerViewNotes.apply {
            adapter = noteAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
