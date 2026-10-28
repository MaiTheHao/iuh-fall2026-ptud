package iuh.fit;

import iuh.fit.gui.GUI;
import iuh.fit.gui.theme.Theme;

public class Main {
    public static void main(String[] args) throws Exception {
        Theme.install();
        GUI gui = new GUI();
        gui.start();
    }
}
