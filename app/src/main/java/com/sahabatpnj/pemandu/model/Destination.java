package com.sahabatpnj.pemandu.model;

public class Destination {
    private String namaDestinasi;
    private String descDestinasi;
    private String imgDestinasi;

    public Destination(String namaDestinasi, String descDestinasi, String imgDestinasi) {
        this.namaDestinasi = namaDestinasi;
        this.descDestinasi = descDestinasi;
        this.imgDestinasi = imgDestinasi;
    }
    public Destination() {

    }

    public String getNamaDestinasi() {
        return namaDestinasi;
    }

    public void setNamaDestinasi(String namaDestinasi) {
        this.namaDestinasi = namaDestinasi;
    }

    public String getDescDestinasi() {
        return descDestinasi;
    }

    public void setDescDestinasi(String descDestinasi) {
        this.descDestinasi = descDestinasi;
    }

    public String getImgDestinasi() {
        return imgDestinasi;
    }

    public void setImgDestinasi(String imgDestinasi) {
        this.imgDestinasi = imgDestinasi;
    }


}
