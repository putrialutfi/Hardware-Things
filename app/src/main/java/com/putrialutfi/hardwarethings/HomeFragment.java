package com.putrialutfi.hardwarethings;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.putrialutfi.hardwarethings.API.ApiClient;
import com.putrialutfi.hardwarethings.API.ApiInterface;
import com.putrialutfi.hardwarethings.Adapter.HardwareAdapter;
import com.putrialutfi.hardwarethings.Model.Hardwares;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    int id;
    String gambar;

    private RecyclerView.LayoutManager mLayoutManager;
    private HardwareAdapter mHardwareAdapter;
    private ArrayList<Hardwares> listHardware;

    ApiInterface mApiInterface;
    RecyclerView mRecyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        swipeRefreshLayout   = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mApiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle bundle = getArguments();
        if (bundle != null) {
            id = bundle.getInt("id");
            gambar = bundle.getString("gambar");
            String deleting = bundle.getString("deleting");
            if (deleting.equals("yes")){
                deleteHardware(id, gambar);
            }
        }
        getHardwares();
    }

    @Override
    public void onResume() {
        super.onResume();
        getHardwares();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getHardwares();
    }

    private void getHardwares() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Wait a second..");
        progressDialog.show();
        Log.v("Main Activity reports ", "getting hardwares");
        Call<ArrayList<Hardwares>> call = mApiInterface.getHardwares();
        call.enqueue(new Callback<ArrayList<Hardwares>>() {
            @Override
            public void onResponse(Call<ArrayList<Hardwares>> call, Response<ArrayList<Hardwares>> response) {
                try {
                    listHardware = response.body();
                    Log.v("reports : ", response.body().toString());
                    progressDialog.dismiss();
                }
                catch (Exception e) {
                    Log.v("Main Activity reports ", e.getMessage() +" : "+ e.getCause());
                    progressDialog.dismiss();
                }
                mHardwareAdapter = new HardwareAdapter(listHardware, getActivity());
                mRecyclerView.setAdapter(mHardwareAdapter);
                mHardwareAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(retrofit2.Call<ArrayList<Hardwares>> call, Throwable t) {
                Log.v("reports : ", "ERROR! " +t.getCause() + " : " +t.getMessage().toString());
            }
        });
    }

    private void deleteHardware(final int id, final String gambar) {
        mApiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Hardwares> call =  mApiInterface.deleteHardware(id, gambar);
        call.enqueue(new Callback<Hardwares>() {
            @Override
            public void onResponse(Call<Hardwares> call, Response<Hardwares> response) {
                Log.v("reports : ", "deleting data");
                String resp_code = response.body().getResp_code();
                String message = response.body().getMessage();
                if(resp_code.equals("1")) {
                    Toast.makeText(getActivity(), "Data is deleted succesfully", Toast.LENGTH_SHORT).show();
                    } else {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Hardwares> call, Throwable t) {
                Log.v("reports :", t.getMessage() +"because"+ t.getCause());
            }
        });
        getHardwares();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        getHardwares();
        mHardwareAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
}
