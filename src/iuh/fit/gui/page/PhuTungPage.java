
package iuh.fit.gui.page;

import iuh.fit.dto.DanhSachPhuTungCriteria;
import iuh.fit.dto.PaginatedResult;
import iuh.fit.entity.PhuTung;
import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.component.ManagementPanel;
import iuh.fit.service.PhuTungService;
import iuh.fit.util.I18nReaderUtil;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import java.net.URI;
import java.net.URLConnection;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhuTungPage extends AbstractPage {

    private static final long serialVersionUID = 1L;
    private static final int SO_DONG_MOI_TRANG = 10;

    private final PhuTungService phuTungService =
            new PhuTungService();

    private final JTextField maField = new JTextField(14);
    private final JTextField tenField = new JTextField(14);
    private final JTextField moTaField = new JTextField(14);
    private final JTextField anhUrlField = new JTextField(14);
    private final JTextField donGiaField = new JTextField(14);
    private final JTextField soLuongField = new JTextField(14);

    private final JLabel anhPreview = new JLabel(
            I18nReaderUtil.get("app.phuTung.chuaCoAnh"),
            JLabel.CENTER
    );

    private ManagementPanel panel;
    private int trang = 1;

    public PhuTungPage() {
        this.setLayout(new BorderLayout());
    }

    @Override
    public void render() {
        this.removeAll();
        this.setBorder(
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );

        this.panel = new ManagementPanel();

        this.panel.setHeaders(
                I18nReaderUtil.get("app.phuTung.hinhAnh"),
                I18nReaderUtil.get("app.phuTung.ma"),
                I18nReaderUtil.get("app.phuTung.ten"),
                I18nReaderUtil.get("app.phuTung.moTa"),
                I18nReaderUtil.get("app.phuTung.donGia"),
                I18nReaderUtil.get("app.phuTung.soLuongTon")
        );

        this.khoiTaoForm();
        this.khoiTaoBoLoc();
        this.ganSuKien();

        this.taiLai(this.panel, 1);

        this.add(this.panel, BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }

    private void khoiTaoForm() {
        this.maField.setEnabled(true);

        this.addFormRow(
                this.panel,
                "app.phuTung.ma",
                this.maField
        );

        this.addFormRow(
                this.panel,
                "app.phuTung.ten",
                this.tenField
        );

        this.addFormRow(
                this.panel,
                "app.phuTung.moTa",
                this.moTaField
        );

        this.addFormRow(
                this.panel,
                "app.phuTung.anhUrl",
                this.anhUrlField
        );

        this.anhUrlField.addActionListener(
                e -> this.hienThiAnh(
                        this.anhUrlField.getText().trim()
                )
        );

        JPanel nutAnhPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 6, 2)
        );

        nutAnhPanel.setOpaque(false);

        JButton chonAnhButton = new JButton(
                I18nReaderUtil.get("app.phuTung.chonAnh")
        );

        JButton goAnhButton = new JButton(
                I18nReaderUtil.get("app.phuTung.goAnh")
        );

        chonAnhButton.addActionListener(e -> this.chonAnh());
        goAnhButton.addActionListener(e -> this.goAnh());

        nutAnhPanel.add(chonAnhButton);
        nutAnhPanel.add(goAnhButton);

        this.panel.getFormPanel().add(nutAnhPanel);

        this.anhPreview.setPreferredSize(
                new Dimension(180, 120)
        );

        this.anhPreview.setBorder(
                BorderFactory.createEtchedBorder()
        );

        JPanel previewPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 6, 2)
        );

        previewPanel.setOpaque(false);
        previewPanel.add(this.anhPreview);

        this.panel.getFormPanel().add(previewPanel);

        this.addFormRow(
                this.panel,
                "app.phuTung.donGia",
                this.donGiaField
        );

        this.addFormRow(
                this.panel,
                "app.phuTung.soLuongTon",
                this.soLuongField
        );
        this.soLuongField.setEditable(false);
    }

    private void khoiTaoBoLoc() {
        // Mã phụ tùng, tên phụ tùng
        this.panel.addTextFilter("app.phuTung.ma", 0);
        this.panel.addTextFilter("app.phuTung.ten", 1);

        // Đơn giá từ - đến
        this.panel.addRangeFilter("app.phuTung.donGiaTuDen", 2);

        // Tồn kho từ - đến
        this.panel.addRangeFilter("app.phuTung.soLuongTuDen", 4);

        this.panel.setFilterChangeListener(
                () -> this.taiLai(this.panel, 1)
        );
    }

    private void ganSuKien() {
        this.panel.getThemButton().addActionListener(
                e -> this.lamMoi()
        );

        this.panel.getLuuButton().addActionListener(
                e -> this.luu()
        );

        this.panel.getXoaTrangButton().addActionListener(
                e -> this.lamMoi()
        );

        this.panel.getXoaButton().addActionListener(
                e -> this.xoa()
        );

        this.panel.getClearFilterButton().addActionListener(
                e -> this.panel.clearFilters()
        );

        this.panel.getPrevButton().addActionListener(
                e -> this.taiLai(this.panel, this.trang - 1)
        );

        this.panel.getNextButton().addActionListener(
                e -> this.taiLai(this.panel, this.trang + 1)
        );

        this.panel.getTable().getSelectionModel()
                .addListSelectionListener(e -> {
                    if (!e.getValueIsAdjusting()) {
                        this.chonDong();
                    }
                });
    }

    private void addFormRow(
            ManagementPanel panel,
            String labelKey,
            JTextField field) {

        JPanel row = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 6, 2)
        );

        row.setOpaque(false);

        row.add(new JLabel(I18nReaderUtil.get(labelKey)));
        row.add(field);

        panel.getFormPanel().add(row);
    }

    private void chonAnh() {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle(
                I18nReaderUtil.get("app.phuTung.chonAnh")
        );

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                		I18nReaderUtil.get("app.phuTung.loaiTepAnh"),
                        "png", "jpg", "jpeg", "gif"
                )
        );

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            this.anhUrlField.setText(
                    file.getAbsolutePath()
            );

            this.hienThiAnh(file.getAbsolutePath());
        }
    }

    private void goAnh() {
        this.anhUrlField.setText("");
        this.anhPreview.setIcon(null);

        this.anhPreview.setText(
                I18nReaderUtil.get("app.phuTung.chuaCoAnh")
        );
    }

    private void hienThiAnh(String duongDan) {
        if (duongDan == null || duongDan.isBlank()) {
            this.goAnh();
            return;
        }

        try {
            BufferedImage bufferedImage;

            if (duongDan.startsWith("https://")
                    || duongDan.startsWith("http://")) {

                URLConnection connection =
                        URI.create(duongDan.trim())
                                .toURL()
                                .openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);

                try (var input = connection.getInputStream()) {
                    bufferedImage = ImageIO.read(input);
                }

            } else {
                bufferedImage = ImageIO.read(
                        new File(duongDan.trim())
                );
            }

            if (bufferedImage == null) {
                throw new IOException(
                		I18nReaderUtil.get("app.phuTung.duLieuKhongPhaiAnh")
                );
            }

            Image image = bufferedImage.getScaledInstance(
                    180,
                    120,
                    Image.SCALE_SMOOTH
            );

            this.anhPreview.setText("");
            this.anhPreview.setIcon(
                    new ImageIcon(image)
            );

        } catch (Exception e) {
            this.anhPreview.setIcon(null);
            this.anhPreview.setText(I18nReaderUtil.get("app.phuTung.khongTaiDuocAnh"));

            System.err.println(I18nReaderUtil.get("app.phuTung.khongCoAnh") + duongDan);
            e.printStackTrace();
        }
    }

    private void taiLai(ManagementPanel panel, int page) {
        if (page < 1) {
            page = 1;
        }

        try {
            DanhSachPhuTungCriteria tieuChi =
                    new DanhSachPhuTungCriteria(
                            panel.getTextFilter(0),
                            panel.getTextFilter(1),
                            panel.getQuickFilter(),
                            this.parseDouble(
                                    panel.getTextFilter(2)
                            ),
                            this.parseDouble(
                                    panel.getTextFilter(3)
                            ),
                            this.parseInteger(
                                    panel.getTextFilter(4)
                            ),
                            this.parseInteger(
                                    panel.getTextFilter(5)
                            ),
                            page,
                            SO_DONG_MOI_TRANG
                    );

            // Kiểm tra khoảng lọc hợp lệ
            if (tieuChi.donGiaTu() != null
                    && tieuChi.donGiaDen() != null
                    && tieuChi.donGiaTu()
                            > tieuChi.donGiaDen()) {
                throw new IllegalArgumentException(
                        "app.phuTung.loiKhoangLoc"
                );
            }

            if (tieuChi.soLuongTu() != null
                    && tieuChi.soLuongDen() != null
                    && tieuChi.soLuongTu()
                            > tieuChi.soLuongDen()) {
                throw new IllegalArgumentException(
                        "app.phuTung.loiKhoangLoc"
                );
            }

            if ((tieuChi.donGiaTu() != null
                    && tieuChi.donGiaTu() < 0)
                    || (tieuChi.donGiaDen() != null
                    && tieuChi.donGiaDen() < 0)
                    || (tieuChi.soLuongTu() != null
                    && tieuChi.soLuongTu() < 0)
                    || (tieuChi.soLuongDen() != null
                    && tieuChi.soLuongDen() < 0)) {
                throw new IllegalArgumentException(
                        "app.phuTung.loiKhoangLoc"
                );
            }

            PaginatedResult<PhuTung> ketQua =
                    this.phuTungService.search(tieuChi);

            List<Object[]> rows = new ArrayList<>();

            for (PhuTung phuTung : ketQua.items()) {
                rows.add(new Object[]{
                        phuTung.getAnhUrl(),
                        phuTung.getMaPhuTung(),
                        phuTung.getTenPhuTung(),
                        phuTung.getMoTa(),
                        phuTung.getDonGiaNhap(),
                        phuTung.getSoLuongTon()
                });
            }

            panel.setRows(rows);

            this.cauHinhCotAnh();

            panel.setCount(
                    ketQua.fromIndex(),
                    ketQua.toIndex(),
                    ketQua.totalItems()
            );

            panel.setPage(
                    ketQua.page(),
                    Math.max(ketQua.totalPages(), 1)
            );

            this.trang = ketQua.page();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.phuTung.loiBoLocSo"
                    )
            );

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(e.getMessage())
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.login.errorSystem"
                    )
            );
        }
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Double.valueOf(value.trim());
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return Integer.valueOf(value.trim());
    }

    private void cauHinhCotAnh() {
        JTable table = this.panel.getTable();

        if (table.getColumnCount() == 0) {
            return;
        }

        TableColumn cotAnh =
                table.getColumnModel().getColumn(0);

        cotAnh.setCellRenderer(new AnhRenderer());
        cotAnh.setPreferredWidth(65);
        cotAnh.setMinWidth(65);
        cotAnh.setMaxWidth(80);

        table.setRowHeight(55);
    }

    private static class AnhRenderer
            extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        private static final Map<String, ImageIcon> CACHE =
                new HashMap<>();

        private static final Set<String> LOI_TAI_ANH =
                new HashSet<>();

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column) {

            JLabel label = (JLabel)
                    super.getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setHorizontalAlignment(
                    SwingConstants.CENTER
            );

            label.setVerticalAlignment(
                    SwingConstants.CENTER
            );

            label.setIcon(null);
            label.setText("");

            if (value == null || value.toString().isBlank()) {
                return label;
            }

            String duongDan = value.toString().trim();

            ImageIcon icon = CACHE.get(duongDan);

            if (icon == null
                    && !LOI_TAI_ANH.contains(duongDan)) {

                try {
                    BufferedImage bufferedImage;

                    if (duongDan.startsWith("https://")
                            || duongDan.startsWith("http://")) {

                        URLConnection connection =
                                URI.create(duongDan)
                                        .toURL()
                                        .openConnection();

                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(5000);

                        try (var input =
                                     connection.getInputStream()) {
                            bufferedImage = ImageIO.read(input);
                        }

                    } else {
                        bufferedImage = ImageIO.read(
                                new File(duongDan)
                        );
                    }

                    if (bufferedImage == null) {
                        throw new IOException(
                        		  I18nReaderUtil.get("app.phuTung.khongPhaiFileAnh")
                        );
                    }

                    Image image = bufferedImage.getScaledInstance(
                            50,
                            45,
                            Image.SCALE_SMOOTH
                    );

                    icon = new ImageIcon(image);
                    CACHE.put(duongDan, icon);

                } catch (Exception e) {
                    LOI_TAI_ANH.add(duongDan);
                }
            }

            if (icon != null) {
                label.setIcon(icon);
            } else {
                label.setText(I18nReaderUtil.get("app.phuTung.khongCoAnh"));
            }

            return label;
        }
    }

    private void chonDong() {
        int row = this.panel.getSelectedRow();

        if (row < 0) {
            return;
        }

        this.anhUrlField.setText(
                this.giaTriChuoi(
                        this.panel.getModel().getValueAt(row, 0)
                )
        );

        this.maField.setText(
                this.giaTriChuoi(
                        this.panel.getModel().getValueAt(row, 1)
                )
        );

        this.tenField.setText(
                this.giaTriChuoi(
                        this.panel.getModel().getValueAt(row, 2)
                )
        );

        this.moTaField.setText(
                this.giaTriChuoi(
                        this.panel.getModel().getValueAt(row, 3)
                )
        );

        this.donGiaField.setText(
                this.giaTriChuoi(
                        this.panel.getModel().getValueAt(row, 4)
                )
        );

        this.soLuongField.setText(
                this.giaTriChuoi(
                        this.panel.getModel().getValueAt(row, 5)
                )
        );

        this.maField.setEnabled(false);

        this.hienThiAnh(
                this.anhUrlField.getText().trim()
        );
    }

    private String giaTriChuoi(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private void lamMoi() {
        this.maField.setText("");
        this.tenField.setText("");
        this.moTaField.setText("");
        this.anhUrlField.setText("");
        this.donGiaField.setText("");
        this.soLuongField.setText("");

        this.maField.setEnabled(true);

        this.panel.getTable().clearSelection();

        this.goAnh();

        this.maField.requestFocusInWindow();
    }

    private void luu() {
        String ma = this.maField.getText().trim();
        String ten = this.tenField.getText().trim();
        String moTa = this.moTaField.getText().trim();
        String anhUrl = this.anhUrlField.getText().trim();

        double donGia;
        int soLuong;

        if (ma.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get("app.phuTung.loiMa")
            );
            return;
        }

        if (ten.isBlank()) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get("app.phuTung.loiTen")
            );
            return;
        }

        try {
            donGia = Double.parseDouble(
                    this.donGiaField.getText().trim()
            );

            if (!Double.isFinite(donGia) || donGia < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get("app.phuTung.loiDonGia")
            );
            return;
        }

        try {
            soLuong = Integer.parseInt(
                    this.soLuongField.getText().trim()
            );

            if (soLuong < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get("app.phuTung.loiSoLuong")
            );
            return;
        }

        PhuTung phuTung = new PhuTung(
                ma,
                ten,
                moTa.isBlank() ? null : moTa,
                anhUrl.isBlank() ? null : anhUrl,
                donGia,
                soLuong
        );

        try {
            if (this.maField.isEnabled()) {
                this.phuTungService.create(phuTung);
                JOptionPane.showMessageDialog(
                        this, I18nReaderUtil.get("app.common.create_succeed"));
            } else {
                this.phuTungService.update(phuTung);
                JOptionPane.showMessageDialog(
                        this, I18nReaderUtil.get("app.common.update_succeed"));
            }

            this.lamMoi();
            this.taiLai(this.panel, 1);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(e.getMessage())
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.login.errorSystem"
                    )
            );
        }
    }

    private void xoa() {
        int row = this.panel.getSelectedRow();

        try {
            if (row < 0) {
                throw new IllegalArgumentException(
                        "app.phuTung.chuaChonXoa"
                );
            }

            String maPhuTung = String.valueOf(
                    this.panel.getModel().getValueAt(row, 1)
            );

            int luaChon = JOptionPane.showConfirmDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.phuTung.xacNhanXoa"
                    ),
                    I18nReaderUtil.get(
                            "app.phuTung.tieuDeXoa"
                    ),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (luaChon != JOptionPane.YES_OPTION) {
                return;
            }

            this.phuTungService.delete(maPhuTung);
            JOptionPane.showMessageDialog(this, "delete_succeed");
            
            this.lamMoi();
            this.taiLai(this.panel, this.trang);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(e.getMessage())
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    I18nReaderUtil.get(
                            "app.login.errorSystem"
                    )
            );
        }
    }

    @Override
    public String getTitleKey() {
        return "app.menu.inventory";
    }

    @Override
    public MenuGroup getGroup() {
        return MenuGroup.TO_CHUC;
    }
}
