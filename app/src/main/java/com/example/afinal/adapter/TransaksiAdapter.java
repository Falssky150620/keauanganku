package com.example.afinal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.afinal.R;
import com.example.afinal.model.Transaksi;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {

    private final Context context;
    private List<Transaksi> transaksiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaksi transaksi);
        void onItemLongClick(Transaksi transaksi);
    }

    public TransaksiAdapter(Context context, List<Transaksi> transaksiList) {
        this.context = context;
        this.transaksiList = transaksiList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTransaksiList(List<Transaksi> transaksiList) {
        this.transaksiList = transaksiList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi transaksi = transaksiList.get(position);

        // Set kategori
        holder.tvKategori.setText(transaksi.getKategori());

        // Set keterangan
        String keterangan = transaksi.getKeterangan();
        if (keterangan != null && !keterangan.isEmpty()) {
            holder.tvKeterangan.setText(keterangan);
            holder.tvKeterangan.setVisibility(View.VISIBLE);
        } else {
            holder.tvKeterangan.setVisibility(View.GONE);
        }

        // Format tanggal
        holder.tvTanggal.setText(formatTanggal(transaksi.getTanggal()));

        // Format nominal dan set warna berdasarkan jenis
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String nominalText = formatRupiah.format(transaksi.getNominal());

        if ("pemasukan".equals(transaksi.getJenis())) {
            holder.tvNominal.setText("+ " + nominalText);
            holder.tvNominal.setTextColor(ContextCompat.getColor(context, R.color.green_income));
            holder.tvJenis.setText("Pemasukan");
            holder.tvJenis.setTextColor(ContextCompat.getColor(context, R.color.green_income));
            holder.indicatorView.setBackgroundColor(ContextCompat.getColor(context, R.color.green_income));
        } else {
            holder.tvNominal.setText("- " + nominalText);
            holder.tvNominal.setTextColor(ContextCompat.getColor(context, R.color.red_expense));
            holder.tvJenis.setText("Pengeluaran");
            holder.tvJenis.setTextColor(ContextCompat.getColor(context, R.color.red_expense));
            holder.indicatorView.setBackgroundColor(ContextCompat.getColor(context, R.color.red_expense));
        }

        // Click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(transaksi);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onItemLongClick(transaksi);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transaksiList != null ? transaksiList.size() : 0;
    }

    private String formatTanggal(String tanggal) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
            Date date = inputFormat.parse(tanggal);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return tanggal;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View indicatorView;
        TextView tvKategori, tvKeterangan, tvTanggal, tvNominal, tvJenis;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            indicatorView = itemView.findViewById(R.id.view_indicator);
            tvKategori = itemView.findViewById(R.id.tv_kategori);
            tvKeterangan = itemView.findViewById(R.id.tv_keterangan);
            tvTanggal = itemView.findViewById(R.id.tv_tanggal);
            tvNominal = itemView.findViewById(R.id.tv_nominal);
            tvJenis = itemView.findViewById(R.id.tv_jenis);
        }
    }
}
