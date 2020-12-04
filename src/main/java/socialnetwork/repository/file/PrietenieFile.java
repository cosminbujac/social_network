package socialnetwork.repository.file;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class PrietenieFile extends AbstractFileRepository <Tuple<Long, Long>, Prietenie> {

    public PrietenieFile(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    /**
     * Converts the data extracted from the file in a Friendship entity
     * @param attributes - the attributes of the Friendship entity we want to create
     * @return the new friendship created with the attributes found in file
     */
    @Override
    public Prietenie extractEntity(List<String> attributes) {
        Prietenie prietenie = new Prietenie(Long.valueOf(attributes.get(0)),Long.valueOf(attributes.get(1)));
        prietenie.setDate(LocalDateTime.parse(attributes.get(2)));
        prietenie.setId(new Tuple(prietenie.getIdU1(),prietenie.getIdU2()));
        return prietenie;
    }

    /**
     * Turns a friendship entity into a string to be written in a file
     * @param entity - the friendship we want to write to the file
     * @return the string with the friendship's attributes
     */
    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getIdU1()+";"+entity.getIdU2()+";"+entity.getDate();
    }
}
