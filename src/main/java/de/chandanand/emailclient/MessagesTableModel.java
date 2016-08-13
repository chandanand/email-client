package de.chandanand.emailclient;

import javax.mail.Address;
import javax.mail.Message;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chand on 13/8/16.
 */
public class MessagesTableModel extends AbstractTableModel {

    private static final String[] columnNames = {"Sender", "Subject", "Date"};

    private List<Message> messageList = new ArrayList();

    public void setMessages(Message[] messages) {
        for (int i = messages.length - 1; i >= 0; i--)
            messageList.add(messages[i]);
        fireTableDataChanged();
    }

    public Message getMessage(int row) {
        return messageList.get(row);
    }

    public void deleteMessage(int row) {
        messageList.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return messageList.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        try {
            Message message = messageList.get(row);
            switch (col) {
                case 0: // Sender
                    Address[] senders = message.getFrom();
                    if (senders != null || senders.length > 0) {
                        return senders[0].toString();
                    } else {
                        return "[none]";
                    }
                case 1: // Subject
                    String subject = message.getSubject();
                    if (subject != null && subject.length() > 0) {
                        return subject;
                    } else {
                        return "[none]";
                    }
                case 2: // Date
                    Date date = message.getSentDate();
                    if (date != null) {
                        return date.toString();
                    } else {
                        return "[none]";
                    }
            }
        } catch (Exception e) {
            return "";
        }

        return "";
    }
}
