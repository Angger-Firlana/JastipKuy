package com.example.application.model;


import java.util.Date;

public class Titipan {
    private Integer id;
    private Integer user_id;
    private String nama_barang;
    private Long harga_estimasi;
    private String status;
    private String lokasi_jemput;
    private String lokasi_antar;
    private Integer diambil_oleh;
    private Date created_at;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getNama_barang() {
        return nama_barang;
    }

    public void setNama_barang(String nama_barang) {
        this.nama_barang = nama_barang;
    }

    public Long getHarga_estimasi() {
        return harga_estimasi;
    }

    public void setHarga_estimasi(Long harga_estimasi) {
        this.harga_estimasi = harga_estimasi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDiambil_oleh() {
        return diambil_oleh;
    }

    public void setDiambil_oleh(Integer diambil_oleh) {
        this.diambil_oleh = diambil_oleh;
    }

    public java.util.Date getCreated_at() {
        return created_at;
    }




    public void setCreated_at(java.util.Date created_at) {
        this.created_at = created_at;
    }

    public String getLokasi_jemput() {
        return lokasi_jemput;
    }

    public void setLokasi_jemput(String lokasi_jemput) {
        this.lokasi_jemput = lokasi_jemput;
    }

    public String getLokasi_antar() {
        return lokasi_antar;
    }

    public void setLokasi_antar(String lokasi_antar) {
        this.lokasi_antar = lokasi_antar;
    }
}
