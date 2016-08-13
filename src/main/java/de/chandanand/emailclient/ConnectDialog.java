package de.chandanand.emailclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ConnectDialog extends JDialog {
    private static final String[] TYPES = {"pop3", "imap"};

    private JTextField usernameTextField, serverTextField, smtpServerTextField;
    private JPasswordField passwordField;

    public ConnectDialog(Frame parent) {
        super(parent, true);
        setTitle("Connect");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                actionCancel();
            }
        });

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getSettingsPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel getSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Connection Settings"));
        GridBagLayout layout = new GridBagLayout();
        settingsPanel.setLayout(layout);

        JLabel typeLabel = new JLabel("Type:");
        layout.setConstraints(typeLabel, getTypeLabelConstraints());
        settingsPanel.add(typeLabel);

        JComboBox typeComboBox = new JComboBox(TYPES);
        layout.setConstraints(typeComboBox, getTypeBoxConstraints());
        settingsPanel.add(typeComboBox);

        JLabel serverLabel = new JLabel("Server:");
        layout.setConstraints(serverLabel, getServerLabelConstraints());
        settingsPanel.add(serverLabel);

        serverTextField = new JTextField(25);
        layout.setConstraints(serverTextField, getServerTextFieldConstraints());
        settingsPanel.add(serverTextField);

        JLabel usernameLabel = new JLabel("Username:");
        layout.setConstraints(usernameLabel, getUsernameLabelConstraints());
        settingsPanel.add(usernameLabel);

        usernameTextField = new JTextField();
        layout.setConstraints(usernameTextField, getUsernameTextFieldConstraints());
        settingsPanel.add(usernameTextField);

        JLabel passwordLabel = new JLabel("Password:");
        layout.setConstraints(passwordLabel, getPasswordConstraints());
        settingsPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        layout.setConstraints(passwordField, getPasswordFieldConstraints());
        settingsPanel.add(passwordField);

        JLabel smtpServerLabel = new JLabel("SMTP Server:");
        layout.setConstraints(smtpServerLabel, getSmtpServerLabelConstraints());
        settingsPanel.add(smtpServerLabel);

        smtpServerTextField = new JTextField(25);
        layout.setConstraints(smtpServerTextField, getSmtpServerTextFieldConstraints());
        settingsPanel.add(smtpServerTextField);
        return settingsPanel;
    }

    private JPanel getButtonPanel() {
        JPanel buttonsPanel = new JPanel();
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> actionConnect());
        buttonsPanel.add(connectButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> actionCancel());
        buttonsPanel.add(cancelButton);
        return buttonsPanel;
    }

    private GridBagConstraints getSmtpServerTextFieldConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = 1.0D;
        return constraints;
    }

    private GridBagConstraints getSmtpServerLabelConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 5, 0);
        return constraints;
    }

    private GridBagConstraints getPasswordFieldConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.weightx = 1.0D;
        return constraints;
    }

    private GridBagConstraints getPasswordConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 5, 0);
        return constraints;
    }

    private GridBagConstraints getUsernameTextFieldConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.weightx = 1.0D;
        return constraints;
    }

    private GridBagConstraints getUsernameLabelConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        return constraints;
    }

    private GridBagConstraints getServerTextFieldConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.weightx = 1.0D;
        return constraints;
    }

    private GridBagConstraints getServerLabelConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        return constraints;
    }

    private GridBagConstraints getTypeBoxConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.weightx = 1.0D;
        return constraints;
    }

    private GridBagConstraints getTypeLabelConstraints() {
        GridBagConstraints constraints;
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        return constraints;
    }

    private void actionConnect() {
        if (serverTextField.getText().trim().length() < 1
                || usernameTextField.getText().trim().length() < 1
                || passwordField.getPassword().length < 1
                || smtpServerTextField.getText().trim().length() < 1) {
            JOptionPane.showMessageDialog(this,
                    "One or more settings is missing.",
                    "Missing Setting(s)", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();
    }

    private void actionCancel() {
        System.exit(0);
    }

    public String getServer() {
        return serverTextField.getText();
    }

    public String getUsername() {
        return usernameTextField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }
}