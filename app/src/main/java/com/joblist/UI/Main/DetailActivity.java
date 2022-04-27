package com.joblist.UI.Main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.joblist.DB.ApiEndPoints;
import com.joblist.Data.Helper.Utils;
import com.joblist.Data.Model.Job;
import com.joblist.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.joblist.DB.baseURL.url;

public class DetailActivity extends AppCompatActivity {
    private ApiEndPoints api;
    private ConstraintLayout constraintLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView emptyTransaksi, loadingProgress;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        String id = this.getIntent().getStringExtra("id");

        ImageView refresh = findViewById(R.id.refresh);
        OptRoundCardView back = findViewById(R.id.back);
        emptyTransaksi = findViewById(R.id.emptyTransaksi);
        loadingProgress = findViewById(R.id.loadingProgress);
        constraintLayout = findViewById(R.id.constraintLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiEndPoints.class);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refreshing();
            }
        });

        back.setOnClickListener(v -> onBackPressed());

        refresh.setOnClickListener(v -> {
            Utils.preventTwoClick(v);
            PopupMenu popup = new PopupMenu(this, v, Gravity.END, R.attr.popupMenuStyle, 0);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_toolbar, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_refresh) {
                        Refreshing();
                    }
                    return true;
                }
            });
            popup.show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingProgress.setAnimation(R.raw.loading);
        loadingProgress.playAnimation();
        loadingProgress.setVisibility(LottieAnimationView.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadJob();
    }

    public void Refreshing() {
        swipeRefreshLayout.setRefreshing(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadJob();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    private void loadJob() {
        Call<List<Job>> call = api.readJobs();
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                List<Job> job = response.body();

                if (job.size() > 0) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    emptyTransaksi.pauseAnimation();
                    emptyTransaksi.setVisibility(LottieAnimationView.GONE);

                    //fill here

                } else {
                    constraintLayout.setVisibility(View.GONE);
                    emptyTransaksi.setAnimation(R.raw.nodata);
                    emptyTransaksi.playAnimation();
                    emptyTransaksi.setVisibility(LottieAnimationView.VISIBLE);
                }

                loadingProgress.pauseAnimation();
                loadingProgress.setVisibility(LottieAnimationView.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    }
                }, 700);
            }

            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                constraintLayout.setVisibility(View.GONE);

                emptyTransaksi.setAnimation(R.raw.nointernet);
                emptyTransaksi.playAnimation();
                emptyTransaksi.setVisibility(LottieAnimationView.VISIBLE);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                loadingProgress.pauseAnimation();
                loadingProgress.setVisibility(LottieAnimationView.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(DetailActivity.this, "Gagal koneksi sistem, silahkan coba lagi...", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Error: ", t);
            }
        });
    }
}