package com.example.afinal;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.afinal.api.ApiConfig;
import com.example.afinal.api.ApiService;
import com.example.afinal.helper.SessionManager;
import com.example.afinal.model.ApiResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;

public class TambahTransaksiActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RadioGroup rgJenis;
    private TextInputEditText etKategori, etNominal, etTanggal, etKeterangan;
    private MaterialButton btnSimpan;
    private ProgressBar progressBar;

    private SessionManager sessionManager;
    private ApiService apiService;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_transaksi);

        sessionManager = new SessionManager(this);
        apiService = ApiConfig.getApiService();
        calendar = Calendar.getInstance();

        initViews();
        setupListeners();
        setDefaultDate();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        rgJenis = findViewById(R.id.rg_jenis);
        etKategori = findViewById(R.id.et_kategori);
        etNominal = findViewById(R.id.et_nominal);
        etTanggal = findViewById(R.id.et_tanggal);
        etKeterangan = findViewById(R.id.et_keterangan);
        btnSimpan = findViewById(R.id.btn_simpan);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setDefaultDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etTanggal.setText(sdf.format(calendar.getTime()));
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

        btnSimpan.setOnClickListener(v -> simpanTransaksi());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    setDefaultDate();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void simpanTransaksi() {
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
        int userId = sessionManager.getUserId();

        apiService.addTransaksi(userId, jenis, kategori, nominal, tanggal, keterangan)
                .enqueue(new Callback<ApiResponse<Void>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<Void> apiResponse = response.body();
                            Toast.makeText(TambahTransaksiActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            if (apiResponse.isSuccess()) {
                                finish();
                            }
                        } else {
                            Toast.makeText(TambahTransaksiActivity.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                        showLoading(false);
                        Toast.makeText(TambahTransaksiActivity.this, "Gagal menyimpan: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSimpan.setEnabled(!show);
    }
}
