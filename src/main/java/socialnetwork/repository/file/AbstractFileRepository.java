package socialnetwork.repository.file;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.memory.InMemoryRepository;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;


public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID,E> {
    String fileName;

    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();

    }

    /**
     * Method that transforms and loads in memory all the data saved in a file
     */
    private void loadData() {
        Path path = Paths.get(fileName);
        try {
            List<String> lines = Files.readAllLines(path);
            lines.forEach(linie -> {
                E entity = extractEntity(Arrays.asList(linie.split(";")));
                super.save(entity);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * extract entity  - template method design pattern
     * creates an entity of type E having a specified list of @code attributes
     *
     * @param attributes -  The attributes of the entity we want to extract
     * @return an entity of type E
     */
    public abstract E extractEntity(List<String> attributes);
    ///Observatie-Sugestie: in locul metodei template extractEntity, puteti avea un factory pr crearea instantelor entity

    protected abstract String createEntityAsString(E entity);

    /**
     * Saves the entity of type E in file
     * @param entity
     *         entity must be not null
     * @return null if the entity was saved, entity otherwise
     */
    @Override
    public E save(E entity) {
        E e = super.save(entity);
        if (e == null) {
            writeToFile(entity);
        }
        return e;

    }

    /**
     * Deletes an entity of Type E by its id
     * @param id
     *      id must be not null
     * @return the entity if it has been deleted, null otherwise
     */
    @Override
    public E delete(ID id) {
        E entity = super.delete(id);
        if(entity!=null)
            writeAllToFile();
        return entity;
    }

    /**
     * Writes an entity of type E to a file
     * @param entity - the entity we want to save in the file
     */
    protected void writeToFile(E entity) {
        try (BufferedWriter bW = new BufferedWriter(new FileWriter(fileName, true))) {
            bW.write(createEntityAsString(entity));
            bW.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Empties the file and rewrites to it all the existing entities
     */
    protected void writeAllToFile() {
        try {
            PrintWriter writer = new PrintWriter(fileName);
            writer.print("");
            writer.close();
            super.findAll().forEach(this::writeToFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }
}

