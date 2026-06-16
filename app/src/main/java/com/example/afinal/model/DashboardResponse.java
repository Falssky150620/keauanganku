package com.example.afinal.model;

import com.google.gson.annotations.SerializedName;

public class DashboardResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("total_pemasukan")
    private double totalPemasukan;

    @SerializedName("total_pengeluaran")
    private double totalPengeluaran;

    @SerializedName("saldo")
    private double saldo;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public double getTotalPemasukan() { return totalPemasukan; }
    public void setTotalPemasukan(double totalPemasukan) { this.totalPemasukan = totalPemasukan; }

    public double getTotalPengeluaran() { return totalPengeluaran; }
    public void setTotalPengeluaran(double totalPengeluaran) { this.totalPengeluaran = totalPengeluaran; }

    public double getSaldo() { return saldo; }
    public void setSaldo(double saldo) { this.saldo = saldo; }
}
