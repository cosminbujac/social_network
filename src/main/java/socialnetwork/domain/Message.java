package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long> {
    Long from;
    List<Long> to;
    String text;
    LocalDateTime dateTime;
    Long replyId;
    Long conversationId=-1L;
    String conversationName;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Message(Long from, List<Long> to, String text,String conversationName) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.dateTime = LocalDateTime.now();
        this.replyId = null;
        this.conversationName=conversationName;

    }

    public String getConversationName() {
        return conversationName;
    }

    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }

    public Message(Long from, List<Long> to, String text, Long replyId) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.dateTime = LocalDateTime.now();
        this.replyId = replyId;
        this.conversationName=conversationName;
    }

    public Message(Long from, List<Long> to, String text, LocalDateTime dateTime, Long replyId,String conversationName) {
        this.from = from;
        this.to = to;
        this.text = text;
        this.dateTime = dateTime;
        this.replyId = replyId;
        this.conversationName = conversationName;
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public List<Long> getTo() {
        return to;
    }

    public void setTo(List<Long> to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId =replyId;
    }

    @Override
    public String toString() {
        return conversationName;
    }
}

