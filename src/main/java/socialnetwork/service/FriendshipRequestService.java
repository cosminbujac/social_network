package socialnetwork.service;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipRequestDBRepo;

import java.util.ArrayList;
import java.util.List;

public class FriendshipRequestService {
    private final Repository<Tuple<Long, Long>, Prietenie> friendshipRepo;
    private final FriendshipRequestDBRepo friendshipRequestRepo;

    public FriendshipRequestService(Repository<Tuple<Long, Long>, Prietenie> friendshipRepo, FriendshipRequestDBRepo friendshipRequestRepo) {
        this.friendshipRepo = friendshipRepo;
        this.friendshipRequestRepo = friendshipRequestRepo;
    }

    public FriendshipRequest addFriendshipRequest(Long sender, Long receiver)
    {
        friendshipRequestRepo.findAll().forEach(x->{
            if(x.getSender().equals(sender)&&x.getReceiver().equals(receiver))
                if( x.getStatus().equals("pending"))
                    throw new ValidException("There already is a pending friendship request!");
                else if(x.getStatus().equals("rejected")) {
                    x.setStatus("pending");
                    return ;
                }
                else
                    throw new ValidException("The friendship already exists!");
        });
        FriendshipRequest friendshipRequest = new FriendshipRequest(sender,receiver);
        friendshipRequest.setId(getNewID());
        return friendshipRequestRepo.save(friendshipRequest);
    }

    /**
     *
     * @param receiver - the user that receives the friendship request
     * @return list of friendship requests sent to him
     */
    public List<FriendshipRequest> getFriendshipRequests(Long receiver){
        List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
        friendshipRequestRepo.findAll().forEach(x->{
            if(x.getReceiver().equals(receiver))
                requests.add(x);
        });
        return  requests;
    }

    public List<FriendshipRequest> getSentRequests(Long sender){
        List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
        friendshipRequestRepo.findAll().forEach(x->{
            if(x.getSender().equals(sender))
                requests.add(x);
        });
        return  requests;
    }

    public List<FriendshipRequest> getFriendshipRequestsInvolving(Long userID){
        List<FriendshipRequest> requests = new ArrayList<FriendshipRequest>();
        friendshipRequestRepo.findAll().forEach(x->{
            if(x.getReceiver().equals(userID)||x.getSender().equals(userID))
                requests.add(x);
        });
        return  requests;
    }

    /**
     *
     * @param id - the id of the friendship
     * @param option - 1 for accept, 0 for reject
     * @return the frienship if accepted, null if rejected
     */
    public Prietenie manageFriendshipRequest(Long id, Integer option){
        FriendshipRequest friendshipRequest = friendshipRequestRepo.findOne(id);
        if(friendshipRequest==null)
            throw new ValidException("The request doesn't exist!");
        if(option.equals(1))
        {
            friendshipRequest.setStatus("approved");
            friendshipRequestRepo.updateStatus(friendshipRequest,"approved");
            return new Prietenie(friendshipRequest.getSender(),friendshipRequest.getReceiver());
        }
        else{
            friendshipRequest.setStatus("rejected");
            friendshipRequestRepo.updateStatus(friendshipRequest,"rejected");
            return null;
        }
    }

    private Long getNewID()
    {
        Long id=-1L;
        for (FriendshipRequest x : friendshipRequestRepo.findAll())
            if(x.getId()>id)
                id=x.getId();
        return  id+1;
    }

    public void deleteUsersRequests(Long id) {
        List<Long> ids=new ArrayList<Long>();
        friendshipRequestRepo.findAll().forEach(x->{
            if(x.getReceiver().equals(id)||x.getSender().equals(id))
                ids.add(x.getId());
        });
        for (Long aLong : ids) {
            friendshipRequestRepo.delete(aLong);
        }
    }
    public FriendshipRequest deleteRequest(Long user1,Long user2){
        FriendshipRequest lookedOne=null;
        for (FriendshipRequest friendshipRequest : friendshipRequestRepo.findAll()) {
            if(friendshipRequest.getReceiver().equals(user1)&&friendshipRequest.getSender().equals(user2) ||friendshipRequest.getReceiver().equals(user2)&&friendshipRequest.getSender().equals(user1)) {
                lookedOne = friendshipRequest;
                break;
            }
        }
        if(lookedOne==null)
            throw new ValidException("The friendship request doesnt exist!");
        return friendshipRequestRepo.delete(lookedOne.getId());
    }
    public FriendshipRequest deleteRequest(Long reqID)
    {
        return friendshipRequestRepo.delete(reqID);
    }

}
