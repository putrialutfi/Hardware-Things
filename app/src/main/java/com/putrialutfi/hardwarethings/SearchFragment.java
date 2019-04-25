package com.putrialutfi.hardwarethings;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.putrialutfi.hardwarethings.API.ApiClient;
import com.putrialutfi.hardwarethings.API.ApiInterface;
import com.putrialutfi.hardwarethings.Adapter.HardwareAdapter;
import com.putrialutfi.hardwarethings.Model.Hardwares;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends android.support.v4.app.Fragment implements SearchView.OnQueryTextListener {

    SearchView mSearchView;
    ApiInterface mApiInterface;

    HardwareAdapter mHardwareAdapter;
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    ProgressDialog progressDialog;

    private ArrayList<Hardwares> listHardware;
    private ArrayList<Hardwares> searchResult;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_search, container, false);

        mSearchView = view.findViewById(R.id.search_view);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.requestFocusFromTouch();

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mApiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getHardwares();
    }

    private void getHardwares() {
        progressDialog = new ProgressDialog(getActivity());
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
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ArrayList<Hardwares>> call, Throwable t) {
                Log.v("reports : ", "ERROR! " +t.getCause() + " : " +t.getMessage().toString());
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        String textQuery =  query.toLowerCase();
        searchData(textQuery);
        if (textQuery.equals("")) {
            mSearchView.setIconified(false);
            mSearchView.requestFocus();
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String textQuery =  newText.toLowerCase();
        searchData(textQuery);
        if (textQuery.equals("")) {
            mSearchView.setIconified(false);
            mSearchView.requestFocus();
        }
        return false;
    }

    private void searchData(String keyword) {
        final List<Hardwares> resultsThings = new ArrayList<>() ;
        if (searchResult == null) {
            searchResult = listHardware;
        }
        if (keyword != null){
            if (listHardware != null & searchResult.size()>0) {
                for (final Hardwares things : searchResult) {
                    if (things.getNama().toLowerCase().contains(keyword.toString())) resultsThings.add(things);
                }
            }
            mHardwareAdapter = new HardwareAdapter(resultsThings, getActivity());
            mRecyclerView.setAdapter(mHardwareAdapter);
            mHardwareAdapter.notifyDataSetChanged();
        }
    }

}
