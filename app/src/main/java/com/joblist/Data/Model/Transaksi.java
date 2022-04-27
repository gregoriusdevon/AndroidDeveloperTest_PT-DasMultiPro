package com.joblist.Data.Model;

import java.util.Date;

public class Transaksi {
    String id_pembayaran;
    String nama_petugas;
    String nisn;
    String nama;
    String nama_kelas;
    Date tgl_bayar;
    Integer nominal;
    Integer bulan_bayar;
    String tahun_bayar;
    String status_bayar;
    Integer jumlah_bayar;
    Integer kurang_bayar;

    public String getId_pembayaran() {
        return id_pembayaran;
    }

    public String getNama_petugas() {
        return nama_petugas;
    }

    public String getNisn() {
        return nisn;
    }

    public String getNama() {
        return nama;
    }

    public String getNama_kelas() {
        return nama_kelas;
    }

    public Date getTgl_bayar() {
        return tgl_bayar;
    }

    public Integer getNominal() {
        return nominal;
    }

    public Integer getBulan_bayar() {
        return bulan_bayar;
    }

    public String getTahun_bayar() {
        return tahun_bayar;
    }

    public String getStatus_bayar() {
        return status_bayar;
    }

    public Integer getJumlah_bayar() {
        return jumlah_bayar;
    }

    public Integer getKurang_bayar() {
        return kurang_bayar;
    }
}
