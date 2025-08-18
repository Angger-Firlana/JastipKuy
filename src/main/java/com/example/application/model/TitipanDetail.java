package com.example.application.model;

public class TitipanDetail {
    private Integer id;
    private Integer idTransaksi;
    private String deskripsi;
    private String catatan_opsional;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(Integer idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getCatatan_opsional() {
        return catatan_opsional;
    }

    public void setCatatan_opsional(String catatan_opsional) {
        this.catatan_opsional = catatan_opsional;
    }
}
