package socialnetwork.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Utilizator extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String password;
    private List<Utilizator> friends;

    public Utilizator(String firstName, String lastName,String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password=password;
        friends = new ArrayList<Utilizator>();
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void removeFriend(Utilizator util){
        friends.remove(util);
    }
    public String getFullName(){
        return firstName+ " "+lastName;
    }

    public void addFriend(Utilizator util)
    {
        friends.add(util);
    }

    public List<Utilizator> getFriends() {
        return friends;
    }

    private String friendsString(Utilizator u){
        String prieteni= ("");
        for(Utilizator x :u.friends)
            prieteni = prieteni.concat(x.getFirstName()+" " +x.getLastName()+", ");
        return prieteni;
        }


    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilizator)) return false;
        Utilizator that = (Utilizator) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName()) &&
                getFriends().equals(that.getFriends());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}