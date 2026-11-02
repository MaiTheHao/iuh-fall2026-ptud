package iuh.fit.enumeration;

public enum LoaiMa {
  DV("DV"),
  KH("KH"),
  NV("NV"),
  PT("PT"),
  VC("VC"),
  PN("PN"),
  PTN("PTN"),
  PC("PC"),
  HD("HD"),
  VD("VD"),
  KN("KN"),
  // U3: tiền tố NCC cho MaService (NCC-yyyyMMdd-NNNNNN).
  NCC("NCC");

  private final String tienTo;

  LoaiMa(String tienTo) {
    this.tienTo = tienTo;
  }

  public String getTienTo() {
    return this.tienTo;
  }
}
