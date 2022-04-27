package com.joblist.DB;

import com.joblist.Data.Repository.KelasRepository;
import com.joblist.Data.Repository.TransaksiRepository;
import com.joblist.Data.Repository.PetugasRepository;
import com.joblist.Data.Repository.SPPRepository;
import com.joblist.Data.Repository.SiswaRepository;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiEndPoints {
    @FormUrlEncoded
    @POST("dbLoginSiswa.php")
    Call<SiswaRepository> loginSiswa(
            @Field("nisn") String nisn,
            @Field("password") String password);

    @FormUrlEncoded
    @POST("dbLoginStaffLevel.php")
    Call<PetugasRepository> loginStaff(
            @Field("username") String username,
            @Field("password") String password);

    // [PS] Punya Siswa
    // Read
    @FormUrlEncoded
    @POST("dbReadSiswa.php")
    Call<SiswaRepository> readProfilSiswa(
            @Field("nisn") String nisn);

    @FormUrlEncoded
    @POST("dbReadTagihan.php")
    Call<TransaksiRepository> readTagihan(
            @Field("nisn") String nisn);

    @FormUrlEncoded
    @POST("dbReadHistory.php")
    Call<TransaksiRepository> readRiwayat(
            @Field("nisn") String nisn);


    // [PP] Punya Petugas
    // Read
    @GET("dbReadAllSiswa.php")
    Call<SiswaRepository> readSiswa();

    @FormUrlEncoded
    @POST("dbReadPetugas.php")
    Call<PetugasRepository> readProfilPetugas(
            @Field("username") String username);

    @FormUrlEncoded
    @POST("dbReadTransaksi.php")
    Call<TransaksiRepository> readTransaksi(
            @Field("nisn") String nisn);

    // Update
    @FormUrlEncoded
    @POST("dbUpdateTransaksi.php")
    Call<TransaksiRepository> updateTransaksi(
            @Field("id_pembayaran") String id_pembayaran,
            @Field("jumlah_bayar") String jumlah_bayar,
            @Field("id_petugas") String id_petugas);


    // [PA] Punya Admin
    // Create
    @FormUrlEncoded
    @POST("dbCreateSiswa.php")
    Call<SiswaRepository> createSiswa(
            @Field("nisn") String nisn,
            @Field("nis") String nis,
            @Field("nama") String nama,
            @Field("id_kelas") String id_kelas,
            @Field("id_spp") Integer id_spp,
            @Field("alamat") String alamat,
            @Field("no_telp") String no_telp,
            @Field("password") String password,
            @Field("id_petugas") String id_petugas);

    @FormUrlEncoded
    @POST("dbCreateKelas.php")
    Call<KelasRepository> createKelas(
            @Field("angkatan") String angkatan,
            @Field("nama_kelas") String nama,
            @Field("jurusan") String jurusan);

    @FormUrlEncoded
    @POST("dbCreateSPP.php")
    Call<SPPRepository> createSPP(
            @Field("angkatan") String angkatan,
            @Field("tahun") String tahun,
            @Field("nominal") String nominal);

    @FormUrlEncoded
    @POST("dbCreatePetugas.php")
    Call<PetugasRepository> createPetugas(
            @Field("username") String username,
            @Field("password") String password,
            @Field("nama_petugas") String nama_petugas);

    @FormUrlEncoded
    @POST("dbReadSiswaKelas.php")
    Call<SiswaRepository> readSiswaKelas(
            @Field("id_kelas") String id_kelas);

    @FormUrlEncoded
    @POST("dbReadSPPAngkatan.php")
    Call<SPPRepository> readSPPAngkatan(
            @Field("angkatan") String angkatan);

    // Read
    @GET("dbReadKelas.php")
    Call<KelasRepository> readKelas();

    @GET("dbReadSPP.php")
    Call<SPPRepository> readSPP();

    @GET("dbReadAllPetugas.php")
    Call<PetugasRepository> readPetugas();

    @FormUrlEncoded
    @POST("dbSearchSiswa.php")
    Call<SiswaRepository> searchSiswa(
            @Field("search") String search);

    @FormUrlEncoded
    @POST("dbSearchSiswaKelas.php")
    Call<SiswaRepository> searchSiswaKelas(
            @Field("search") String search,
            @Field("id_kelas") String id_kelas);

    @FormUrlEncoded
    @POST("dbSearchKelas.php")
    Call<KelasRepository> searchKelas(
            @Field("search") String search);

    @FormUrlEncoded
    @POST("dbSearchSPP.php")
    Call<SPPRepository> searchSPP(
            @Field("search") String search);

    @FormUrlEncoded
    @POST("dbSearchPetugas.php")
    Call<PetugasRepository> searchPetugas(
            @Field("search") String search);

    // Update
    @FormUrlEncoded
    @POST("dbUpdateSiswa.php")
    Call<SiswaRepository> updateSiswa(
            @Field("nisn") String nisn,
            @Field("nama") String nama,
            @Field("id_kelas") String id_kelas,
            @Field("id_spp") Integer id_spp,
            @Field("alamat") String alamat,
            @Field("no_telp") String no_telp,
            @Field("password") String password,
            @Field("id_petugas") String id_petugas);

    @FormUrlEncoded
    @POST("dbUpdateKelas.php")
    Call<KelasRepository> updateKelas(
            @Field("id_kelas") String id_kelas,
            @Field("nama_kelas") String nama_kelas,
            @Field("jurusan") String jurusan,
            @Field("angkatan") String angkatan);

    @FormUrlEncoded
    @POST("dbUpdateSPP.php")
    Call<SPPRepository> updateSPP(
            @Field("id_spp") Integer id_spp,
            @Field("nominal") String nominal);

    @FormUrlEncoded
    @POST("dbUpdatePetugas.php")
    Call<PetugasRepository> updatePetugas(
            @Field("id_petugas") String id_petugas,
            @Field("username") String username,
            @Field("password") String password,
            @Field("nama_petugas") String nama_petugas);

    // Delete
    @FormUrlEncoded
    @POST("dbDeleteSiswa.php")
    Call<SiswaRepository> deleteSiswa(
            @Field("nisn") String nisn);

    @FormUrlEncoded
    @POST("dbDeleteKelas.php")
    Call<KelasRepository> deleteKelas(
            @Field("id_kelas") String id_kelas);

    @FormUrlEncoded
    @POST("dbDeleteSPP.php")
    Call<SPPRepository> deleteSPP(
            @Field("id_spp") Integer id_spp);

    @FormUrlEncoded
    @POST("dbDeletePetugas.php")
    Call<PetugasRepository> deletePetugas(
            @Field("id_petugas") String id_petugas);
}
