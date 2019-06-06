package se.claremont.taf.performance.gui.guiProfiles;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TafDialog extends JDialog {

    public TafDialog(JFrame parentFrame, String title){
        super(parentFrame, title, true);
    }

    public TafDialog(Window parent, String title){
        super(parent, title);
    }

    public TafDialog(TafDialog frame, String title) {
        super(frame, title, true);
    }

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
