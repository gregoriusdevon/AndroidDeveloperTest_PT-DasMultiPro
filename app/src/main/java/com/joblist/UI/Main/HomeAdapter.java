package com.joblist.UI.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.joblist.Data.Helper.Utils;
import com.joblist.Data.Model.Job;
import com.joblist.R;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    private final Context context;
    private final List<Job> listJob;
    private static OnRecyclerViewItemClickListener mListener;

    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(String id);
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener listener) {
        mListener = listener;
    }

    public HomeAdapter(Context context, List<Job> job) {
        this.context = context;
        this.listJob = job;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ps_container_transaksi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Job job = listJob.get(position);

        holder.tvCompany.setText(job.getCompany());
        holder.tvTitle.setText(job.getTitle());
        holder.tvType.setText(job.getType());

        holder.constraintLayout.setOnClickListener(v -> {
            Utils.preventTwoClick(v);
            mListener.onItemClicked(job.getId());
        });
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
        return listJob.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        TextView tvCompany, tvTitle, tvType;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCompany = itemView.findViewById(R.id.company);
            tvTitle = itemView.findViewById(R.id.title);
            tvType = itemView.findViewById(R.id.type);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
