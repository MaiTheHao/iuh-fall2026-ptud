package iuh.fit.entity;

import iuh.fit.enumeration.LoaiSanPham;

public class DichVu extends SanPham {
    private Double donGia;

    public DichVu() {
    }

    public DichVu(String maSanPham) {
        super(maSanPham, null, null, null);
    }

    public DichVu(String maSanPham, String tenSanPham, String moTa, String anhUrl, Double donGia) {
        super(maSanPham, tenSanPham, moTa, anhUrl);
        this.donGia = donGia;
    }

    @Override
    public LoaiSanPham getLoaiSanPham() {
        return LoaiSanPham.DICH_VU;
    }

    public Double getDonGia() {
        return donGia;
    }

    public void setDonGia(Double donGia) {
        this.donGia = donGia;
    }
}
