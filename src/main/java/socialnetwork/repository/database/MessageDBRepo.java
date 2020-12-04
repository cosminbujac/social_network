package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepo extends AbstractDatabaseRepository<Long, Message> {
    public MessageDBRepo(Validator<Message> validator, String url, String username, String password, String table) throws SQLException {
        super(validator, url, username, password, table);
    }

    @Override
    protected Message extractEntity(ResultSet result) throws SQLException {
        Timestamp timestamp = null;
        Long id = result.getLong("id");
        Long sender = result.getLong("sender");
        String text = result.getString("mesaj");
        timestamp=result.getTimestamp("data");
        LocalDateTime date = timestamp.toLocalDateTime();
        Long conversationId=result.getLong("conversation_id");
        Long replyId = result.getLong("answer_to");
        String conversationName= result.getString("conversation_name");
        String getAll = "SELECT * from message_to WHERE id_mesaj="+id;
        Statement statement = connection.createStatement();
        ResultSet resultTo = statement.executeQuery(getAll);
        List<Long> to=extractTo(resultTo);
        Message message = new Message(sender,to,text,date,replyId,conversationName);
        message.setConversationId(conversationId);
        message.setId(id);
        return message;
    }
    private ArrayList<Long> extractTo(ResultSet resultTo) throws SQLException {
        ArrayList<Long> to= new ArrayList<Long>();
        while(resultTo.next())
        {
            Long id=resultTo.getLong("receiver");
            to.add(id);
        }
        return to;
    }

    @Override
    protected void writeToDatabase(Message entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            Long replyId=entity.getReplyId();
            String insert ="INSERT INTO "+table+" VALUES("+entity.getId()+", "+entity.getFrom()+", '"+entity.getText()+
                    "', '"+entity.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))+"', "+replyId + ", "+entity.getConversationId()+", '"+
                    entity.getConversationName()+"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(insert);
            entity.getTo().forEach(x->{
                String newInsert="INSERT INTO message_to VALUES("+entity.getId()+", "+x+")";
                try {
                    statement.executeUpdate(newInsert);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            });
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }

    }

    @Override
    protected void deleteFromDatabase(Message entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            Statement statement = connection.createStatement();
            String delete = "DELETE from "+table + " WHERE id=" +entity.getId();
            String delete2="DELETE from message_to WHERE id_mesaj=" +entity.getId();
            statement.executeUpdate(delete);

        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }

    }
}
