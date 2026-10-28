package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;
import iuh.fit.gui.theme.Theme;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public abstract class AbstractPage extends JPanel {

    private Consumer<Class<? extends AbstractPage>> navigator;

    public final void setNavigator(Consumer<Class<? extends AbstractPage>> navigator) {
        this.navigator = navigator;
    }

    protected final JLabel taoNhanTrang(String text) {
        JLabel nhan = new JLabel(text);
        nhan.setFont(Theme.fieldLabelFont());
        nhan.setForeground(Theme.INK);
        return nhan;
    }

    protected final void goTo(Class<? extends AbstractPage> page) {
        if (navigator != null) {
            navigator.accept(page);
        }
    }

    public abstract void render();

    public abstract String getTitleKey();

    public MenuGroup getGroup() {
        return null;
    }

    public boolean hasSeparatorBefore() {
        return false;
    }

    protected final void clearActionListeners(AbstractButton... buttons) {
        for (AbstractButton button : buttons) {
            for (ActionListener listener : button.getActionListeners()) {
                button.removeActionListener(listener);
            }
        }
    }

    protected final void clearActionListeners(JComboBox<?>... combos) {
        for (JComboBox<?> combo : combos) {
            for (ActionListener listener : combo.getActionListeners()) {
                combo.removeActionListener(listener);
            }
        }
    }
}
