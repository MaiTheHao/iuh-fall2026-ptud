package iuh.fit.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import iuh.fit.core.config.QrConfig;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import javax.imageio.ImageIO;

public final class VietQrClient {

    private static final String ENDPOINT = "https://api.vietqr.io/v2/generate";
    private static final int SO_LAN_THU_TOI_DA = 2;

    private VietQrClient() {
    }

    public static BufferedImage taoQr(long soTien, String noiDung) {
        String bin = QrConfig.bin();
        String soTk = QrConfig.soTk();
        String tenTk = QrConfig.tenTk();
        if (bin == null || soTk == null || tenTk == null) {
            return null;
        }
        for (int lan = 0; lan < SO_LAN_THU_TOI_DA; lan++) {
            try {
                BufferedImage anh = goiMotLan(bin, soTk, tenTk, soTien, noiDung);
                if (anh != null) {
                    return anh;
                }
            } catch (Exception ex) {
                continue;
            }
        }
        return null;
    }

    private static BufferedImage goiMotLan(String bin, String soTk, String tenTk, long soTien,
            String noiDung) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        JsonObject than = new JsonObject();
        than.addProperty("acqId", bin);
        than.addProperty("accountNo", soTk);
        than.addProperty("accountName", tenTk);
        than.addProperty("amount", soTien);
        than.addProperty("addInfo", noiDung == null ? "" : noiDung);
        than.addProperty("template", "compact2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ENDPOINT))
                .timeout(Duration.ofSeconds(8))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(than.toString(), StandardCharsets.UTF_8))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 || response.body() == null) {
            return null;
        }
        JsonObject ketQua = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!ketQua.has("code") || !"00".equals(ketQua.get("code").getAsString())) {
            return null;
        }
        JsonObject duLieu = ketQua.has("data") && ketQua.get("data").isJsonObject()
                ? ketQua.getAsJsonObject("data")
                : null;
        if (duLieu == null || !duLieu.has("qrDataURL")) {
            return null;
        }
        String duongDan = duLieu.get("qrDataURL").getAsString();
        int viTriBase64 = duongDan.indexOf("base64,");
        String chuoiAnh = viTriBase64 >= 0 ? duongDan.substring(viTriBase64 + 7) : duongDan;
        byte[] bytesAnh = Base64.getDecoder().decode(chuoiAnh.trim());
        try (ByteArrayInputStream dauVao = new ByteArrayInputStream(bytesAnh)) {
            return ImageIO.read(dauVao);
        }
    }
}
