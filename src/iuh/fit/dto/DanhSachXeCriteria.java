
package iuh.fit.dto;

public record DanhSachXeCriteria(
        String bienSo,
        String hangXe,
        String dongXe,
        Integer doiXeTu,
        Integer doiXeDen,
        Integer odoTu,
        Integer odoDen,
        String timNhanh,
        int page,
        int pageSize) {
}
