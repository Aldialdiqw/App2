package creditcard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import database.DatabaseHelper;
import com.example.app2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.List;

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder> {

    private List<CreditCard> creditCards;
    private DatabaseHelper databaseHelper;

    public CreditCardAdapter(List<CreditCard> creditCards, DatabaseHelper databaseHelper) {
        this.creditCards = creditCards;
        this.databaseHelper = databaseHelper;
    }

    @NonNull
    @Override
    public CreditCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.creditcardinfo, parent, false);
        return new CreditCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreditCardViewHolder holder, int position) {
        CreditCard creditCard = creditCards.get(position);

        holder.creditId.setText(String.valueOf(creditCard.getCreditId()));
        holder.cardholderName.setText(creditCard.getCardholderName());
        holder.cardNumber.setText(creditCard.getCardNumber());
        holder.expirationDate.setText(creditCard.getExpirationDate());
        holder.cvv.setText(creditCard.getCvv());

        holder.btnDeleteCard.setOnClickListener(v -> {
            databaseHelper.deleteCard(creditCard.getCreditId());

            // Remove card from RecyclerView and notify adapter
            creditCards.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, creditCards.size());
        });
    }

    @Override
    public int getItemCount() {
        return creditCards.size();
    }

    public static class CreditCardViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText creditId, cardholderName, cardNumber, expirationDate, cvv;
        Button btnDeleteCard;

        public CreditCardViewHolder(@NonNull View itemView) {
            super(itemView);

            TextInputLayout tilCreditId = itemView.findViewById(R.id.til_card_id);
            TextInputLayout tilCardholderName = itemView.findViewById(R.id.til_cardholder_name);
            TextInputLayout tilCardNumber = itemView.findViewById(R.id.til_card_number);
            TextInputLayout tilExpirationDate = itemView.findViewById(R.id.til_expiration_date);
            TextInputLayout tilCVV = itemView.findViewById(R.id.til_cvv);

            creditId = (TextInputEditText) tilCreditId.getEditText();
            cardholderName = (TextInputEditText) tilCardholderName.getEditText();
            cardNumber = (TextInputEditText) tilCardNumber.getEditText();
            expirationDate = (TextInputEditText) tilExpirationDate.getEditText();
            cvv = (TextInputEditText) tilCVV.getEditText();
            btnDeleteCard = itemView.findViewById(R.id.btn_delete_card);
        }
    }
}