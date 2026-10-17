package iuh.fit.gui.component;

import iuh.fit.util.I18nReaderUtil;

import javax.imageio.ImageIO;
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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URLConnection;

public class AnhField extends JPanel {

    private static final long serialVersionUID = 1L;

    private final String keyChonAnh;
    private final String keyLoaiTepAnh;
    private final String keyChuaCoAnh;
    private final String keyKhongTaiDuocAnh;
    private final String keyDuLieuKhongPhaiAnh;

    private final JTextField duongDanField = new JTextField(14);
    private final JLabel previewLabel = new JLabel("", SwingConstants.CENTER);

    public AnhField(
            String keyNhan,
            String keyChonAnh,
            String keyGoAnh,
            String keyChuaCoAnh,
            String keyKhongTaiDuocAnh,
            String keyLoaiTepAnh,
            String keyDuLieuKhongPhaiAnh) {

        this.keyChonAnh = keyChonAnh;
        this.keyLoaiTepAnh = keyLoaiTepAnh;
        this.keyChuaCoAnh = keyChuaCoAnh;
        this.keyKhongTaiDuocAnh = keyKhongTaiDuocAnh;
        this.keyDuLieuKhongPhaiAnh = keyDuLieuKhongPhaiAnh;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setOpaque(false);

        JPanel duongDanRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        duongDanRow.setOpaque(false);
        duongDanRow.add(new JLabel(I18nReaderUtil.get(keyNhan)));
        duongDanRow.add(this.duongDanField);
        this.add(duongDanRow);

        this.duongDanField.addActionListener(
                e -> this.setDuongDan(this.duongDanField.getText().trim())
        );

        JPanel nutRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 2));
        nutRow.setOpaque(false);

        JButton chonAnhButton = new JButton(I18nReaderUtil.get(keyChonAnh));
        JButton nhapUrlButton = new JButton(I18nReaderUtil.get("app.common.nhapUrl"));
        JButton goAnhButton = new JButton(I18nReaderUtil.get(keyGoAnh));

        chonAnhButton.addActionListener(e -> this.chonAnh());
        nhapUrlButton.addActionListener(e -> this.nhapUrl());
        goAnhButton.addActionListener(e -> this.clear());

        nutRow.add(chonAnhButton);
        nutRow.add(nhapUrlButton);
        nutRow.add(goAnhButton);
        this.add(nutRow);

        this.previewLabel.setPreferredSize(new Dimension(180, 120));
        this.previewLabel.setBorder(BorderFactory.createEtchedBorder());
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
            BufferedImage bufferedImage;

            if (duongDan.startsWith("https://") || duongDan.startsWith("http://")) {
                URLConnection connection = URI.create(duongDan.trim()).toURL().openConnection();

                connection.setConnectTimeout(5000);
                connection.setReadTimeout(10000);

                try (var input = connection.getInputStream()) {
                    bufferedImage = ImageIO.read(input);
                }
            } else {
                bufferedImage = ImageIO.read(new File(duongDan.trim()));
            }

            if (bufferedImage == null) {
                return new KetQuaTaiAnh(null, true);
            }

            Image image = bufferedImage.getScaledInstance(180, 120, Image.SCALE_SMOOTH);

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
