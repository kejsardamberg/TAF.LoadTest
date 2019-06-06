package se.claremont.taf.performance.gui.guiProfiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TafFrame extends JFrame {
    public void setInitialFocus(Component component){
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                super.windowOpened(e);
                component.requestFocus();
            }
        });
    }
}
