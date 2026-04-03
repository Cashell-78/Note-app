package com.example.smartnotereminder

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.smartnotereminder.data.Note
import com.example.smartnotereminder.data.NoteDatabase
import com.example.smartnotereminder.databinding.ActivityAddEditNoteBinding
import com.example.smartnotereminder.receiver.AlarmReceiver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditNoteBinding
    private var reminderCalendar: Calendar? = null
    private var noteId: Int = -1
    private var initialTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteId = intent.getIntExtra("NOTE_ID", -1)
        initialTitle = intent.getStringExtra("NOTE_TITLE")

        if (noteId != -1) {
            loadNote(noteId)
        } else if (initialTitle != null) {
            binding.toolbarTitle.text = initialTitle
        }

        binding.buttonBack.setOnClickListener {
            finish()
        }

        binding.buttonSetReminder.setOnClickListener {
            showDateTimePicker()
        }

        binding.buttonSaveToolbar.setOnClickListener {
            saveNote()
        }

        binding.buttonSettings.setOnClickListener {
            Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadNote(id: Int) {
        val database = NoteDatabase.getDatabase(this)
        lifecycleScope.launch {
            val note = database.noteDao().getNoteById(id)
            note?.let {
                binding.toolbarTitle.text = it.title
                binding.editTextContent.setText(it.content)
                it.reminderTime?.let { time ->
                    reminderCalendar = Calendar.getInstance().apply { timeInMillis = time }
                    updateReminderText()
                }
            }
        }
    }

    private fun showDateTimePicker() {
        val currentCalendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        reminderCalendar = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, month)
                            set(Calendar.DAY_OF_MONTH, dayOfMonth)
                            set(Calendar.HOUR_OF_DAY, hourOfDay)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                        }
                        updateReminderText()
                    },
                    currentCalendar.get(Calendar.HOUR_OF_DAY),
                    currentCalendar.get(Calendar.MINUTE),
                    false
                )
                timePickerDialog.show()
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateReminderText() {
        reminderCalendar?.let {
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            binding.textViewReminder.text = "Reminder set for: ${sdf.format(it.time)}"
        }
    }

    private fun saveNote() {
        val title = binding.toolbarTitle.text.toString().trim()
        val content = binding.editTextContent.text.toString().trim()

        val database = NoteDatabase.getDatabase(this)
        val note = Note(
            id = if (noteId == -1) 0 else noteId,
            title = title,
            content = content,
            reminderTime = reminderCalendar?.timeInMillis
        )

        lifecycleScope.launch {
            val id = if (noteId == -1) {
                database.noteDao().insertNote(note).toInt()
            } else {
                database.noteDao().updateNote(note)
                noteId
            }
            noteId = id // Update noteId for subsequent saves

            reminderCalendar?.let {
                scheduleAlarm(id, it.timeInMillis)
            }

            showSavedAnimation()
        }
    }

    private fun showSavedAnimation() {
        binding.savedToast.animate()
            .translationY(0f)
            .setDuration(300)
            .withEndAction {
                lifecycleScope.launch {
                    delay(1500)
                    binding.savedToast.animate()
                        .translationY(binding.savedToast.height.toFloat())
                        .setDuration(300)
                        .start()
                }
            }
            .start()
    }

    private fun scheduleAlarm(id: Int, timeInMillis: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            putExtra("NOTE_ID", id)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
        }
    }
}
