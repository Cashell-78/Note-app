package com.example.smartnotereminder.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.smartnotereminder.data.NoteDatabase
import com.example.smartnotereminder.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra("NOTE_ID", -1)
        if (noteId == -1) return

        val database = NoteDatabase.getDatabase(context)
        val notificationHelper = NotificationHelper(context)

        CoroutineScope(Dispatchers.IO).launch {
            val note = database.noteDao().getNoteById(noteId)
            note?.let {
                notificationHelper.showNotification(it.title, it.content, it.id)
            }
        }
    }
}
