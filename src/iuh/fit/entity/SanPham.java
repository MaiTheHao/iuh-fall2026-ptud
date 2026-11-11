package iuh.fit.entity;

import iuh.fit.enumeration.LoaiSanPham;

/**
 * Lớp cha chung cho các sản phẩm có thể xuất hiện trên hoá đơn.
 */
public abstract class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private String moTa;
    private String anhUrl;

    protected SanPham() {
    }

    protected SanPham(String maSanPham, String tenSanPham, String moTa, String anhUrl) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.moTa = moTa;
        this.anhUrl = anhUrl;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public String getAnhUrl() {
        return anhUrl;
    }

    public void setAnhUrl(String anhUrl) {
        this.anhUrl = anhUrl;
    }

    public abstract LoaiSanPham getLoaiSanPham();
}
