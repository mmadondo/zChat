package commmadondo.github.zchat;

import java.util.Date;

/**
 * Created by mmadondo on 4/23/2017.
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

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
