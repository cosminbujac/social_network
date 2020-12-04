package socialnetwork.repository.database;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.sql.*;
import java.util.Map;

public abstract class AbstractDatabaseRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    protected Connection connection=null;
    protected String URL,username,password,table;

    public AbstractDatabaseRepository(Validator<E> validator, String url,String username,String password,String table) throws SQLException {
        super(validator);
        this.URL = url;
        this.username=username;
        this.password=password;
        this.table=table;
        loadData();
    }

    /**
     * Method that transforms and loads in memory all the data saved in a database
     */
    private void loadData()
    {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            String getAll = "SELECT * from "+table;
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(getAll);
            while(result.next()) {
                E entity = extractEntity(result);
                super.save(entity);
            }
            connection.close();
        }catch(SQLException e){
            e.printStackTrace();
            throw new ConnectionException("Error in connection to postgresql server");
        }
    }

    /**
     *
     * @param result -A result set that holds a row of data from the database
     * @return - The entity of type E created from that data
     * @throws SQLException if there is a problem with the database
     */
    protected abstract E extractEntity(ResultSet result) throws SQLException;

    /**
     * Saves in memory an entity and writes it to a database
     * @param entity
     *         entity must be not null
     * @return  null if the entity is saved, the entity if it already exists
     */
    @Override
    public E save(E entity) {
        E e = super.save(entity);
        if (e == null) {
            writeToDatabase(entity);
        }
        return e;
    }

    /**
     * Saves an entity in a database
     * @param entity the entity we want to write in a database
     */
    protected abstract void writeToDatabase(E entity);

    /**
     * Deletes an entity from both the memory and database
     * @param id the id of the entity we want to delete
     *      id must be not null
     * @return null if the entity didn't exist to begin with, the entity itself if it was deleted
     */
    @Override
    public E delete(ID id) {
        E entity = super.delete(id);
        if(entity!=null)
            deleteFromDatabase(entity);
        return entity;
    }

    /**
     * Auxiliar method to delete an entity from the database
     * @param entity the entity we want to delete
     */
    protected abstract void deleteFromDatabase(E entity);

}
