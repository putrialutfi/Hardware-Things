package com.putrialutfi.hardwarethings;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.putrialutfi.hardwarethings.API.ApiClient;
import com.putrialutfi.hardwarethings.API.ApiInterface;
import com.putrialutfi.hardwarethings.Model.Hardwares;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EntryFragment extends android.support.v4.app.Fragment {

    private final int REQUEST_CODE = 1;

    private EditText mNama, mHarga;
    private ImageView mGambar;
    private Button mEditGambar, mSimpan;
    private ProgressDialog progressDialog;

    private String nama, harga, gambar;
    private int id;

    private Bitmap mBitmap;

    private ApiInterface mApiInterface;

    public EntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_entry, container, false);

        mNama = view.findViewById(R.id.nama_hardware);
        mHarga = view.findViewById(R.id.harga_hardware);
        mEditGambar = view.findViewById(R.id.button_edit_pict);
        mSimpan = view.findViewById(R.id.button_simpan);
        mGambar = view.findViewById(R.id.gambar);
        mEditGambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choosePict();
            }
        });

        mSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (id == 0) {
                    if (mNama.getText().toString().isEmpty() || mHarga.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Isian Tidak Boleh Kosong!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addHardware();
                        goToHomeFragment();
                    }
                }
                else {
                    if (mNama.getText().toString().isEmpty() || mHarga.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Isian Tidak Boleh Kosong!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        updateHardware(id);
                        goToHomeFragment();
                    }
                }
            }
        });

        return view;
    }

    private void goToHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void updateHardware(int id) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait a second..");
        progressDialog.show();

        String nama = mNama.getText().toString();
        String harga = mHarga.getText().toString();
        String gambar = null;
        if (mBitmap == null) {
            gambar = "";
        } else {
            gambar = getImageName(mBitmap);
        }

        mApiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Hardwares> call = mApiInterface.updateHardware(id, nama, harga, gambar);
        call.enqueue(new Callback<Hardwares>() {
            @Override
            public void onResponse(Call<Hardwares> call, Response<Hardwares> response) {
                Log.v("reports :", "updating hardware");
                String resp_code = response.body().getResp_code();
                String message = response.body().getMessage();
                if (resp_code.equals("1")) {
                    Toast.makeText(getActivity(), "Hardware is updated succesfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    Log.v("reports :", message);
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Hardwares> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed : "+t.getCause(), Toast.LENGTH_SHORT).show();
                Log.v("reports : ", t.getMessage() + "because " + t.getCause());
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void onResume() {
        super.onResume();
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getInt("id");
            nama = bundle.getString("nama");
            harga = bundle.getString("harga");
            gambar = bundle.getString("gambar");
            if (id != 0) {
                mNama.setText(nama);
                mHarga.setText(harga);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.skipMemoryCache(true);
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                requestOptions.placeholder(R.drawable.chip);
                requestOptions.error(R.drawable.chip);

                Glide.with(getActivity())
                        .load(gambar)
                        .apply(requestOptions)
                        .into(mGambar);
            }
        }
    }

    private void addHardware() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait a second..");
        progressDialog.show();

        String nama = mNama.getText().toString();
        String harga = mHarga.getText().toString();
        String gambar = null;
        if (mBitmap == null) {
            gambar = "";
        } else {
            gambar = getImageName(mBitmap);
        }

        mApiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Hardwares> call =  mApiInterface.addHardware(nama, harga, gambar);
        call.enqueue(new Callback<Hardwares>() {
            @Override
            public void onResponse(Call<Hardwares> call, Response<Hardwares> response) {
                Log.v("reports : ", "adding data");

                String resp_code = response.body().getResp_code();
                String message = response.body().getMessage();

                if(resp_code.equals("1")) {
                    Toast.makeText(getActivity(), "Data is added succesfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Hardwares> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                Log.v("reports :", t.getMessage() +"because"+ t.getCause());
                progressDialog.dismiss();
            }
        });
    }

    private String getImageName(Bitmap mBitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imagesName = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imagesName;
    }

    private void choosePict() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(Intent.createChooser(intent,"Pilih Gambar"), REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri path = data.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), path);
                mGambar = getActivity().findViewById(R.id.gambar);
                mGambar.setImageBitmap(mBitmap);
            }
            catch (IOException e){
                e.printStackTrace();
                Log.v("reports :", e.getMessage() + "because : " + e.getCause());
            }
        }
    }

}
