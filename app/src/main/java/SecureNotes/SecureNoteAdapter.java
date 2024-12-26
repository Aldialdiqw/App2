package SecureNotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import database.DatabaseHelper;
import com.example.app2.R;

import java.util.List;


public class SecureNoteAdapter extends RecyclerView.Adapter<SecureNoteAdapter.ViewHolder> {

    private List<Note> Notes;
    private DatabaseHelper databaseHelper;

    public SecureNoteAdapter(List<Note> notes, DatabaseHelper databaseHelper) {
        this.Notes = notes;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public SecureNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.securenotesinfo, parent, false);
        return new SecureNoteAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SecureNoteAdapter.ViewHolder holder, int position) {
        Note notes = Notes.get(position);

        holder.tvN_id.setText(String.valueOf(notes.getN_id()));
        holder.tvNotetitle.setText(notes.getNotetitle());
        holder.tvNote.setText(notes.getNote());



        holder.btnDeleteMembership.setOnClickListener(v -> {
            databaseHelper.deleteNote(notes.getN_id());

            Notes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, Notes.size());
        });
    }

    @Override
    public int getItemCount() {
        return Notes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvN_id, tvNotetitle, tvNote;
        Button btnDeleteMembership;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvN_id = itemView.findViewById(R.id.et_note_id);
            tvNotetitle = itemView.findViewById(R.id.et_note_title);
            tvNote = itemView.findViewById(R.id.et_note_content);
            btnDeleteMembership = itemView.findViewById(R.id.btn_delete_card);
        }
    }

}