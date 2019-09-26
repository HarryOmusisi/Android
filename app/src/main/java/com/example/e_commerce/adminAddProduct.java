package com.example.e_commerce;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class adminAddProduct extends AppCompatActivity
{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_product);

        Toast.makeText(this, "Welcome admin...", Toast.LENGTH_SHORT).show();
    }
}
