package com.example.afinal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.api.ApiConfig;
import com.example.afinal.api.ApiService;
import com.example.afinal.model.ApiResponse;
import com.example.afinal.model.Transaksi;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;

public class EditTransaksiActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RadioGroup rgJenis;
    private TextInputEditText etKategori, etNominal, etTanggal, etKeterangan;
    private MaterialButton btnUpdate, btnHapus;
    private ProgressBar progressBar;

    private ApiService apiService;
    private Calendar calendar;

    private int idTransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_transaksi);

        apiService = ApiConfig.getApiService();
        calendar = Calendar.getInstance();

        initViews();
        loadIntentData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rgJenis = findViewById(R.id.rg_jenis);
        etKategori = findViewById(R.id.et_kategori);
        etNominal = findViewById(R.id.et_nominal);
        etTanggal = findViewById(R.id.et_tanggal);
        etKeterangan = findViewById(R.id.et_keterangan);
        btnUpdate = findViewById(R.id.btn_update);
        btnHapus = findViewById(R.id.btn_hapus);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void loadIntentData() {
        idTransaksi = getIntent().getIntExtra("id_transaksi", 0);
        String jenis = getIntent().getStringExtra("jenis");
        String kategori = getIntent().getStringExtra("kategori");
        double nominal = getIntent().getDoubleExtra("nominal", 0);
        String tanggal = getIntent().getStringExtra("tanggal");
        String keterangan = getIntent().getStringExtra("keterangan");

        if (idTransaksi == 0) {
            Toast.makeText(this, "Data transaksi tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if ("pemasukan".equals(jenis)) {
            rgJenis.check(R.id.rb_pemasukan);
        } else {
            rgJenis.check(R.id.rb_pengeluaran);
        }

        etKategori.setText(kategori);
        
        // Remove decimal part if it's .0
        if (nominal == (long) nominal) {
             String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format((long) nominal);
             etNominal.setText(formatted);
        } else {
             etNominal.setText(String.format(Locale.getDefault(), "%s", nominal));
        }
        
        etTanggal.setText(tanggal);
        etKeterangan.setText(keterangan);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdf.parse(tanggal);
            if (date != null) {
                calendar.setTime(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        etTanggal.setOnClickListener(v -> showDatePicker());

        etNominal.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    etNominal.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");

                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getNumberInstance(new Locale("id", "ID")).format(parsed);
                        current = formatted;
                        etNominal.setText(formatted);
                        etNominal.setSelection(formatted.length());
                    } else {
                        current = "";
                        etNominal.setText("");
                    }

                    etNominal.addTextChangedListener(this);
                }
            }
        });

        btnUpdate.setOnClickListener(v -> updateTransaksi());

        btnHapus.setOnClickListener(v -> showDeleteDialog());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etTanggal.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateTransaksi() {
        String jenis = rgJenis.getCheckedRadioButtonId() == R.id.rb_pemasukan ? "pemasukan" : "pengeluaran";
        String kategori = etKategori.getText().toString().trim();
        String nominalStr = etNominal.getText().toString().trim().replaceAll("[Rp,.\\s]", "");
        String tanggal = etTanggal.getText().toString().trim();
        String keterangan = etKeterangan.getText().toString().trim();

        if (kategori.isEmpty() || nominalStr.isEmpty() || tanggal.isEmpty()) {
            Toast.makeText(this, getString(R.string.msg_fill_all), Toast.LENGTH_SHORT).show();
            return;
        }

        double nominal;
        try {
            nominal = Double.parseDouble(nominalStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Nominal tidak valid", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        Transaksi transaksi = new Transaksi();
        transaksi.setIdTransaksi(idTransaksi);
        transaksi.setJenis(jenis);
        transaksi.setKategori(kategori);
        transaksi.setNominal(nominal);
        transaksi.setTanggal(tanggal);
        transaksi.setKeterangan(keterangan);

        apiService.updateTransaksi(transaksi).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    Toast.makeText(EditTransaksiActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (apiResponse.isSuccess()) {
                        finish();
                    }
                } else {
                    Toast.makeText(EditTransaksiActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditTransaksiActivity.this, "Gagal mengupdate: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.btn_hapus))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.msg_ya), (dialog, which) -> deleteTransaksi())
                .setNegativeButton(getString(R.string.msg_tidak), null)
                .show();
    }

    private void deleteTransaksi() {
        showLoading(true);

        apiService.deleteTransaksi(idTransaksi).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    Toast.makeText(EditTransaksiActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                    if (apiResponse.isSuccess()) {
                        finish();
                    }
                } else {
                    Toast.makeText(EditTransaksiActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditTransaksiActivity.this, "Gagal menghapus: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnUpdate.setEnabled(!show);
        btnHapus.setEnabled(!show);
    }
}
