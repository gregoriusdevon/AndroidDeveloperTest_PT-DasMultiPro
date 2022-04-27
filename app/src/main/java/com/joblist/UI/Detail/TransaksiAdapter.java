package com.joblist.UI.Detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.modul_spp_ukk2021.R;
import com.example.modul_spp_ukk2021.UI.Data.Helper.Utils;
import com.example.modul_spp_ukk2021.UI.Data.Model.Transaksi;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private String strDt;
    private final Context context;
    private final List<Transaksi> listTransaksi;
    private static OnRecyclerViewItemClickListener mListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(String nama_siswa,
                           String nisn,
                           String id_pembayaran,
                           Integer jumlah_bayar,
                           Integer nominal,
                           String tanggalTagihan,
                           String tanggalBayar,
                           String status,
                           String staff,
                           String nama_kelas,
                           Integer kurang_bayar,
                           Integer download);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public TransaksiAdapter(Context context, List<Transaksi> transaksi) {
        this.context = context;
        this.listTransaksi = transaksi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pp_container_data_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaksi transaksi = listTransaksi.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        format.setMaximumFractionDigits(0);

        DateFormatSymbols symbols = new DateFormatSymbols(localeID);
        String[] monthNames = symbols.getMonths();
        holder.tvBulan.setText(monthNames[transaksi.getBulan_bayar() - 1] + " " + transaksi.getTahun_bayar());

        if (transaksi.getTgl_bayar() != null) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("d MMMM yyyy", localeID);
            strDt = simpleDate.format(transaksi.getTgl_bayar());
        }

        if (transaksi.getJumlah_bayar() < transaksi.getNominal() && transaksi.getJumlah_bayar() > 0) {
            holder.tvNominal.setText(format.format(transaksi.getKurang_bayar()));
            holder.tvStatus.setText("Kurang");
            holder.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_kurang));
        } else if (transaksi.getJumlah_bayar().equals(transaksi.getNominal())) {
            holder.tvNominal.setText(format.format(transaksi.getNominal()));
            holder.tvStatus.setText(transaksi.getStatus_bayar());
            holder.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_lunas));
        } else {
            holder.tvNominal.setText(format.format(transaksi.getNominal()));
            holder.tvStatus.setText(transaksi.getStatus_bayar());
            holder.tvStatus.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_belum));
        }

        if (transaksi.getJumlah_bayar().equals(transaksi.getNominal())) {
            holder.constraintLayout.setOnClickListener(v -> {
                Utils.preventTwoClick(v);
                mListener.onItemClicked(
                        transaksi.getNama(),
                        transaksi.getNisn(),
                        monthNames[transaksi.getBulan_bayar() - 1].substring(0, 3) + transaksi.getTahun_bayar() + transaksi.getId_pembayaran(),
                        transaksi.getJumlah_bayar(),
                        null,
                        "SPP " + monthNames[transaksi.getBulan_bayar() - 1] + " " + transaksi.getTahun_bayar(),
                        strDt,
                        transaksi.getStatus_bayar(),
                        transaksi.getNama_petugas(),
                        transaksi.getNama_kelas(),
                        transaksi.getKurang_bayar(),
                        1);
            });

        } else {
            holder.constraintLayout.setOnClickListener(v -> {
                Utils.preventTwoClick(v);
                mListener.onItemClicked(null,
                        null,
                        transaksi.getId_pembayaran(),
                        transaksi.getJumlah_bayar(),
                        transaksi.getNominal(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        transaksi.getKurang_bayar(),
                        null);
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return listTransaksi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView tvBulan, tvNominal, tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBulan = itemView.findViewById(R.id.bulan);
            tvStatus = itemView.findViewById(R.id.status);
            tvNominal = itemView.findViewById(R.id.nominal);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
