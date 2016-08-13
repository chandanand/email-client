package de.chandanand.emailclient;

import javax.swing.*;
import java.awt.*;

public class DownloadingDialog extends JDialog {

    public DownloadingDialog(Frame parent) {
        super(parent, true);
        setTitle("E-mail Client");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPane.add(new JLabel("Downloading messages..."));
        setContentPane(contentPane);

        pack();
        setLocationRelativeTo(parent);
    }
}