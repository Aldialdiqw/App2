package random;

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


public class RandomAdapter extends RecyclerView.Adapter<RandomAdapter.ViewHolder> {

    private List<Random> randoms;
    private DatabaseHelper databaseHelper;

    public RandomAdapter(List<Random> randoms, DatabaseHelper databaseHelper) {
        this.randoms = randoms;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public RandomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.random_info, parent, false);
        return new RandomAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RandomAdapter.ViewHolder holder, int position) {
        Random random = randoms.get(position);

        holder.tvr_id.setText(String.valueOf(random.getId()));
        holder.title.setText(random.getTitle());
        holder.description.setText(random.getDescription());


        holder.btnDeleteCard.setOnClickListener(v -> {
            databaseHelper.deleteRandom(random.getId());

            randoms.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, randoms.size());
        });
    }

    @Override
    public int getItemCount() {
        return randoms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvr_id, title, description;
        Button btnDeleteCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvr_id = itemView.findViewById(R.id.et_note_id);
            title = itemView.findViewById(R.id.tv_title);
            description = itemView.findViewById(R.id.et_description);
            btnDeleteCard = itemView.findViewById(R.id.btn_delete_card);
        }
    }
}
