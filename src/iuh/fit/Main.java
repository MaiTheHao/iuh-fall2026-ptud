package iuh.fit;

import iuh.fit.gui.GUI;
import iuh.fit.gui.Theme;
import iuh.fit.service.DayMetricService;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws Exception {
        Theme.install();
        try {
            DayMetricService.getInstance().nap();
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Khong nap duoc DayMetric luc khoi dong", ex);
        }
        GUI gui = new GUI();
        gui.start();
    }
}
