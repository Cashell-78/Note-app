package com.example.smartnotereminder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smartnotereminder.data.Note
import com.example.smartnotereminder.databinding.ItemNoteBinding
import java.text.SimpleDateFormat
import java.util.*

class NoteAdapter(private val onNoteClick: (Note) -> Unit) :
    ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class NoteViewHolder(private val binding: ItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note) {
            binding.textViewTitle.text = note.title
            binding.textViewContent.text = note.content
            
            if (note.reminderTime != null) {
                val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                binding.textViewReminderTime.text = sdf.format(Date(note.reminderTime))
                binding.textViewReminderTime.visibility = android.view.View.VISIBLE
            } else {
                binding.textViewReminderTime.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onNoteClick(note) }
        }
    }

    class NoteDiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }
}
