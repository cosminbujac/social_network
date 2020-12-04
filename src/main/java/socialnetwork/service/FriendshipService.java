package socialnetwork.service;

import socialnetwork.domain.Prietenie;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.domain.validators.FriendshipException;
import socialnetwork.domain.validators.ValidException;
import socialnetwork.repository.Repository;
import sun.nio.ch.Util;

import java.util.*;


public class FriendshipService {
    private final Repository<Tuple<Long, Long>, Prietenie> repoPrietenie;
    private final Repository<Long, Utilizator> repoUtil;

    /**
     * Constructor of the firendships
     * @param repoPrietenie - reference to the Friendship Repository
     * @param repoUtil - reference to the User Repository
     */
    public FriendshipService(Repository<Tuple<Long, Long>, Prietenie> repoPrietenie, Repository<Long, Utilizator> repoUtil) {
        this.repoPrietenie = repoPrietenie;
        this.repoUtil = repoUtil;
        repoPrietenie.findAll().forEach(x->{
            Utilizator u1= repoUtil.findOne(x.getIdU1());
            Utilizator u2= repoUtil.findOne(x.getIdU2());
            u1.addFriend(u2);
            u2.addFriend(u1);
        });
    }
    /**
     * Service method of deleting all the friendships in which a certain user is involved
     * @param IdUser of type Long which is the Id of the user we want to delete
     */
    public void deleteUsersFriendships(Long IdUser)
    {
        List<Tuple<Long,Long>> ids = new ArrayList<>();
        this.getAll().forEach(x->{
            if(x.getIdU2().equals(IdUser) || x.getIdU1().equals(IdUser))
                ids.add(x.getId());
        });
        ids.forEach(x->deleteFriendship(x.getLeft(),x.getRight()));
    }

    /**
     * Service method for adding a new friendship between two users
     * @param prietenie - The friendship we want to add
     * @return null if the friendship was just created, the friendship itself if it already was created
     * @throws ValidException if the users do no exist, or if there's only one user for each side of the friendship
     * @throws FriendshipException if the two users were already friends
     */
    public Prietenie addPrietenie(Prietenie prietenie) {
        Utilizator u1 = repoUtil.findOne(prietenie.getIdU1());
        Utilizator u2 = repoUtil.findOne(prietenie.getIdU2());
        if(u1==null)
            throw new ValidException("Utilizatorul cu primul ID nu exista!");
        if(u2==null)
            throw new ValidException("Utilizatorul cu al doilea ID nu exista!");
        if(u1==u2)
            throw new ValidException("Utilizatorul nu poate sa fie prieten cu el insusi!");

        List<Utilizator> friends=u1.getFriends();
        for(Utilizator x:friends)
            if(x.getId().equals(u2.getId()))
                throw new FriendshipException("Cei doi sunt deja prieteni!");
        Prietenie task = repoPrietenie.save(prietenie);
        u1.addFriend(u2);
        u2.addFriend(u1);
        return task;
    }

    /**
     * Service method to delete a friendship between 2 users
     * @param id1 of type Long, being the id of the first user
     * @param id2 of type Long, being the id of the second user
     * @return the friendship if it has been deleted, null otherwise
     * @throws ValidException if the id of either of the users is invalid
     * @throws FriendshipException if the friendship between the 2 users didn't exist in the first place
     */
    public Prietenie deleteFriendship(Long id1,Long id2){
        Utilizator u1 = repoUtil.findOne(id1);
        Utilizator u2 = repoUtil.findOne(id2);
        if(u1==null)
            throw new ValidException("Utilizatorul cu primul ID nu exista!");
        if(u2==null)
            throw new ValidException("Utilizatorul cu al doilea ID nu exista!");
        if(u1==u2)
            throw new ValidException("Utilizatorul nu poate sa fie prieten cu el insusi!");
        Tuple<Long,Long> ids = this.getPrietenieId(id1,id2);
        if(ids.getLeft().equals(0L) && ids.getRight().equals(0L))
            throw new FriendshipException("Utilizatorii nu sunt prieteni!");
        u1.removeFriend(u2);
        u2.removeFriend(u1);
        return repoPrietenie.delete(ids);
    }

    /**
     * Service method that returns the id of a friendship between two users
     * @param id1 - the id of the first user
     * @param id2 - the id of the second user
     * @return ids - being the ID of the friendship bewtween the two users
     */
    public Tuple<Long,Long> getPrietenieId(Long id1, Long id2)
    {
        Tuple<Long,Long> ids = new  Tuple<>(0L,0L);
        this.getAll().forEach(x->{
            if(x.getIdU1().equals(id1)&&x.getIdU2().equals(id2) || x.getIdU1().equals(id2) && x.getIdU2().equals(id1)) {
                ids.setLeft(x.getId().getLeft());
                ids.setRight(x.getId().getRight());
            }
        });
        return ids;
    }

    /**
     * Service method that returns all the friendships in memory
     * @return collection of friendships
     */
    public Iterable<Prietenie> getAll(){
        return repoPrietenie.findAll();
    }

    /**
     * Returns all the friendship relations of a certain user
     * @param id - the id of the user
     * @return - a list of friendship relations
     * @throws ValidException if the user doesnt exist
     */
    public List<Prietenie> getFriendshipsOf(Long id)
    {
        List<Prietenie> friendships = new ArrayList<Prietenie>();
        Utilizator user = repoUtil.findOne(id);
        if(user==null)
            throw new ValidException("The user doesnt exist");
        user.getFriends().forEach(x->{
            Prietenie pr = getOne(getPrietenieId(x.getId(),id));
            friendships.add(pr);
        });
        return friendships;
    }

    /**
     *
     * @param id - the id of the friendship
     * @return the friendship with the specified id or null if there isn't any
     */
    public Prietenie getOne(Tuple<Long,Long> id)
    {
        return repoPrietenie.findOne(id);
    }

    /**
     *Counts the number of communities (conex components)
     * @return nr , the number of communities there are in the social network, based on all the friendships
     */
    public int nrComunitati(){
        Integer nr =0;
        Map vizitat = new HashMap<Long,Integer>();
        repoUtil.findAll().forEach(x->vizitat.put(x.getId(),0));
        for (Utilizator utilizator : repoUtil.findAll()) {
            if(vizitat.get(utilizator.getId()).equals(0)) {
                vizitat.put(utilizator.getId(),1);
                nr++;
                dfs(utilizator,vizitat);
            }
        }
        return nr;
    }

    /**
     *Performs dfs on an adjacency list
     * @param utilizator - being user that we consider as the current node of the dfs
     * @param vizitat - being a map in which we keep track if the current node has been visited or not
     */
    private void dfs(Utilizator utilizator,Map vizitat) {
        utilizator.getFriends().forEach(x->{
            if(vizitat.get(x.getId()).equals(0))
            {
                vizitat.put(x.getId(),1);
                dfs(x,vizitat);
            }
        });

    }

    /**
     *Service method that calculates and returns the biggest community ( the one that has the longest path )
     * @return longest path, a list of type Long that contains the id's of the users in that community
     * @throws ValidException if there are no the app has no users
     */
    public List<Long> getLongest_path() {
        Map vizitat = new HashMap<Long,Integer>();
        List <Long> longestPath =  new ArrayList<Long>();
        List <Long> aux =  new ArrayList<Long>();
        Integer max=0;
        repoUtil.findAll().forEach(x->vizitat.put(x.getId(),0));
        if(vizitat.isEmpty())
            throw new ValidException("There are no users!");

        for(Utilizator user:repoUtil.findAll())
        {
            if(vizitat.get(user.getId()).equals(0))
            {
                vizitat.put(user.getId(),1);
                aux.add(user.getId());
                dfsAdd(vizitat,user,aux);
                if(aux.size()>max)
                {
                    max=aux.size();
                    longestPath= (List<Long>) ((ArrayList<Long>) aux).clone();
                }
                aux.clear();

            }
        }
        return longestPath;
    }
    /**
     * Method that performs dfs while adding the id of the current node to a list of id's
     * @param vizitat - a map that checks if a node has been visited before
     * @param user - the current node
     * @param ids - list where we memorize the ids in the current conex component
     */
    private void dfsAdd(Map vizitat,Utilizator user,List<Long> ids) {
        user.getFriends().forEach(x->{
            if(vizitat.get(x.getId()).equals(0))
            {
                ids.add(x.getId());
                vizitat.put(x.getId(),1);
                dfsAdd(vizitat,x,ids);
            }
        });
    }

}
