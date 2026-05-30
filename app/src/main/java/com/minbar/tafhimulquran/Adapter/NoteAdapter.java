package com.minbar.tafhimulquran.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.minbar.tafhimulquran.Activity.TafheemActivity;
import com.minbar.tafhimulquran.Model.NoteModel;
import com.minbar.tafhimulquran.Model.VerseModel;
import com.minbar.tafhimulquran.R;
import com.minbar.tafhimulquran.Utils.Config;
import com.minbar.tafhimulquran.Utils.NoteDatabaseHelper;
import com.minbar.tafhimulquran.Utils.SqlLiteDbHelper;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context context;
    private List<NoteModel> notes;
    private NoteDatabaseHelper noteDbHelper;

    public NoteAdapter(Context context, List<NoteModel> notes) {
        this.context = context;
        this.notes = notes;
        this.noteDbHelper = new NoteDatabaseHelper(context);
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteModel note = notes.get(position);
        holder.bind(note);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private void showNoteDialog(NoteModel note) {
        // Create dialog using Material design
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_note, null);

        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);
        EditText etNote = dialogView.findViewById(R.id.etNote);
        View btnSaveNote = dialogView.findViewById(R.id.btnSaveNote);
        View btnCloseDialog = dialogView.findViewById(R.id.btnCloseDialog);
        View btnTafseer = dialogView.findViewById(R.id.btnTafseer);

        // Set dialog title with surah name and verse
        tvDialogTitle.setText(note.getSurahName() + " - আয়াতঃ " + Config.ENtoBN(note.getVerseId()));

        // Set existing note content
        etNote.setText(note.getNote());

        AlertDialog dialog = builder.setView(dialogView).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        // Save button click
        btnSaveNote.setOnClickListener(v -> {
            String updatedNote = etNote.getText().toString().trim();
            if (!updatedNote.isEmpty()) {
                // Update note in database
                noteDbHelper.updateNote(note.getId(), updatedNote);

                // Update the note object in the list
                note.setNote(updatedNote);

                // Notify adapter of changes
                notifyItemChanged(notes.indexOf(note));
                Toast.makeText(context, "নোট আপডেট হয়েছে", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        // Close button click
        btnCloseDialog.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Tafseer button click
        btnTafseer.setOnClickListener(v -> {
            SqlLiteDbHelper dbHelper = SqlLiteDbHelper.getInstance(context);
            int surahId = Integer.parseInt(note.getSurahId());
            int verseId = Integer.parseInt(note.getVerseId());

            VerseModel verseData = dbHelper.getVerseById(surahId, verseId);

            if (verseData != null) {
                Intent intent = new Intent(context, TafheemActivity.class);
                intent.putExtra("surah_id", String.valueOf(surahId));
                intent.putExtra("verse_id", String.valueOf(verseId));
                intent.putExtra("arabicTxt", verseData.getArabic());
                intent.putExtra("transTxt", verseData.getTrans());
                intent.putExtra("banglaTxt", verseData.getBangla());
                context.startActivity(intent);
            }
            dialog.dismiss();
        });
    }

    private void showDeleteConfirmation(NoteModel note, int position) {
        new MaterialAlertDialogBuilder(context)
                .setTitle("নোট মুছে ফেলুন")
                .setMessage("আপনি কি নিশ্চিতভাবে এই নোটটি মুছে ফেলতে চান?")
                .setPositiveButton("হ্যাঁ", (dialog, which) -> {
                    noteDbHelper.deleteNote(note.getId());
                    notes.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, notes.size());
                    Toast.makeText(context, "নোটটি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("না", null)
                .show();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView tvSurah, tvVerse, tvContent;
        ImageView btnDelete;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSurah = itemView.findViewById(R.id.tvNoteSurah);
            tvVerse = itemView.findViewById(R.id.tvNoteVerse);
            tvContent = itemView.findViewById(R.id.tvNoteContent);
            btnDelete = itemView.findViewById(R.id.btnDeleteNote);
        }

        public void bind(NoteModel note) {
            tvSurah.setText(note.getSurahName());
            tvVerse.setText("আয়াতঃ " + Config.ENtoBN(note.getVerseId()));
            tvContent.setText(note.getNote());

            itemView.setOnClickListener(v -> {
                showNoteDialog(note);
            });

            btnDelete.setOnClickListener(v -> {
                showDeleteConfirmation(note, getAdapterPosition());
            });
        }
    }
}