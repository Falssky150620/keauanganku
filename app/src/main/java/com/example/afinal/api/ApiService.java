package com.example.afinal.api;

import com.example.afinal.model.ApiResponse;
import com.example.afinal.model.DashboardResponse;
import com.example.afinal.model.Transaksi;
import com.example.afinal.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    // ============================================
    // User Endpoints
    // ============================================

    @FormUrlEncoded
    @POST("register.php")
    Call<ApiResponse<Void>> register(
            @Field("nama") String nama,
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<ApiResponse<User>> login(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("profile.php")
    Call<ApiResponse<User>> getProfile(
            @Query("id_user") int idUser
    );

    // ============================================
    // Transaksi Endpoints
    // ============================================

    @GET("transaksi.php")
    Call<ApiResponse<List<Transaksi>>> getTransaksi(
            @Query("id_user") int idUser
    );

    @GET("transaksi.php")
    Call<ApiResponse<Transaksi>> getTransaksiById(
            @Query("id") int idTransaksi
    );

    @FormUrlEncoded
    @POST("transaksi.php")
    Call<ApiResponse<Void>> addTransaksi(
            @Field("id_user") int idUser,
            @Field("jenis") String jenis,
            @Field("kategori") String kategori,
            @Field("nominal") double nominal,
            @Field("tanggal") String tanggal,
            @Field("keterangan") String keterangan
    );

    @PUT("transaksi.php")
    Call<ApiResponse<Void>> updateTransaksi(
            @Body Transaksi transaksi
    );

    @DELETE("transaksi.php")
    Call<ApiResponse<Void>> deleteTransaksi(
            @Query("id") int idTransaksi
    );

    // ============================================
    // Dashboard Endpoint
    // ============================================

    @GET("dashboard.php")
    Call<DashboardResponse> getDashboard(
            @Query("id_user") int idUser
    );
}
