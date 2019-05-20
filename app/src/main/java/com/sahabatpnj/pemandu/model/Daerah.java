package com.sahabatpnj.pemandu.model;

public class Daerah {

    private String namaDaerah;
    private String descDaerah;
    private String imgDaerah;

    public Daerah(String namaDaerah, String descDaerah, String imgDaerah){
        this.namaDaerah = namaDaerah;
        this.descDaerah = descDaerah;
        this.imgDaerah = imgDaerah;
    }

    public Daerah(){

    }

    public String getNamaDaerah() {
        return namaDaerah;
    }

    public void setNamaDaerah(String namaDaerah) {
        this.namaDaerah = namaDaerah;
    }

    public String getDescDaerah() {
        return descDaerah;
    }

    public void setDescDaerah(String descDaerah) {
        this.descDaerah = descDaerah;
    }

    public String getImgDaerah() {
        return imgDaerah;
    }

    public void setImgDaerah(String imgDaerah) {
        this.imgDaerah = imgDaerah;
    }
}

