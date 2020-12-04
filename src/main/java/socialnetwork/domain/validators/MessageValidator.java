package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidException {
        if(entity.getFrom()==null)
            throw new ValidException("Senderul nu poate fi null");
        if(entity.getText().equals(""))
            throw new ValidException("Mesajul nu poate fi vid");
        if(entity.getTo().size()==0)
            throw new ValidException("Nu exista utilizatori carora sa le trimiteti mesajul");
    }
}
