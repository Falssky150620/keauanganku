package com.example.afinal;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.api.ApiConfig;
import com.example.afinal.api.ApiService;
import com.example.afinal.helper.SessionManager;
import com.example.afinal.model.DashboardResponse;
import com.google.android.material.card.MaterialCardView;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvGreeting, tvSaldo, tvPemasukan, tvPengeluaran;
    private MaterialCardView cardTambah, cardTransaksi;
    private ImageButton btnLogout;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        sessionManager = new SessionManager(this);
        apiService = ApiConfig.getApiService();

        initViews();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboard();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvSaldo = findViewById(R.id.tv_saldo);
        tvPemasukan = findViewById(R.id.tv_pemasukan);
        tvPengeluaran = findViewById(R.id.tv_pengeluaran);
        cardTambah = findViewById(R.id.card_tambah);
        cardTransaksi = findViewById(R.id.card_transaksi);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar);

        // Set greeting
        String nama = sessionManager.getUserName();
        tvGreeting.setText(String.format(getString(R.string.greeting), nama));
    }

    private void setupListeners() {
        cardTambah.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TambahTransaksiActivity.class));
        });

        cardTransaksi.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, TransaksiListActivity.class));
        });

        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void loadDashboard() {
        progressBar.setVisibility(View.VISIBLE);
        int userId = sessionManager.getUserId();

        apiService.getDashboard(userId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    DashboardResponse dashboard = response.body();

                    if (dashboard.isSuccess()) {
                        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

                        tvSaldo.setText(formatRupiah.format(dashboard.getSaldo()));
                        tvPemasukan.setText(formatRupiah.format(dashboard.getTotalPemasukan()));
                        tvPengeluaran.setText(formatRupiah.format(dashboard.getTotalPengeluaran()));
                    }
                } else {
                    Toast.makeText(DashboardActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DashboardActivity.this, "Gagal memuat dashboard: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.btn_logout))
                .setMessage(getString(R.string.msg_confirm_logout))
                .setPositiveButton(getString(R.string.msg_ya), (dialog, which) -> {
                    sessionManager.logout();
                    Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.msg_tidak), null)
                .show();
    }
}
