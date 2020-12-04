package socialnetwork.service;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.repository.Repository;

import java.sql.ClientInfoStatus;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private final Repository<Long, Message> messageRepository;

    public MessageService(Repository<Long, Message> messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Sends a new message in a conversation or creates a new conversation with a first message
     * @param message - the message to be sent
     * @return null if the message is sent, the message otherwise
     */
    public Message trimiteMesaj(Message message)
    {
        message.setId(getNewID());
        if(message.getReplyId()==null)
            message.setConversationId(message.getId());
        else {
            message.setConversationName(getOne(message.getReplyId()).getConversationName());
            message.setConversationId(getOne(message.getReplyId()).getConversationId());
        }
        return messageRepository.save(message);
    }

    /**
     * Returns a new id for a message or conversation
     * @return new ID
     */
    private Long getNewID()
    {
        Long id=-1L;
        for (Message x : messageRepository.findAll())
            if(x.getId()>id)
                id=x.getId();
        return  id+1;
    }

    /**
     * Returns a message with a certain id
     * @param id the id of a message
     * @return message
     */
    public Message getOne(Long id){
        return messageRepository.findOne(id);
    }

    /**
     * Returns all the messages sent by everyone
     * @return Iterable of messages
     */
    public Iterable<Message> getAll(){
        return messageRepository.findAll();
    }

    /**
     * Returns the list of a certain users messages
     * @param id - the id of the user
     * @return List of messages of certain user
     */
    public List<Message> findUserMessages(Long id){
        List<Message> messages = new ArrayList<Message>();
        messageRepository.findAll().forEach(x->{
            if(x.getFrom().equals(id))
                messages.add(x);
            else
            for (Long aLong : x.getTo()) {
                if(aLong.equals(id))
                {
                    messages.add(x);
                    break;
                }
            }
        });
        return messages;
    }

    /**
     * Find all the conversations of a certain user
     * @param id - the id of the user
     * @return List of all the conversations
     */
    public List<Tuple<Long,String>> getAllConvsOf(Long id)
    {
        List<Tuple<Long,String>> convos=new ArrayList<Tuple<Long,String>>();
        findUserMessages(id).stream().forEach(x->{
            Tuple<Long,String> newConv=new Tuple<Long,String>(x.getConversationId(),x.getConversationName());
            if(!convos.contains(newConv))
                convos.add(newConv);
        });
        return convos;
    }

    public List<Message> getConversation(Long conversationID){
        List<Message>messages = new ArrayList<>();
        getAll().forEach(x->{
            if(x.getConversationId().equals(conversationID))
                messages.add(x);
        });
        return messages;
    }
}
