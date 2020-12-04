package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDatabaseRepository extends AbstractDatabaseRepository<Long, Utilizator> {
    public UserDatabaseRepository(Validator validator, String url, String username, String password, String table) throws SQLException {
        super(validator, url, username, password, table);
    }

    /**
     *
     * @param result -A result set that holds a row of data from the database
     * @return the newly created User from the data received from the database
     * @throws SQLException if there is a problem with the database
     */
    @Override
    protected Utilizator extractEntity(ResultSet result) throws SQLException {
        Long id = result.getLong("id");
        String lastName = result.getString("lastName");
        String firstName = result.getString("firstName");
        String password = result.getString("password");
        Utilizator user = new Utilizator(firstName,lastName,password);
        user.setId(id);
        return user;
    }

    /**
     * Writes an entity of type Utilizator (User) to the database
     * @param entity the entity we want to write in a database
     */
    @Override
    protected void writeToDatabase(Utilizator entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            String insert ="INSERT INTO "+table+" VALUES("+entity.getId()+", '"+entity.getFirstName()+"', '"+entity.getLastName()+"', '" +entity.getPassword() + "') " ;
            Statement statement = connection.createStatement();
            statement.executeUpdate(insert);
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }
    }

    /**
     * Deletes a entity of type Utilizator (User) from the database
     * @param entity the entity we want to delete
     */
    @Override
    protected void deleteFromDatabase(Utilizator entity) {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            Statement statement = connection.createStatement();
            String delete = "DELETE from "+table + " WHERE id=" +entity.getId();
            statement.executeUpdate(delete);

        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }
    }
}
