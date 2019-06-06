package se.claremont.taf.performance.gui;

import se.claremont.taf.performance.loadagent.LoadAgent;
import se.claremont.taf.performance.TestRunner;
import se.claremont.taf.performance.gui.guiProfiles.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoadAgentAdditionWindow {

    TafTextField ipAddressField;
    TafDialog dialog;

    public LoadAgentAdditionWindow(LoadAgentsDialog window) {
        dialog = new TafDialog(window.frame, "TAF - Add load agent");
        dialog.getContentPane().setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
        TafButton okButton = new TafButton("Ok");
        //okButton.setEnabled(false);
        TafPanel dataPanel = new TafPanel();
        dataPanel.setLayout(new GridLayout(3, 2));
        dataPanel.add(new TafLabel("Load agent name:"));
        TafTextField nameField = new TafTextField();
        dataPanel.add(nameField);
        dataPanel.add(new TafLabel("Load agent IP-address:"));
        ipAddressField = new TafTextField();
        dataPanel.add(ipAddressField);

        dataPanel.add(new TafLabel("Load agent IP-port:"));
        TafTextField portField = new TafTextField();
        portField.setText("4212");
        dataPanel.add(portField);
        dialog.getContentPane().add(dataPanel);
        TafPanel buttonPanel = new TafPanel();
        buttonPanel.setLayout(new FlowLayout());
        TafButton cancelButton = new TafButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        buttonPanel.add(cancelButton);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                if(name.length() == 0) name = ipAddressField.getText();
                TestRunner.getInstance().loadAgentList.addAgent(new LoadAgent(name, ipAddressField.getText()));
                window.updateList();
                dialog.dispose();
            }
        });
        buttonPanel.add(okButton);
        dialog.getContentPane().add(buttonPanel);
        dialog.pack();
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation(dim.width/2-dialog.getSize().width/2, dim.height/2-dialog.getSize().height/2);
        dialog.setVisible(true);
    }
}
