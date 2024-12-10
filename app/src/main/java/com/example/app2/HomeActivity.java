package com.example.app2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private ImageView creditcard;
    private ImageView memberships;
    private ImageView vouchers;
    private ImageView passwords;
    private ImageView personal_id;
    private ImageView other;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);


        creditcard=findViewById(R.id.cards);
        memberships=findViewById(R.id.memberships);
        vouchers=findViewById(R.id.vouchers);
        passwords=findViewById(R.id.password);
        personal_id=findViewById(R.id.personal_ids);
        other=findViewById(R.id.other);




    }
}
