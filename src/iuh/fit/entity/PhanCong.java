package iuh.fit.entity;

import iuh.fit.enumeration.CaLamViec;

public class PhanCong {
    private String maPhanCong;
    private String maNhanVien;
    private int ngay;
    private int thang;
    private int nam;
    private CaLamViec caLamViec;
    private String ghiChu;

    public PhanCong() {
    }

    public PhanCong(String maPhanCong, String maNhanVien, int ngay, int thang, int nam, CaLamViec caLamViec,
            String ghiChu) {
        this.maPhanCong = maPhanCong;
        this.maNhanVien = maNhanVien;
        this.ngay = ngay;
        this.thang = thang;
        this.nam = nam;
        this.caLamViec = caLamViec;
        this.ghiChu = ghiChu;
    }

    public String getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public int getNgay() {
        return ngay;
    }

    public void setNgay(int ngay) {
        this.ngay = ngay;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public CaLamViec getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(CaLamViec caLamViec) {
        this.caLamViec = caLamViec;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}
