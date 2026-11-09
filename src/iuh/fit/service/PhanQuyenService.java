package iuh.fit.service;

import iuh.fit.enumeration.ManHinh;
import iuh.fit.enumeration.VaiTro;

import java.util.Set;

public class PhanQuyenService {

    private static final Set<ManHinh> MAN_HINH_TIEP_NHAN = Set.of(
            ManHinh.KHACH_HANG,
            ManHinh.XE,
            ManHinh.DANH_MUC_XE,
            ManHinh.TIEP_NHAN_KHACH_HANG,
            ManHinh.QUAN_LY_PHIEU_TIEP_NHAN,
            ManHinh.XEM_CHAN_DOAN,
            ManHinh.TIEP_NHAN_KHIEU_NAI,
            ManHinh.QUAN_LY_KHIEU_NAI,
            ManHinh.LICH_CUA_TOI,
            ManHinh.LICH_CUA_SHOP,
            ManHinh.DOI_MAT_KHAU
    );

    private static final Set<ManHinh> MAN_HINH_THU_NGAN = Set.of(
            ManHinh.LAP_HOA_DON,
            ManHinh.QUAN_LY_HOA_DON,
            ManHinh.LICH_CUA_TOI,
            ManHinh.DOI_MAT_KHAU
    );

    private static final Set<ManHinh> MAN_HINH_KY_THUAT = Set.of(
            ManHinh.QUAN_LY_CHAN_DOAN,
            ManHinh.BANG_CHAN_DOAN,
            ManHinh.XEM_CHAN_DOAN,
            ManHinh.LICH_CUA_TOI,
            ManHinh.DOI_MAT_KHAU
    );

    public boolean duocHien(VaiTro vaiTro, ManHinh manHinh) {
        if (vaiTro == null || manHinh == null || manHinh == ManHinh.DANG_NHAP) {
            return false;
        }

        return switch (vaiTro) {
            case NHAN_VIEN_QUAN_LY -> true;
            case NHAN_VIEN_TIEP_NHAN -> MAN_HINH_TIEP_NHAN.contains(manHinh);
            case NHAN_VIEN_THU_NGAN -> MAN_HINH_THU_NGAN.contains(manHinh);
            case NHAN_VIEN_KY_THUAT -> MAN_HINH_KY_THUAT.contains(manHinh);
        };
    }
}
