package passwords;

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


public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

private List<Service> service;
private DatabaseHelper databaseHelper;

public ServiceAdapter(List<Service> service, DatabaseHelper databaseHelper) {
    this.service = service;
    this.databaseHelper = databaseHelper;
}

@NonNull
@Override
public ServiceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.passwordinfo, parent, false);
    return new ServiceAdapter.ViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull ServiceAdapter.ViewHolder holder, int position) {
    Service Service = service.get(position);

    holder.tvPasswordId.setText(String.valueOf(Service.getP_id()));
    holder.tvServiceName.setText(Service.getService_name());
    holder.tvUsername.setText(Service.getUsername());
    holder.tvPassword.setText(Service.getPassword());


    holder.btnDeleteMembership.setOnClickListener(v -> {
        databaseHelper.deleteMembership(Service.getP_id());

        service.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, service.size());
    });
}

@Override
public int getItemCount() {
    return service.size();
}

public static class ViewHolder extends RecyclerView.ViewHolder {
    TextView tvPasswordId, tvServiceName, tvUsername, tvPassword;
    Button btnDeleteMembership;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);

        tvPasswordId = itemView.findViewById(R.id.tv_password_id);
        tvServiceName = itemView.findViewById(R.id.tv_service_name);
        tvUsername = itemView.findViewById(R.id.tv_username);
        tvPassword = itemView.findViewById(R.id.tv_password);

        btnDeleteMembership = itemView.findViewById(R.id.btn_delete_entry);
    }
}

}

