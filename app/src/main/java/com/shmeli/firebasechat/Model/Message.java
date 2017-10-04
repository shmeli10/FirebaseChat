package com.shmeli.firebasechat.Model;

import java.util.Date;

/**
 * Created by Serghei Ostrovschi on 10/3/17.
 */

public class Message {

    private String textMessage;
    private String author;

    private long timeMessage;

    private boolean isUnread;

    //public Message(String textMessage, String author) {
    public Message(String textMessage, String author, boolean isUnread) {
        this.textMessage    = textMessage;
        this.author         = author;
        this.isUnread       = isUnread;

        timeMessage = new Date().getTime();

        // isUnread = true;
    }

    public Message() {
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getTimeMessage() {
        return timeMessage;
    }

    public void setTimeMessage(long timeMessage) {
        this.timeMessage = timeMessage;
    }

    public boolean isUnread() {
        return isUnread;
    }

    public void setUnread(boolean unread) {
        isUnread = unread;
    }
}
