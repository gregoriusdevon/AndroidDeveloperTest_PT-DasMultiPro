package com.joblist.UI.Main;

import android.content.Context;
import android.content.Intent;
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

    public HomeAdapter(Context context, List<Job> job) {
        this.context = context;
        this.listJob = job;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Job job = listJob.get(position);

        holder.tvCompany.setText(job.getCompany());
        holder.tvTitle.setText(job.getTitle());
        holder.tvLocation.setText(job.getLocation());

        holder.constraintLayout.setOnClickListener(v -> {
            Utils.preventTwoClick(v);
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("id", job.getId());
            intent.putExtra("title", job.getTitle());
            intent.putExtra("company", job.getCompany());
            intent.putExtra("location", job.getLocation());
            intent.putExtra("type", job.getType());
            intent.putExtra("description", job.getDescription());
            context.startActivity(intent);
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
        TextView tvCompany, tvTitle, tvLocation;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCompany = itemView.findViewById(R.id.company);
            tvTitle = itemView.findViewById(R.id.title);
            tvLocation = itemView.findViewById(R.id.location);
            constraintLayout = itemView.findViewById(R.id.constraintLayout);
        }
    }
}
