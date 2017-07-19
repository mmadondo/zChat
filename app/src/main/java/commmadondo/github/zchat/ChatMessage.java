package commmadondo.github.zchat;

import java.util.Date;

/**
 * Created by mmadondo on 4/23/2017. The messages show in a public  group chat.
 
 Will try to implement this in iOS via swift
 */

public class ChatMessage {
    //MainActivity mainAct;
    private String messageText;
    private String messageUser;
    private long messageTime;
   // private String messageID;

    public ChatMessage(String messageText, String messageUser) {
        this.messageText = messageText;
        this.messageUser = messageUser;
       // this.messageID = messageID;

        messageTime = new Date().getTime(); // Initialize to current time
    }

    // necessary for Firebase's deserializer
    public ChatMessage(){

    }

    /**
     *
     * @return text message
     */
    public String getMessageText() {
        return messageText;
    }

    /**
     *
     * @param messageText the user's text
     */
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     *
     * @return user who sent message
     */
    public String getMessageUser() {
        return messageUser;
    }

    /**
     *
     * @param messageUser
     */
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    /**
     * Get time when message was received
     * @return time
     */
    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

 /*   public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;

    }*/
}
