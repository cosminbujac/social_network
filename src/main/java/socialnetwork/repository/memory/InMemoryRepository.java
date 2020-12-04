package socialnetwork.repository.memory;

import socialnetwork.domain.Entity;
import socialnetwork.domain.validators.Validator;
import socialnetwork.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryRepository<ID, E extends Entity<ID>> implements Repository<ID,E> {

    private Validator<E> validator;
    Map<ID,E> entities;


    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
        entities=new HashMap<ID,E>();
    }

    /**
     * Method that returns a certain entity found by ID
     * @param id -the id of the entity to be returned
     *           id must not be null
     * @return the entity found, null if none
     */
    @Override
    public E findOne(ID id){
        if (id==null)
            throw new IllegalArgumentException("id must be not null");
        return entities.get(id);
    }


    /**
     *
     * @return a collection of a certain entity
     */
    @Override
    public Iterable<E> findAll() {
        return entities.values();
    }

    /**
     * Saves an entity in memory
     * @param entity
     *         entity must be not null
     * @return the entity if there already is one with the same id, null otherwise
     */
    @Override
    public E save(E entity) {
        if (entity==null)
            throw new IllegalArgumentException("entity must be not null");
        validator.validate(entity);
        if(entities.get(entity.getId()) != null) {
            return entity;
        }
        else entities.put(entity.getId(),entity);
        return null;
    }

    /**
     * Deletes an entity with a certain ID
     * @param id
     *      id must be not null
     * @return the entity that was deleted if there is one saved, null otherwise
     */
    @Override
    public E delete(ID id) {
        if(id==null)
            throw new IllegalArgumentException("Id-ul nu poate sa fie null!");
        return entities.remove(id);

    }

    /**
     * Replaces the entity that shares the same id with the new one
     * @param entity
     *          entity must not be null
     * @return null if the replace is successful, the entity otherwise
     */
    @Override
    public E update(E entity) {

        if(entity == null)
            throw new IllegalArgumentException("entity must be not null!");
        validator.validate(entity);

        entities.put(entity.getId(),entity);

        if(entities.get(entity.getId()) != null) {
            entities.put(entity.getId(),entity);
            return null;
        }
        return entity;

    }

}
