package personal_id;

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

public class PersonalIdAdapter extends RecyclerView.Adapter<PersonalIdAdapter.ViewHolder> {

    private List<PersonalID> personalIdList;
    private DatabaseHelper databaseHelper;


    public PersonalIdAdapter(List<PersonalID> personalIdList, DatabaseHelper databaseHelper) {
        this.personalIdList = personalIdList;
        this.databaseHelper = databaseHelper;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_id_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PersonalID personalId = personalIdList.get(position);

        holder.textViewId.setText(String.valueOf(personalId.getId()));
        holder.textViewFullName.setText(personalId.getFullName());
        holder.textViewIdNumber.setText(personalId.getPersonalId());
        holder.textViewBirthDate.setText(String.valueOf(personalId.getDateOfBirth()));
        holder.textViewNationality.setText(personalId.getNationality());
        holder.textViewGender.setText(personalId.getGender());
        holder.textViewExpiryDate.setText(personalId.getDateOfExpiry());
        holder.textViewIssueDate.setText(personalId.getDateOfIssue());
        holder.textViewIssuedBy.setText(personalId.getIssuedBy());
        holder.textViewCardId.setText(personalId.getCardId());
        holder.textViewResidence.setText(personalId.getResidence());

        holder.buttonDeleteEntry.setOnClickListener(v -> {
            databaseHelper.deletePersonal_id(personalId.getId());

            personalIdList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,personalIdList.size());
        });
    }

    @Override
    public int getItemCount() {
        return personalIdList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewId, textViewFullName, textViewIdNumber, textViewBirthDate, textViewNationality, textViewExpiryDate, textViewGender, textViewIssueDate, textViewIssuedBy, textViewCardId, textViewResidence;
        Button buttonDeleteEntry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewId = itemView.findViewById(R.id.tv_id);
            textViewFullName = itemView.findViewById(R.id.tv_full_name);
            textViewIdNumber = itemView.findViewById(R.id.tv_id_number);
            textViewBirthDate = itemView.findViewById(R.id.tv_birthdate);
            textViewNationality = itemView.findViewById(R.id.tv_nationality);
            textViewExpiryDate = itemView.findViewById(R.id.tv_expiry_date);
            textViewGender = itemView.findViewById(R.id.tv_gender);
            textViewIssueDate = itemView.findViewById(R.id.tv_date_of_issue);
            textViewIssuedBy = itemView.findViewById(R.id.tv_issued_by);
            textViewCardId = itemView.findViewById(R.id.tv_card_id);
            textViewResidence = itemView.findViewById(R.id.tv_residence);
            buttonDeleteEntry = itemView.findViewById(R.id.btn_delete_entry);
        }
    }
}
