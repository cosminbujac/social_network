package socialnetwork.domain.validators;

import socialnetwork.domain.FriendshipRequest;

public class FriendshipRequestValidator implements Validator<FriendshipRequest>{
    @Override
    public void validate(FriendshipRequest entity) throws ValidException {
        if(entity.getId()==null)
            throw new ValidException("Id ul requestului este null!");
        if(entity.getReceiver()==null || entity.getSender()==null)
            throw new ValidException("The users cannot be null!");
    }
}
