package com.joblist.UI.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.joblist.R;
import com.joblist.Data.Model.Transaksi;
import com.google.android.material.card.MaterialCardView;

import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TagihanSiswaAdapter extends RecyclerView.Adapter<TagihanSiswaAdapter.ViewHolder> {
    private final Context context;
    private final List<Transaksi> transaksi;

    public TagihanSiswaAdapter(Context context, List<Transaksi> transaksi) {
        this.context = context;
        this.transaksi = transaksi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_container_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Transaksi transaksi = this.transaksi.get(position);

        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        format.setMaximumFractionDigits(0);

        DateFormatSymbols symbols = new DateFormatSymbols(localeID);
        String[] bulan = symbols.getMonths();

        holder.tvBulan.setText(bulan[transaksi.getBulan_bayar() - 1] + " " + transaksi.getTahun_bayar());
        holder.tvInisial.setText(transaksi.getNama_kelas().substring(0, transaksi.getNama_kelas().indexOf(' ')));

        if (transaksi.getTgl_bayar() != null) {
            SimpleDateFormat simpleDate = new SimpleDateFormat("dd/MM/yyyy", localeID);
            String strDt = simpleDate.format(transaksi.getTgl_bayar());
            holder.tvTanggal.setText(strDt);
        } else {
            holder.tvTanggal.setText("--/--/----");
        }

        if (transaksi.getKurang_bayar() == 0) {
            holder.tvStatus.setText(transaksi.getStatus_bayar());
            holder.tvNominal.setText(format.format(transaksi.getNominal()));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red500));
        } else {
            holder.tvStatus.setText("Kurang");
            holder.tvNominal.setText(format.format(transaksi.getKurang_bayar()));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorYellow));
        }
    }

    @Override
    public int getItemCount() {
        return transaksi.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvBulan, tvNominal, tvStatus, tvTanggal, tvInisial;

        public ViewHolder(View itemView) {
            super(itemView);
            tvBulan = itemView.findViewById(R.id.bulan);
            tvStatus = itemView.findViewById(R.id.status);
            tvInisial = itemView.findViewById(R.id.inisial);
            tvNominal = itemView.findViewById(R.id.nominal);
            tvTanggal = itemView.findViewById(R.id.tanggal);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}