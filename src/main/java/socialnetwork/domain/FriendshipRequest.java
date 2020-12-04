package socialnetwork.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class FriendshipRequest extends Entity<Long> {
    Long sender,receiver;
    String status;
    LocalDateTime sent_at;

    public FriendshipRequest(Long sender, Long receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = "pending";
        sent_at=LocalDateTime.now();
    }

    public LocalDateTime getSent_at() {
        return sent_at;
    }

    public void setSent_at(LocalDateTime sent_at) {
        this.sent_at = sent_at;
    }

    public FriendshipRequest(Long sender, Long receiver, String status, LocalDateTime dateTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = status;
        sent_at=dateTime;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return "Utilizatorul cu id-ul: " +
                 + sender +
                " a cerut prietenia utilizatorului cu id-ul: " + receiver +
                " | status cerere: " + status;
    }

    public Long getReceiver() {
        return receiver;
    }

    public String getStatus() {
        return status;
    }
}
