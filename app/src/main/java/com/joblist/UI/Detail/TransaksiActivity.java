package com.joblist.UI.Detail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.example.modul_spp_ukk2021.R;
import com.example.modul_spp_ukk2021.UI.DB.ApiEndPoints;
import com.example.modul_spp_ukk2021.UI.Data.Helper.InputFilterMinMax;
import com.example.modul_spp_ukk2021.UI.Data.Helper.Utils;
import com.example.modul_spp_ukk2021.UI.Data.Model.Transaksi;
import com.example.modul_spp_ukk2021.UI.Data.Repository.TransaksiRepository;
import com.github.captain_miao.optroundcardview.OptRoundCardView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.modul_spp_ukk2021.UI.DB.baseURL.url;

public class TransaksiActivity extends AppCompatActivity {
    private Bitmap bitmap;
    private ApiEndPoints api;
    private TransaksiAdapter adapter;
    private RecyclerView recyclerView;
    private String id_pembayaran, id_petugas;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LottieAnimationView emptyTransaksi, process;
    private final List<Transaksi> transaksi = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_activity_pembayaran);
        id_petugas = getSharedPreferences("myprefs", Context.MODE_PRIVATE).getString("idStaff", null);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        process = findViewById(R.id.process);
        ImageView refresh = findViewById(R.id.refresh);
        OptRoundCardView back = findViewById(R.id.back);
        emptyTransaksi = findViewById(R.id.emptyTransaksi);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView = findViewById(R.id.recyclerTagihanSiswa);
        adapter = new TransaksiAdapter(this, transaksi);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
            inflater.inflate(R.menu.menu_refresh, popup.getMenu());

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

        adapter.setOnRecyclerViewItemClickListener((nama_siswa, nisn, id_pembayaran, jumlah_bayar, nominal, tanggalTagihan, tanggalBayar, status, nama_staff, nama_kelas, kurang_bayar, download) -> {
            if (download == null) {
                DialogUpdate(id_pembayaran, nominal, kurang_bayar);

            } else {
                GeneratePembayaran(nama_siswa, nisn, id_pembayaran, jumlah_bayar, tanggalTagihan, tanggalBayar, status, nama_staff, nama_kelas);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        process.setAnimation(R.raw.loading);
        process.playAnimation();
        process.setVisibility(LottieAnimationView.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadDataPembayaran();
    }

    public void Refreshing() {
        swipeRefreshLayout.setRefreshing(true);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        loadDataPembayaran();
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.scheduleLayoutAnimation();
    }

    private void loadDataPembayaran() {
        String nisnSiswa = this.getIntent().getStringExtra("nisnSiswa");
        Call<TransaksiRepository> call = api.readTransaksi(nisnSiswa);
        call.enqueue(new Callback<TransaksiRepository>() {
            @Override
            public void onResponse(Call<TransaksiRepository> call, Response<TransaksiRepository> response) {
                String value = response.body().getValue();
                String message = response.body().getMessage();
                List<Transaksi> transaksi = response.body().getResult();

                if (value.equals("1")) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTransaksi.pauseAnimation();
                    emptyTransaksi.setVisibility(LottieAnimationView.GONE);

                    adapter = new TransaksiAdapter(TransaksiActivity.this, transaksi);
                    recyclerView.setAdapter(adapter);
                    runLayoutAnimation(recyclerView);

                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyTransaksi.setAnimation(R.raw.nodata);
                    emptyTransaksi.playAnimation();
                    emptyTransaksi.setVisibility(LottieAnimationView.VISIBLE);
                    Toast.makeText(TransaksiActivity.this, message, Toast.LENGTH_SHORT).show();
                }

                process.pauseAnimation();
                process.setVisibility(LottieAnimationView.GONE);
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
            public void onFailure(Call<TransaksiRepository> call, Throwable t) {
                recyclerView.setVisibility(View.GONE);

                emptyTransaksi.setAnimation(R.raw.nointernet);
                emptyTransaksi.playAnimation();
                emptyTransaksi.setVisibility(LottieAnimationView.VISIBLE);

                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }

                process.pauseAnimation();
                process.setVisibility(LottieAnimationView.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                Toast.makeText(TransaksiActivity.this, "Gagal koneksi sistem, silahkan coba lagi...", Toast.LENGTH_LONG).show();
                Log.e("DEBUG", "Error: ", t);
            }
        });
    }

    private void DialogUpdate(String id_pembayaran, Integer nominal, Integer kurang_bayar) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransaksiActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(TransaksiActivity.this).inflate(R.layout.pp_dialog_update_transaksi, findViewById(R.id.layoutDialogContainer));
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        TextView maxInput = view.findViewById(R.id.maxInput);
        TextView tagihan = view.findViewById(R.id.tagihan);
        EditText jumlah_bayar = view.findViewById(R.id.jumlahBayar);

        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        format.setMaximumFractionDigits(0);

        if (kurang_bayar == 0) {
            tagihan.setText("Tagihan    : " + format.format(nominal));
            maxInput.setText("Max Input: " + format.format(nominal));
            jumlah_bayar.setFilters(new InputFilter[]{new InputFilterMinMax("0", nominal.toString())});
        } else {
            tagihan.setText("Tagihan : " + format.format(nominal));
            maxInput.setText("Kurang  : " + format.format(kurang_bayar));
            jumlah_bayar.setHint("Max input." + kurang_bayar);
            jumlah_bayar.setFilters(new InputFilter[]{new InputFilterMinMax("0", kurang_bayar.toString())});
        }

        view.findViewById(R.id.buttonKirim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<TransaksiRepository> call = api.updateTransaksi(id_pembayaran, jumlah_bayar.getText().toString(), id_petugas);
                call.enqueue(new Callback<TransaksiRepository>() {
                    @Override
                    public void onResponse(Call<TransaksiRepository> call, Response<TransaksiRepository> response) {
                        String value = response.body().getValue();
                        String message = response.body().getMessage();

                        if (value.equals("1")) {
                            alertDialog.dismiss();
                            process.setAnimation(R.raw.success);
                            process.playAnimation();
                            process.setVisibility(LottieAnimationView.VISIBLE);
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    process.pauseAnimation();
                                    process.setVisibility(LottieAnimationView.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    loadDataPembayaran();
                                }
                            }, 3000);

                        } else {
                            Toast.makeText(TransaksiActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransaksiRepository> call, Throwable t) {
                        process.setAnimation(R.raw.failed);
                        process.playAnimation();
                        process.setVisibility(LottieAnimationView.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(TransaksiActivity.this, "Gagal koneksi sistem, silahkan coba lagi...", Toast.LENGTH_LONG).show();
                        Log.e("DEBUG", "Error: ", t);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                process.pauseAnimation();
                                process.setVisibility(LottieAnimationView.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }, 3000);
                    }
                });
            }
        });

        view.findViewById(R.id.buttonLunas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                process.setAnimation(R.raw.loading);
                process.playAnimation();
                process.setVisibility(LottieAnimationView.VISIBLE);

                Call<TransaksiRepository> call = api.updateTransaksi(id_pembayaran, nominal.toString(), id_petugas);
                call.enqueue(new Callback<TransaksiRepository>() {
                    @Override
                    public void onResponse(Call<TransaksiRepository> call, Response<TransaksiRepository> response) {
                        String value = response.body().getValue();
                        String message = response.body().getMessage();

                        if (value.equals("1")) {
                            process.setAnimation(R.raw.success);
                            process.playAnimation();
                            process.setVisibility(LottieAnimationView.VISIBLE);
                            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    process.pauseAnimation();
                                    process.setVisibility(LottieAnimationView.GONE);
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    loadDataPembayaran();
                                }
                            }, 3000);

                        } else {
                            Toast.makeText(TransaksiActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransaksiRepository> call, Throwable t) {
                        process.setAnimation(R.raw.failed);
                        process.playAnimation();
                        process.setVisibility(LottieAnimationView.VISIBLE);
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(TransaksiActivity.this, "Gagal koneksi sistem, silahkan coba lagi...", Toast.LENGTH_LONG).show();
                        Log.e("DEBUG", "Error: ", t);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                process.pauseAnimation();
                                process.setVisibility(LottieAnimationView.GONE);
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            }
                        }, 3000);
                    }
                });
            }
        });

        view.findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void GeneratePembayaran(String nama_siswa, String nisn, String id_pembayaran, Integer jumlah_bayar, String tanggalTagihan, String tanggalBayar, String status, String nama_staff, String nama_kelas) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TransaksiActivity.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(TransaksiActivity.this).inflate(R.layout.dialog_generate_laporan, findViewById(R.id.layoutDialogContainer));
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();

        TextView tvFillNama = view.findViewById(R.id.tvFillNama);
        TextView tvNISN = view.findViewById(R.id.tvNISN);
        TextView nomor_pembayaran = view.findViewById(R.id.nomor_pembayaran);
        TextView nominal = view.findViewById(R.id.nominal);
        TextView tanggal_tagihan = view.findViewById(R.id.tanggal_tagihan);
        TextView tanggal_bayar = view.findViewById(R.id.tanggal_bayar);
        TextView status_pembayaran = view.findViewById(R.id.status_pembayaran);
        TextView dilayaniOleh = view.findViewById(R.id.dilayaniOleh);
        TextView kelas = view.findViewById(R.id.kelas);

        Locale localeID = new Locale("in", "ID");
        NumberFormat format = NumberFormat.getCurrencyInstance(localeID);
        format.setMaximumFractionDigits(0);

        tvFillNama.setText(nama_siswa);
        tvNISN.setText("NISN    : " + nisn);
        nomor_pembayaran.setText("#" + id_pembayaran.toUpperCase());
        nominal.setText(format.format(jumlah_bayar));
        tanggal_tagihan.setText(tanggalTagihan);
        tanggal_bayar.setText(tanggalBayar);
        status_pembayaran.setText(status);
        dilayaniOleh.setText(nama_staff);
        kelas.setText(nama_kelas);

        this.id_pembayaran = id_pembayaran;

        ConstraintLayout layoutToPdf = view.findViewById(R.id.layoutDialog);
        view.findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                bitmap = LoadBitmap(layoutToPdf, layoutToPdf.getWidth(), layoutToPdf.getHeight());
                Log.e("size", "" + layoutToPdf.getWidth() + " " + layoutToPdf.getWidth());
                createPdf();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private Bitmap LoadBitmap(View v, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    private void createPdf() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float hight = displaymetrics.heightPixels;
        float width = displaymetrics.widthPixels;
        int convertHighet = (int) hight, convertWidth = (int) width;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(convertWidth, convertHighet, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        canvas.drawColor(Color.WHITE);
        bitmap = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHighet, true);
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(page);

        // write the document content
        String filename = id_pembayaran + ".pdf";
        String destination = getExternalFilesDir(null) + "/";
        File dir = new File(destination);

        try {
            File file = new File(destination, filename);
            document.writeTo(new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.v("PdfError", e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        document.close();
        openPdf();
    }

    private void openPdf() {
        String filename = id_pembayaran + ".pdf";
        String destination = getExternalFilesDir(null) + "/";
        File pdfFile = new File(destination + "/" + filename);

        if (pdfFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri mURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", pdfFile);
            intent.setDataAndType(mURI, "application/pdf");
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "The file not exists! ", Toast.LENGTH_SHORT).show();
        }
    }
}