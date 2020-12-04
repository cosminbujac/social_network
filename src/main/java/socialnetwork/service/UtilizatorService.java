package socialnetwork.service;

import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.ValidException;
import socialnetwork.repository.Repository;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collection;

public class UtilizatorService  {
    private static final int iterations = 20*1000;
    private static final int saltLen = 32;
    private static final int desiredKeyLen = 256;
    private Repository<Long, Utilizator> repo;

    public UtilizatorService(Repository<Long, Utilizator> repo) {
        this.repo = repo;
    }

    /**
     * Service method of adding an user
     *
     * @param firstname being the firstname of the user
     * @param lastname being the lastname of the user
     * @param password being the password of the user
     * @return null if the user ID isn't used by another user, the user itself it his id is being used
     * @throws ValidException if User's name is null or has symbols or numbers in ints composition
     */
    public Utilizator addUtilizator(String firstname, String lastname, String password) throws Exception {
        if(password.length()<8)
            throw new ValidException("Password must have at least 8 characters!");
        String hashedPass = getSaltedHash(password);
        Utilizator user = new Utilizator(firstname,lastname,hashedPass);
        user.setId(getNewID());
        return repo.save(user);
    }
    /**
     * Service method of deleting an user
     *
     * @param  id of type Long, the ID of the user we want to delete
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException  if the given id is null.
     */
    public Utilizator deleteUser(Long id){
        return repo.delete(id);
    }

    /**
     * Finds an unused ID and returns it
     * @return an ID of type Long
     */
    public Long getNewID()
    {
        Long id=-1L;
        for (Utilizator x : repo.findAll())
            if(x.getId()>id)
                id=x.getId();
        return  id+1;
    }

    /**
     * Service method that returns all the users saved in memory
     * @return collection of users
     */
    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    /**
     * Service method of finding a certain user by his id
     * @param id - the id of the entity we want to find
     * @return the entity with the specified id or null if there's none
     */
    public Utilizator getOne(Long id){
        return repo.findOne(id);
    }


    /** Computes a salted PBKDF2 hash of given plaintext password
     suitable for storing in a database.
     Empty passwords are not supported. */
    private static String getSaltedHash(String password) throws Exception {
        byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
        // store the salt with the password
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(salt) + "$" + hash(password, salt);
    }

    /** Checks whether given plaintext password corresponds
     to a stored salted hash of the password. */
   public static boolean check(String password, String stored) throws Exception{
        String[] saltAndHash = stored.split("\\$");
        if (saltAndHash.length != 2) {
            throw new IllegalStateException("The stored password must have the form 'salt$hash'");
        }
        Base64.Decoder decoder = Base64.getDecoder();
        String hashOfInput = hash(password, decoder.decode(saltAndHash[0]));
        return hashOfInput.equals(saltAndHash[1]);
    }

    private static String hash(String password, byte[] salt) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        if (password == null || password.length() == 0)
            throw new IllegalArgumentException("Empty passwords are not supported.");
        SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
        return encoder.encodeToString(key.getEncoded());
    }


}
