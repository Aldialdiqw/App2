package memberships;

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
import java.util.Locale;

public class MembershipAdapter extends RecyclerView.Adapter<MembershipAdapter.ViewHolder> {

    private List<MembershipInfo> memberships;
    private DatabaseHelper databaseHelper;

    public MembershipAdapter(List<MembershipInfo> memberships, DatabaseHelper databaseHelper) {
        this.memberships = memberships;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.membershipinfo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MembershipInfo membership = memberships.get(position);

        holder.tvMembershipId.setText(String.valueOf(membership.getId()));
        holder.tvMembershipName.setText(membership.getName());
        holder.tvCompanyName.setText(membership.getCompanyName());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "%.2f", membership.getPrice()));
        holder.tvPaymentDate.setText(membership.getPaymentDate());

        holder.btnDeleteMembership.setOnClickListener(v -> {
            databaseHelper.deleteMembership(membership.getId());

            memberships.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, memberships.size());
        });
    }

    @Override
    public int getItemCount() {
        return memberships.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMembershipId, tvMembershipName, tvCompanyName, tvPrice, tvPaymentDate;
        Button btnDeleteMembership;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMembershipId = itemView.findViewById(R.id.tv_membership_id);
            tvMembershipName = itemView.findViewById(R.id.tv_membership_name);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPaymentDate = itemView.findViewById(R.id.tv_payment_date);
            btnDeleteMembership = itemView.findViewById(R.id.btn_delete_membership);
        }
    }

}

