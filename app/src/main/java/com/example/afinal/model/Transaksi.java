package com.example.afinal.model;

import com.google.gson.annotations.SerializedName;

public class Transaksi {

    @SerializedName("id_transaksi")
    private int idTransaksi;

    @SerializedName("id_user")
    private int idUser;

    @SerializedName("jenis")
    private String jenis;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("nominal")
    private double nominal;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("keterangan")
    private String keterangan;

    @SerializedName("created_at")
    private String createdAt;

    public Transaksi() {}

    public int getIdTransaksi() { return idTransaksi; }
    public void setIdTransaksi(int idTransaksi) { this.idTransaksi = idTransaksi; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    public String getJenis() { return jenis; }
    public void setJenis(String jenis) { this.jenis = jenis; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public double getNominal() { return nominal; }
    public void setNominal(double nominal) { this.nominal = nominal; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getKeterangan() { return keterangan; }
    public void setKeterangan(String keterangan) { this.keterangan = keterangan; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
