package com.joblist.UI.Main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.joblist.R;
import com.joblist.DB.ApiEndPoints;
import com.joblist.Data.Model.Transaksi;
import com.joblist.Data.Repository.TransaksiRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.joblist.DB.baseURL.url;

public class RiwayatSiswaFragment extends Fragment {
    private String nisnSiswa;
    private TextView tagihan_count;
    private RecyclerView recyclerView;
    private RiwayatSiswaAdapter adapter;
    private LottieAnimationView lottieAnim;
    private final List<Transaksi> transaksi = new ArrayList<>();

    public RiwayatSiswaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.ps_fragment_riwayat, container, false);
        nisnSiswa = requireActivity().getSharedPreferences("myprefs", Context.MODE_PRIVATE).getString("nisnSiswa", null);

        lottieAnim = view.findViewById(R.id.lottieAnim);
        tagihan_count = view.findViewById(R.id.riwayat_count);

        adapter = new RiwayatSiswaAdapter(getActivity(), transaksi);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.recyclerRiwayat);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ((HomeActivity) requireActivity()).setHistoryRefreshListener(new HomeActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                loadDataHistory();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataHistory();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(), R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    private void loadDataHistory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiEndPoints api = retrofit.create(ApiEndPoints.class);

        Call<TransaksiRepository> call = api.readRiwayat(nisnSiswa);
        call.enqueue(new Callback<TransaksiRepository>() {
            @Override
            public void onResponse(Call<TransaksiRepository> call, Response<TransaksiRepository> response) {
                String value = response.body().getValue();
                List<Transaksi> results = response.body().getResult();
                String message = response.body().getMessage();

                if (value.equals("1")) {
                    recyclerView.setVisibility(View.VISIBLE);
                    lottieAnim.pauseAnimation();
                    lottieAnim.setVisibility(LottieAnimationView.GONE);

                    adapter = new RiwayatSiswaAdapter(getActivity(), results);
                    recyclerView.setAdapter(adapter);
                    runLayoutAnimation(recyclerView);

                    tagihan_count.setText("(" + results.size() + ")");

                } else {
                    tagihan_count.setText("(0)");
                    recyclerView.setVisibility(View.GONE);

                    lottieAnim.setAnimation(R.raw.nodata);
                    lottieAnim.playAnimation();
                    lottieAnim.setVisibility(LottieAnimationView.VISIBLE);
                    Log.e("Failed:", message);
                }
            }

            @Override
            public void onFailure(Call<TransaksiRepository> call, Throwable t) {
                tagihan_count.setText("(0)");
                recyclerView.setVisibility(View.GONE);

                lottieAnim.setAnimation(R.raw.nointernet);
                lottieAnim.playAnimation();
                lottieAnim.setVisibility(LottieAnimationView.VISIBLE);
                Log.e("DEBUG", "Error: ", t);
            }
        });
    }
}
