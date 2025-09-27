package com.example.tridots;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.tridots.R;

public class ChooseAdTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_ad_type);

        Toolbar toolbar = findViewById(R.id.toolbarChooseAd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViewById(R.id.buttonAddRenting).setOnClickListener(v ->
                startActivity(new Intent(ChooseAdTypeActivity.this, AddRentingAdActivity.class)));

        findViewById(R.id.buttonAddVehicle).setOnClickListener(v ->
                startActivity(new Intent(ChooseAdTypeActivity.this, AddVehicleAdActivity.class)));

        findViewById(R.id.buttonAddService).setOnClickListener(v ->
                startActivity(new Intent(ChooseAdTypeActivity.this, com.example.tridots.AddServiceAdActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
