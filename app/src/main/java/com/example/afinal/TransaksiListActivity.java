package com.example.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.adapter.TransaksiAdapter;
import com.example.afinal.api.ApiConfig;
import com.example.afinal.api.ApiService;
import com.example.afinal.helper.SessionManager;
import com.example.afinal.model.ApiResponse;
import com.example.afinal.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransaksiListActivity extends AppCompatActivity {

    private RecyclerView rvTransaksi;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;
    private ImageButton btnBack;
    private TransaksiAdapter adapter;
    private List<Transaksi> transaksiList;
    private SessionManager sessionManager;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi_list);

        sessionManager = new SessionManager(this);
        apiService = ApiConfig.getApiService();

        initViews();
        setupRecyclerView();
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTransaksi();
    }

    private void initViews() {
        rvTransaksi = findViewById(R.id.rv_transaksi);
        progressBar = findViewById(R.id.progress_bar);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupRecyclerView() {
        transaksiList = new ArrayList<>();
        adapter = new TransaksiAdapter(this, transaksiList);
        rvTransaksi.setLayoutManager(new LinearLayoutManager(this));
        rvTransaksi.setAdapter(adapter);

        adapter.setOnItemClickListener(new TransaksiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Transaksi transaksi) {
                Intent intent = new Intent(TransaksiListActivity.this, EditTransaksiActivity.class);
                intent.putExtra("id_transaksi", transaksi.getIdTransaksi());
                intent.putExtra("jenis", transaksi.getJenis());
                intent.putExtra("kategori", transaksi.getKategori());
                intent.putExtra("nominal", transaksi.getNominal());
                intent.putExtra("tanggal", transaksi.getTanggal());
                intent.putExtra("keterangan", transaksi.getKeterangan());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(Transaksi transaksi) {
                showDeleteDialog(transaksi);
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadTransaksi() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
        rvTransaksi.setVisibility(View.GONE);

        int userId = sessionManager.getUserId();

        apiService.getTransaksi(userId).enqueue(new Callback<ApiResponse<List<Transaksi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Transaksi>>> call, Response<ApiResponse<List<Transaksi>>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Transaksi>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        transaksiList = apiResponse.getData();
                        adapter.setTransaksiList(transaksiList);

                        if (transaksiList.isEmpty()) {
                            layoutEmpty.setVisibility(View.VISIBLE);
                            rvTransaksi.setVisibility(View.GONE);
                        } else {
                            layoutEmpty.setVisibility(View.GONE);
                            rvTransaksi.setVisibility(View.VISIBLE);
                        }
                    } else {
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(TransaksiListActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Transaksi>>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(TransaksiListActivity.this, "Gagal memuat transaksi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteDialog(Transaksi transaksi) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.btn_hapus))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.msg_ya), (dialog, which) -> deleteTransaksi(transaksi))
                .setNegativeButton(getString(R.string.msg_tidak), null)
                .show();
    }

    private void deleteTransaksi(Transaksi transaksi) {
        progressBar.setVisibility(View.VISIBLE);

        apiService.deleteTransaksi(transaksi.getIdTransaksi()).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    Toast.makeText(TransaksiListActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (apiResponse.isSuccess()) {
                        loadTransaksi(); // Reload list
                    }
                } else {
                    Toast.makeText(TransaksiListActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TransaksiListActivity.this, "Gagal menghapus: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
