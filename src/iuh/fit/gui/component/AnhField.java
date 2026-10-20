package iuh.fit.gui.component;

import iuh.fit.gui.theme.Theme;
import iuh.fit.util.I18nReaderUtil;
import iuh.fit.util.TaiAnhUtil;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;

public class AnhField extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Bộ khóa i18n cho một trường ảnh, tránh truyền bảy chuỗi rời theo thứ tự.
     */
    public record KhoaAnh(
            String nhan,
            String chonAnh,
            String goAnh,
            String chuaCoAnh,
            String khongTaiDuocAnh,
            String loaiTepAnh,
            String duLieuKhongPhaiAnh) {
    }

    private final String keyChonAnh;
    private final String keyLoaiTepAnh;
    private final String keyChuaCoAnh;
    private final String keyKhongTaiDuocAnh;
    private final String keyDuLieuKhongPhaiAnh;

    private final JTextField duongDanField = new JTextField(20);
    private final JLabel previewLabel = new JLabel("", SwingConstants.CENTER);

    public AnhField(KhoaAnh khoa) {
        this.keyChonAnh = khoa.chonAnh();
        this.keyLoaiTepAnh = khoa.loaiTepAnh();
        this.keyChuaCoAnh = khoa.chuaCoAnh();
        this.keyKhongTaiDuocAnh = khoa.khongTaiDuocAnh();
        this.keyDuLieuKhongPhaiAnh = khoa.duLieuKhongPhaiAnh();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        JPanel duongDanRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        duongDanRow.setOpaque(false);

        JLabel nhanLabel = new JLabel(I18nReaderUtil.get(khoa.nhan()));
        nhanLabel.setFont(nhanLabel.getFont().deriveFont(Font.BOLD));
        nhanLabel.setForeground(Theme.INK);
        duongDanRow.add(nhanLabel);
        duongDanRow.add(this.duongDanField);
        this.add(duongDanRow);

        this.duongDanField.addActionListener(
                e -> this.setDuongDan(this.duongDanField.getText().trim())
        );

        JPanel nutRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        nutRow.setOpaque(false);

        JButton chonAnhButton = new JButton(I18nReaderUtil.get(this.keyChonAnh));
        JButton nhapUrlButton = new JButton(I18nReaderUtil.get("app.common.nhapUrl"));
        JButton goAnhButton = new JButton(I18nReaderUtil.get(khoa.goAnh()));

        chonAnhButton.setMargin(new Insets(4, 8, 4, 8));
        nhapUrlButton.setMargin(new Insets(4, 8, 4, 8));
        goAnhButton.setMargin(new Insets(4, 8, 4, 8));

        chonAnhButton.addActionListener(e -> this.chonAnh());
        nhapUrlButton.addActionListener(e -> this.nhapUrl());
        goAnhButton.addActionListener(e -> this.clear());

        nutRow.add(chonAnhButton);
        nutRow.add(nhapUrlButton);
        nutRow.add(goAnhButton);
        this.add(nutRow);

        this.previewLabel.setPreferredSize(new Dimension(240, 160));
        this.previewLabel.setForeground(Theme.INK);
        this.previewLabel.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        this.previewLabel.setIcon(null);
        this.previewLabel.setText(I18nReaderUtil.get(this.keyChuaCoAnh));

        JPanel previewRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        previewRow.setOpaque(false);
        previewRow.add(this.previewLabel);
        this.add(previewRow);
    }

    public String getDuongDan() {
        return this.duongDanField.getText().trim();
    }

    public void setDuongDan(String duongDan) {
        this.duongDanField.setText(duongDan == null ? "" : duongDan);

        this.hienThiAnh(duongDan);
    }

    public void clear() {
        this.duongDanField.setText("");
        this.previewLabel.setIcon(null);
        this.previewLabel.setText(I18nReaderUtil.get(this.keyChuaCoAnh));
    }

    private void chonAnh() {
        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle(I18nReaderUtil.get(this.keyChonAnh));
        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        I18nReaderUtil.get(this.keyLoaiTepAnh),
                        "png", "jpg", "jpeg", "gif"
                )
        );

        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            this.setDuongDan(file.getAbsolutePath());
        }
    }

    private void nhapUrl() {
        String ketQua = (String) JOptionPane.showInputDialog(
                this,
                I18nReaderUtil.get("app.common.urlGoiY"),
                I18nReaderUtil.get("app.common.tieuDeNhapUrl"),
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                this.duongDanField.getText());

        if (ketQua != null) {
            this.setDuongDan(ketQua.trim());
        }
    }

    private void hienThiAnh(String duongDan) {
        if (duongDan == null || duongDan.isBlank()) {
            this.clear();
            return;
        }

        String dia = duongDan.trim();

        this.previewLabel.setIcon(null);
        this.previewLabel.setText("");

        SwingWorker<KetQuaTaiAnh, Void> worker = new SwingWorker<>() {
            @Override
            protected KetQuaTaiAnh doInBackground() {
                return taiAnh(dia);
            }

            @Override
            protected void done() {
                KetQuaTaiAnh ketQua;

                try {
                    ketQua = get();
                } catch (Exception e) {
                    ketQua = new KetQuaTaiAnh(null, false);
                }

                if (ketQua.icon() != null) {
                    previewLabel.setText("");
                    previewLabel.setIcon(ketQua.icon());
                } else {
                    previewLabel.setIcon(null);
                    previewLabel.setText(
                            I18nReaderUtil.get(
                                    ketQua.duLieuKhongPhaiAnh()
                                            ? keyDuLieuKhongPhaiAnh
                                            : keyKhongTaiDuocAnh
                            )
                    );
                }
            }
        };

        worker.execute();
    }

    private static KetQuaTaiAnh taiAnh(String duongDan) {
        try {
            BufferedImage bufferedImage = TaiAnhUtil.docAnh(duongDan, 5000, 10000);

            if (bufferedImage == null) {
                return new KetQuaTaiAnh(null, true);
            }

            Image image = bufferedImage.getScaledInstance(240, 160, Image.SCALE_SMOOTH);

            return new KetQuaTaiAnh(new ImageIcon(image), false);

        } catch (Exception e) {
            return new KetQuaTaiAnh(null, false);
        }
    }

    private static final class KetQuaTaiAnh {

        private final ImageIcon icon;
        private final boolean duLieuKhongPhaiAnh;

        private KetQuaTaiAnh(ImageIcon icon, boolean duLieuKhongPhaiAnh) {
            this.icon = icon;
            this.duLieuKhongPhaiAnh = duLieuKhongPhaiAnh;
        }

        private ImageIcon icon() {
            return this.icon;
        }

        private boolean duLieuKhongPhaiAnh() {
            return this.duLieuKhongPhaiAnh;
        }
    }
}
