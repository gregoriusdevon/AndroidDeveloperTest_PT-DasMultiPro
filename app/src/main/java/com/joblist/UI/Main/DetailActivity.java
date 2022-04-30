package com.joblist.UI.Main;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.github.captain_miao.optroundcardview.OptRoundCardView;
import com.joblist.DB.ApiEndPoints;
import com.joblist.Data.Helper.Utils;
import com.joblist.Data.Model.Job;
import com.joblist.R;

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

    private void loadJob() {
        Call<Job> call = api.readDetail(url + "positions/" + getIntent().getStringExtra("id"));
        call.enqueue(new Callback<Job>() {
            @Override
            public void onResponse(Call<Job> call, Response<Job> response) {
                Job job = response.body();

                if (job != null) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    emptyTransaksi.pauseAnimation();
                    emptyTransaksi.setVisibility(LottieAnimationView.GONE);

                    ((TextView) findViewById(R.id.id)).setText("ID: #" + job.getId());
                    ((TextView) findViewById(R.id.title)).setText(job.getTitle());
                    ((TextView) findViewById(R.id.company)).setText(job.getCompany());
                    ((TextView) findViewById(R.id.location)).setText(job.getLocation());
                    ((TextView) findViewById(R.id.type)).setText(job.getType());
                    ((TextView) findViewById(R.id.title2)).setText(job.getTitle());
                    ((TextView) findViewById(R.id.description)).setText(Html.fromHtml(job.getDescription()));
                    ((TextView) findViewById(R.id.howToApply)).setText(Html.fromHtml(job.getHow_to_apply()));

                    ((TextView) findViewById(R.id.description)).setMovementMethod(LinkMovementMethod.getInstance());
                    ((TextView) findViewById(R.id.howToApply)).setMovementMethod(LinkMovementMethod.getInstance());

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
            public void onFailure(Call<Job> call, Throwable t) {
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