package iuh.fit.gui.component;

import iuh.fit.gui.Theme;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class TheBoGoc extends JPanel {

    private final int boGoc;

    public TheBoGoc(int boGoc) {
        this.boGoc = boGoc;
        this.setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D doHoa = (Graphics2D) g.create();
        try {
            doHoa.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            doHoa.setColor(Theme.palette().PANEL());
            doHoa.fillRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1,
                    this.boGoc, this.boGoc);
            doHoa.setColor(Theme.palette().BORDER());
            doHoa.drawRoundRect(0, 0, this.getWidth() - 1, this.getHeight() - 1,
                    this.boGoc, this.boGoc);
        } finally {
            doHoa.dispose();
        }
    }
}
