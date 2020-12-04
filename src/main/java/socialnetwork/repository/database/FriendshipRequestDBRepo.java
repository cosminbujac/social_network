package socialnetwork.repository.database;

import socialnetwork.domain.FriendshipRequest;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendshipRequestDBRepo extends AbstractDatabaseRepository<Long, FriendshipRequest> {
    public FriendshipRequestDBRepo(Validator<FriendshipRequest> validator, String url, String username, String password, String table) throws SQLException {
        super(validator, url, username, password, table);
    }

    @Override
    protected FriendshipRequest extractEntity(ResultSet result) throws SQLException {
        Long id = result.getLong("id");
        Long sender = result.getLong("sender");
        Long receiver = result.getLong("receiver");
        String status= result.getString("status");
        Timestamp timestamp=result.getTimestamp("sent_at");
        LocalDateTime date = timestamp.toLocalDateTime();
        FriendshipRequest friendshipRequest= new FriendshipRequest(sender,receiver,status,date);
        friendshipRequest.setId(id);
        return friendshipRequest;
    }

    @Override
    protected void writeToDatabase(FriendshipRequest entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            String insert ="INSERT INTO "+table+" VALUES("+entity.getId()+", "+entity.getSender()+", "+entity.getReceiver()+", '"+entity.getStatus()+"', '"
                    +entity.getSent_at().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))+"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(insert);
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }
    }

    @Override
    protected void deleteFromDatabase(FriendshipRequest entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            Statement statement = connection.createStatement();
            String delete = "DELETE from "+table + " WHERE id=" +entity.getId();
            statement.executeUpdate(delete);

        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server ");
        }
    }

    public void updateStatus(FriendshipRequest friendshipRequest,String status)
    {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            Statement statement = connection.createStatement();
            String update = "UPDATE "+table + " SET status='" +status + "' WHERE id="+ friendshipRequest.getId();
            statement.executeUpdate(update);

        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server ");
        }
    }
}
