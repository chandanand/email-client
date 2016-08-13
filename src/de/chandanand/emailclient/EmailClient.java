package de.chandanand.emailclient;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by chand on 13/8/16.
 */
public class EmailClient {

    private JFrame mainFrame;
    private MessagesTableModel tableModel;
    private JTable table;
    private JTextArea messageTextArea;
    private JSplitPane splitPane;
    private JButton downloadButton, replyButton, forwardButton, deleteButton;
    private Message selectedMessage;
    private boolean deleting;
    private Session session;

    public EmailClient() {
        prepareGUI();
    }

    public void prepareGUI() {
        mainFrame = new JFrame("E-mail Management System");
        mainFrame.setSize(640, 480);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exitProgram();
            }
        });

        tableModel = new MessagesTableModel();
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(listSelectionEvent -> {
            try {
                tableSelectionChanged();
            } catch (MessagingException ex) {
                Logger.getLogger(EmailClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        mainFrame.setJMenuBar(createMenuBar());
        JPanel buttonPanel = createButtonPanel();
        JPanel mailPanel = createMailPanel();
        JPanel buttonPanel2 = createButtonPanel2();

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(buttonPanel, BorderLayout.NORTH);
        mainFrame.add(mailPanel, BorderLayout.CENTER);
        mainFrame.add(buttonPanel2, BorderLayout.SOUTH);

        splitPane.setDividerLocation(.5);
    }

    private void exitProgram() {
        System.exit(0);
    }

    private void tableSelectionChanged() throws MessagingException {
        if (!deleting) {
            selectedMessage = tableModel.getMessage(table.getSelectedRow());
            showSelectedMessage();
            updateButtons();
        }
    }

    private void showSelectedMessage() {
        mainFrame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            messageTextArea.setText(getMessageContent(selectedMessage));
            messageTextArea.setCaretPosition(0);
        } catch (Exception e) {
            showError("Unable to load message.", false);
        } finally {
            mainFrame.setCursor(Cursor.getDefaultCursor());
        }
    }

    private void updateButtons() throws MessagingException {
        if (selectedMessage != null) {
            if (selectedMessage.getContentType().contains("multipart"))
                downloadButton.setEnabled(true);
            replyButton.setEnabled(true);
            forwardButton.setEnabled(true);
            deleteButton.setEnabled(true);
        } else {
            downloadButton.setEnabled(true);
            replyButton.setEnabled(false);
            forwardButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    public static String getMessageContent(Message message)
            throws Exception {
        StringBuilder messageContent = new StringBuilder();
        String contentType = message.getContentType();
        Object content = message.getContent();

        if (contentType.contains("text/plain")
                || contentType.contains("text/html")) {
            if (content != null) {
                messageContent.append(content.toString());
            }
        }

        if (contentType.contains("multipart")) {
            Multipart multipart = (Multipart) content;
            if (multipart != null) {
                for (int i = 0; i < multipart.getCount(); i++) {
                    MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                    messageContent.append(part.getContent().toString());
                }
            }
            return messageContent.toString();
        } else {
            return String.valueOf(content);
        }
    }

    public static String getAttachmentContent(Message message)
            throws Exception {
        String contentType = message.getContentType();
        Object content = message.getContent();
        String attachFiles = "";

        if (contentType.contains("multipart")) {
            Multipart multipart = (Multipart) content;
            for (int i = 0; i < multipart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    String fileName = part.getFileName();
                    attachFiles += fileName + ", ";
                }
            }
        }
        if (attachFiles.length() > 1) {
            attachFiles = attachFiles.substring(0, attachFiles.length() - 2);
        }
        return attachFiles;
    }

    private JPanel createButtonPanel2() {
        JPanel buttonPanel2 = new JPanel();

        downloadButton = new JButton("Download");
        downloadButton.addActionListener(actionEvent -> {
            try {
                actionDownload();
            } catch (MessagingException | IOException ex) {
                Logger.getLogger(EmailClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        downloadButton.setEnabled(false);
        buttonPanel2.add(downloadButton);

        replyButton = new JButton("Reply");
        replyButton.addActionListener(actionEvent -> replyToMessage());
        replyButton.setEnabled(false);
        buttonPanel2.add(replyButton);

        forwardButton = new JButton("Forward");
        forwardButton.addActionListener(actionEvent -> forwardMessage());
        forwardButton.setEnabled(false);
        buttonPanel2.add(forwardButton);

        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(actionEvent -> {
            try {
                actionDelete();
            } catch (MessagingException ex) {
                Logger.getLogger(EmailClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        deleteButton.setEnabled(false);
        buttonPanel2.add(deleteButton);

        return buttonPanel2;
    }

    private JPanel createMailPanel() {
        JPanel emailsPanel = new JPanel();
        emailsPanel.setBorder(BorderFactory.createTitledBorder("E-mails"));

        messageTextArea = new JTextArea();
        messageTextArea.setEditable(false);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(table), new JScrollPane(messageTextArea));
        emailsPanel.setLayout(new BorderLayout());
        emailsPanel.add(splitPane, BorderLayout.CENTER);

        return emailsPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton newButton = new JButton("New Message");
        newButton.addActionListener(actionEvent -> sendNewMessage());
        buttonPanel.add(newButton);
        return buttonPanel;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(actionEvent -> exitProgram());

        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        return menuBar;
    }



    private void sendNewMessage() {
        sendMessage(MessageDialog.NEW, null);
    }

    private void replyToMessage() {
        sendMessage(MessageDialog.REPLY, selectedMessage);
    }

    private void forwardMessage() {
        sendMessage(MessageDialog.FORWARD, selectedMessage);
    }

    private void actionDownload() throws MessagingException, IOException {
        String contentType = selectedMessage.getContentType();
        String saveDirectory = JOptionPane.showInputDialog("Save Directory at: ");

        if (contentType.contains("multipart")) {
            Multipart multiPart = (Multipart) selectedMessage.getContent();
            for (int i = 0; i < multiPart.getCount(); i++) {
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    String fileName = part.getFileName();
                    part.saveFile(saveDirectory + File.separator + fileName);
                }
            }
        }

        JOptionPane.showMessageDialog(mainFrame, "Downloaded all attachments to " + saveDirectory);
    }

    private void actionDelete() throws MessagingException {
        deleting = true;

        try {
            selectedMessage.setFlag(Flags.Flag.DELETED, true);
            Folder folder = selectedMessage.getFolder();
            folder.close(true);
            folder.open(Folder.READ_WRITE);
        } catch (Exception e) {
            showError("Unable to delete message.", false);
        }

        tableModel.deleteMessage(table.getSelectedRow());

        messageTextArea.setText("");
        deleting = false;
        selectedMessage = null;
        updateButtons();
    }

    private void sendMessage(int type, Message message) {
        MessageDialog dialog;
        try {
            dialog = new MessageDialog(mainFrame, type, message);
            if (!dialog.display()) {
                // Return if dialog was cancelled.
                return;
            }
        } catch (Exception e) {
            showError("Unable to send message.", false);
            return;
        }

        try {
            MimeMessage newMessage = new MimeMessage(session);
            newMessage.setFrom(new InternetAddress(dialog.getFrom()));
            newMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(dialog.getTo()));
            newMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(dialog.getCc()));
            newMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(dialog.getBcc()));
            newMessage.setSubject(dialog.getSubject());
            newMessage.setSentDate(new Date());

            BodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(dialog.getContent());

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            File[] attachFiles = dialog.getFileNames();
            if (attachFiles != null && attachFiles.length > 0) {
                for (File aFile : attachFiles) {
                    MimeBodyPart attachPart = new MimeBodyPart();

                    try {
                        attachPart.attachFile(aFile);
                    } catch (IOException ex) {
                        throw ex;
                    }

                    multipart.addBodyPart(attachPart);
                }
            }
            newMessage.setContent(multipart);
            Transport.send(newMessage);
        } catch (Exception e) {
            showError("Unable to send message.", false);
        }
    }

    public void connect() {
        ConnectDialog dialog = new ConnectDialog(mainFrame);
        dialog.setVisible(true);

        final DownloadingDialog downloadingDialog = new DownloadingDialog(mainFrame);
        SwingUtilities.invokeLater(() -> downloadingDialog.setVisible(true));

        Store store = null;
        try {
            Properties props = new Properties();
            props.put("mail.pop3.host", dialog.getServer());
            props.put("mail.pop3.starttls.enable", true);
            props.put("mail.pop3.user", dialog.getUsername());
            props.put("mail.pop3.password", dialog.getPassword());
            props.put("mail.pop3.port", "995");
            props.put("mail.pop3.auth", true);
            props.put("mail.pop3.timeout", 5000);

            session = Session.getDefaultInstance(props, null);

            store = session.getStore("pop3s");
            store.connect(dialog.getServer(), dialog.getUsername(), dialog.getPassword());
        } catch (Exception e) {
            downloadingDialog.dispose();
            e.printStackTrace();
            showError("Unable to connect.", true);
        }

        try {
            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_WRITE);
            Message[] messages = folder.getMessages();
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            folder.fetch(messages, profile);
            tableModel.setMessages(messages);
        } catch (Exception e) {
            downloadingDialog.dispose();
            showError("Unable to download messages.", true);
        }
        downloadingDialog.dispose();
    }

    private void showError(String message, boolean exit) {
        JOptionPane.showMessageDialog(mainFrame, message, "Error",
                JOptionPane.ERROR_MESSAGE);
        if (exit)
            System.exit(0);
    }

    public void start() {
        mainFrame.setVisible(true);
    }
}
