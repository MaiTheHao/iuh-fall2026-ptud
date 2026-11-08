package iuh.fit.enumeration;

public enum PhuongThucThanhToan implements I18nEnum {
  CHUYEN_KHOAN {
    @Override
    public String getI18nKey() {
      return "app.thanhToan.phuongThucThanhToan.chuyenKhoan";
    }
  },
  TIEN_MAT {
    @Override
    public String getI18nKey() {
      return "app.thanhToan.phuongThucThanhToan.tienMat";
    }
  }
}
