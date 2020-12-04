package socialnetwork.repository.database;

import jdk.vm.ci.meta.Local;
import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;

import javax.naming.ldap.PagedResultsControl;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FriendshipDatabaseRepository extends AbstractDatabaseRepository<Tuple<Long,Long>, Prietenie> {
    public FriendshipDatabaseRepository(Validator<Prietenie> validator, String url, String username, String password, String table) throws SQLException {
        super(validator, url, username, password, table);
    }

    /**
     *
     * @param result -A result set that holds a row of data from the database
     * @return the newly created friendship from the data received from the database
     * @throws SQLException if there is a problem with the database
     */
    @Override
    protected Prietenie extractEntity(ResultSet result) throws SQLException {
        Long idUser1 = result.getLong("user1_id");
        Long idUser2 = result.getLong("user2_id");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = LocalDate.parse(result.getString("date"), formatter);
        Prietenie friendship = new Prietenie(idUser1,idUser2);
        LocalDateTime dateTime=date.atStartOfDay();
        friendship.setId(new Tuple(idUser1,idUser2));
        friendship.setDate(dateTime);
        return friendship;
    }

    /**
     * Writes an entity of type Prietenie (friendship) to a database
     * @param entity the entity we want to write in a database
     */
    @Override
    protected void writeToDatabase(Prietenie entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            String insert ="INSERT INTO "+table+" VALUES("+entity.getIdU1()+", "+entity.getIdU2()+", '"+entity.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE) +"')";
            Statement statement = connection.createStatement();
            statement.executeUpdate(insert);
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }

    }

    /**
     * Deletes an entity of type Prietenie (friendship)  from the database
     * @param entity the entity we want to delete
     */
    @Override
    protected void deleteFromDatabase(Prietenie entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            Statement statement = connection.createStatement();
            String delete = "DELETE from "+table + " WHERE user1_id=" +entity.getId().getLeft()+" AND user2_id=" + entity.getId().getRight();
            statement.executeUpdate(delete);

        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }
    }
}
