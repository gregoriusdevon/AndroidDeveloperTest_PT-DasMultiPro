package com.joblist.UI.Home;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.joblist.DB.ApiEndPoints;
import com.joblist.Data.Helper.DrawerAdapter;
import com.joblist.Data.Helper.DrawerItem;
import com.joblist.Data.Helper.SimpleItem;
import com.joblist.Data.Model.Job;
import com.joblist.R;
import com.joblist.UI.Landing.LoginActivity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.joblist.DB.baseURL.url;

public class HomeActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener {
    private static final int POS_DASHBOARD = 0;
    private static final int POS_LOGOUT = 1;

    private TextView nama;
    private ApiEndPoints api;
    private HomeAdapter adapter;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private RecyclerView recyclerView;
    private FirebaseAuth mFirebaseAuth;
    private SlidingRootNav slidingRootNav;
    private SwipeRefreshLayout swipeRefreshLayout;
    private final List<Job> job = new ArrayList<>();
    private boolean doubleBackToExitPressedOnce = false;
    private LottieAnimationView emptyTransaksi, loadingProgress;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        nama = findViewById(R.id.nama);
        emptyTransaksi = findViewById(R.id.emptyTransaksi);
        EditText searchSiswa = findViewById(R.id.searchSiswa);
        loadingProgress = findViewById(R.id.loadingProgress);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mFirebaseAuth = FirebaseAuth.getInstance();
        nama.setText(getUserName());

        recyclerView = findViewById(R.id.recyclerTagihanSiswa);
        adapter = new HomeAdapter(this, job);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Refreshing();
            }
        });

        searchSiswa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 1) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    api = retrofit.create(ApiEndPoints.class);

                    Call<List<Job>> call = api.searchLocation(url + "positions.json?location=" + s.toString().trim());
                    call.enqueue(new Callback<List<Job>>() {
                        @Override
                        public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                            List<Job> job = response.body();

                            if (job.size() > 0) {
                                recyclerView.setVisibility(View.VISIBLE);
                                emptyTransaksi.pauseAnimation();
                                emptyTransaksi.setVisibility(LottieAnimationView.GONE);

                                adapter = new HomeAdapter(HomeActivity.this, job);
                                recyclerView.setAdapter(adapter);
                                runLayoutAnimation(recyclerView);

                            } else {
                                recyclerView.setVisibility(View.GONE);
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
                            recyclerView.setVisibility(View.GONE);

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
                            Toast.makeText(HomeActivity.this, "Gagal koneksi sistem, silahkan coba lagi...", Toast.LENGTH_LONG).show();
                            Log.e("DEBUG", "Error: ", t);
                        }
                    });

                } else {
                    loadDataPembayaran();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        adapter.setOnRecyclerViewItemClickListener((id) -> {
        });

        SideNavSetup();
    }

    private String getUserName() {
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user != null) {
            return user.getDisplayName();
        }
        return "error";
    }

    @Override
    public void onResume() {
        super.onResume();
        loadingProgress.setAnimation(R.raw.loading);
        loadingProgress.playAnimation();
        loadingProgress.setVisibility(LottieAnimationView.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadDataPembayaran();
    }

    @Override
    public void onBackPressed() {
        slidingRootNav.openMenu();
        if (slidingRootNav.isMenuOpened()) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan lagi untuk keluar...", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public void Refreshing() {
        swipeRefreshLayout.setRefreshing(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadDataPembayaran();
    }

    public void SideNavSetup() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withMenuLayout(R.layout.activity_sidenav)
                .withDragDistance(100)
                .withRootViewScale(0.8f)
                .withRootViewElevation(5)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_DASHBOARD).setChecked(true),
                createItemFor(POS_LOGOUT)));
        adapter.setListener(this);
        adapter.setSelected(POS_DASHBOARD);

        RecyclerView list = findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(int position) {
        if (position == POS_LOGOUT) {
            GoogleSignIn.getClient(HomeActivity.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()).signOut();
            LoginManager.getInstance().logOut();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Refreshing();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    private void loadDataPembayaran() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(ApiEndPoints.class);

        Call<List<Job>> call = api.readJobs();
        call.enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                List<Job> job = response.body();

                if (job.size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTransaksi.pauseAnimation();
                    emptyTransaksi.setVisibility(LottieAnimationView.GONE);

                    adapter = new HomeAdapter(HomeActivity.this, job);
                    recyclerView.setAdapter(adapter);
                    runLayoutAnimation(recyclerView);

                } else {
                    recyclerView.setVisibility(View.GONE);
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
                recyclerView.setVisibility(View.GONE);

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
                Toast.makeText(HomeActivity.this, "Gagal koneksi sistem, silahkan coba lagi...", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Error: ", t);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
                .withIconTint(color(R.color.grey300))
                .withTextTint(color(R.color.grey300))
                .withSelectedIconTint(color(R.color.red500))
                .withSelectedTextTint(color(R.color.red500));
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.pp_sideNavTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.pp_sideNavIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(this, id);
            }
        }
        ta.recycle();
        return icons;
    }

    @ColorInt
    private int color(@ColorRes int res) {
        return ContextCompat.getColor(this, res);
    }
}