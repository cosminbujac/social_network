package socialnetwork.domain;

import java.time.format.DateTimeFormatter;

public class FriendshipRequestDTO {
    FriendshipRequest friendshipRequest;
    Utilizator sender;

    public FriendshipRequestDTO(FriendshipRequest friendshipRequest, Utilizator sender) {
        this.friendshipRequest = friendshipRequest;
        this.sender = sender;
    }

    public FriendshipRequest getFriendshipRequest() {
        return friendshipRequest;
    }

    public Utilizator getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return sender.getFullName()+" | "+friendshipRequest.getSent_at().format(DateTimeFormatter.ofPattern("dd-MM-yyyy' at 'HH:mm:ss")) + " | status:"+ friendshipRequest.getStatus();
    }
}
