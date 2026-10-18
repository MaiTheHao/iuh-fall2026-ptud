package iuh.fit;

import iuh.fit.core.context.AppContext;
import iuh.fit.dao.NhanVienDAO;
import iuh.fit.gui.GUI;
import iuh.fit.gui.theme.Theme;

public class Main {
	private static final NhanVienDAO DAO = new NhanVienDAO();
    public static void main(String[] args) throws Exception {
        Theme.install();
        GUI gui = new GUI();
        AppContext.getInstance().setCurrentUser(DAO.findById("NV011120250001").orElseThrow());
        gui.start();
    }
}
