package com.putrialutfi.hardwarethings.API;

import com.putrialutfi.hardwarethings.Model.Hardwares;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("select_hardware.php")
    Call<ArrayList<Hardwares>> getHardwares();

    @FormUrlEncoded
    @POST("add_hardware.php")
    Call<Hardwares> addHardware(
            @Field("nama") String nama,
            @Field("harga") String harga,
            @Field("gambar") String gambar
    );

    @FormUrlEncoded
    @POST("update_hardware.php")
    Call<Hardwares> updateHardware(
            @Field("id") int id,
            @Field("nama") String nama,
            @Field("harga") String harga,
            @Field("gambar") String gambar
    );

    @FormUrlEncoded
    @POST("delete_hardware.php")
    Call<Hardwares> deleteHardware(
            @Field("id") int id,
            @Field("gambar") String gambar
    );
}
