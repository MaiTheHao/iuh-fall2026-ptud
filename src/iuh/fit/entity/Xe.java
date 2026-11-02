package iuh.fit.entity;

public class Xe {
    private String bienSo;
    private String maDoiXe;
    private int odo;
    // chi de hien thi, sinh tu JOIN
    private String maHangXe;
    // chi de hien thi, sinh tu JOIN
    private String maDongXe;
    // chi de hien thi, sinh tu JOIN
    private String tenHangXe;
    // chi de hien thi, sinh tu JOIN
    private String tenDongXe;
    // chi de hien thi, sinh tu JOIN
    private int namSX;

    public Xe() {
    }

    public Xe(String bienSo, String maDoiXe, int odo) {
        this.bienSo = bienSo;
        this.maDoiXe = maDoiXe;
        this.odo = odo;
    }

    public String getBienSo() {
        return bienSo;
    }

    public void setBienSo(String bienSo) {
        this.bienSo = bienSo;
    }

    public String getMaDoiXe() {
        return maDoiXe;
    }

    public void setMaDoiXe(String maDoiXe) {
        this.maDoiXe = maDoiXe;
    }

    public String getMaHangXe() {
        return maHangXe;
    }

    public void setMaHangXe(String maHangXe) {
        this.maHangXe = maHangXe;
    }

    public String getMaDongXe() {
        return maDongXe;
    }

    public void setMaDongXe(String maDongXe) {
        this.maDongXe = maDongXe;
    }

    public String getTenHangXe() {
        return tenHangXe;
    }

    public void setTenHangXe(String tenHangXe) {
        this.tenHangXe = tenHangXe;
    }

    public String getTenDongXe() {
        return tenDongXe;
    }

    public void setTenDongXe(String tenDongXe) {
        this.tenDongXe = tenDongXe;
    }

    public int getNamSX() {
        return namSX;
    }

    public void setNamSX(int namSX) {
        this.namSX = namSX;
    }

    public int getOdo() {
        return odo;
    }

    public void setOdo(int odo) {
        this.odo = odo;
    }
}
