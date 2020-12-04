package socialnetwork.repository.file;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UtilizatorFile extends AbstractFileRepository<Long, Utilizator>{

    public UtilizatorFile(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    /**
     * Converts the data extracted from the file in an Utilizator entity
     * @param attributes - the attributes of the User entity
     * @return the user created with the data from the file
     */
    @Override
    public Utilizator extractEntity(List<String> attributes) {
        Utilizator user = new Utilizator(attributes.get(1),attributes.get(2),attributes.get(3));
        user.setId(Long.parseLong(attributes.get(0)));
        return user;
    }

    /**
     * Turns a Utilizator entity into a string to be written in a file
     * @param entity - the user we want to write in the file
     * @return -the string with the user's attributes
     */
    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName();
    }
}
