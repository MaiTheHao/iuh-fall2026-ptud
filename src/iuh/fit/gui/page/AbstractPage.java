package iuh.fit.gui.page;

import iuh.fit.enumeration.MenuGroup;

import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.util.function.Consumer;

public abstract class AbstractPage extends JPanel {

    private Consumer<Class<? extends AbstractPage>> navigator;

    public final void setNavigator(Consumer<Class<? extends AbstractPage>> navigator) {
        this.navigator = navigator;
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

    public KeyStroke getShortcut() {
        return null;
    }

    public boolean hasSeparatorBefore() {
        return false;
    }
}
