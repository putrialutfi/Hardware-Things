package com.putrialutfi.hardwarethings.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.putrialutfi.hardwarethings.API.ApiClient;
import com.putrialutfi.hardwarethings.API.ApiInterface;
import com.putrialutfi.hardwarethings.EntryFragment;
import com.putrialutfi.hardwarethings.HomeFragment;
import com.putrialutfi.hardwarethings.Model.Hardwares;
import com.putrialutfi.hardwarethings.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HardwareAdapter extends RecyclerView.Adapter<HardwareAdapter.ViewHolder>{

    List<Hardwares> listHardware;
    ApiInterface mApiInterface;
    private Context context;

    public HardwareAdapter(List<Hardwares> listHardware, Context context) {
        this.listHardware = listHardware;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.mNama.setText(listHardware.get(i).getNama());
        viewHolder.mHarga.setText(listHardware.get(i).getHarga());

        RequestOptions mRequestOptions = new RequestOptions();
        mRequestOptions.skipMemoryCache(true);
        mRequestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
        mRequestOptions.placeholder(R.drawable.chip);
        mRequestOptions.error(R.drawable.chip);

        Glide.with(context)
                .load(listHardware.get(i).getGambar())
                .apply(mRequestOptions)
                .into(viewHolder.mGambar);

        viewHolder.listItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final String actions[] = {"Update", "Delete"};
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
                alertDialog.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                EntryFragment entryFragment = new EntryFragment();
                                Bundle bundle = new Bundle();
                                bundle.putInt("id", listHardware.get(i).getId());
                                bundle.putString("nama", listHardware.get(i).getNama());
                                bundle.putString("harga", listHardware.get(i).getHarga());
                                bundle.putString("gambar", listHardware.get(i).getGambar());
                                entryFragment.setArguments(bundle);

                                FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_content, entryFragment);
                                fragmentTransaction.isAddToBackStackAllowed();
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                                break;
                            case 1:
                                deleteHardware(listHardware.get(i).getId(), listHardware.get(i).getGambar());
                                break;
                        }
                    }
                });
                alertDialog.create();
                alertDialog.show();
                return true;
            }
        });
    }

    private void deleteHardware(final int id, final String gambar) {
        mApiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        new android.support.v7.app.AlertDialog.Builder(context)
                .setMessage("Hapus Data?")
                .setCancelable(false)
                .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Call<Hardwares> call =  mApiInterface.deleteHardware(id, gambar);
                        call.enqueue(new Callback<Hardwares>() {
                            @Override
                            public void onResponse(Call<Hardwares> call, Response<Hardwares> response) {
                                Log.v("reports : ", "deleting data");

                                String resp_code = response.body().getResp_code();
                                String message = response.body().getMessage();

                                if(resp_code.equals("1")) {
                                    Toast.makeText(context, "Data is deleted succesfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Hardwares> call, Throwable t) {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                                Log.v("reports :", t.getMessage() +"because"+ t.getCause());
                            }
                        });
                    }
                })
        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).create().show();
    }

    @Override
    public int getItemCount() {
        return listHardware.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNama, mHarga;
        private ImageView mGambar;
        LinearLayout listItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mNama = itemView.findViewById(R.id.nama);
            mHarga = itemView.findViewById(R.id.harga);
            mGambar = itemView.findViewById(R.id.gambar);
            listItem = itemView.findViewById(R.id.list_item);
        }
    }
}
