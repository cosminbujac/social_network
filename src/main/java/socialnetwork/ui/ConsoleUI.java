package socialnetwork.ui;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipException;
import socialnetwork.domain.validators.ValidException;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.sql.SQLOutput;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private UtilizatorService utilizatorService;
    private FriendshipService friendshipService;
    private FriendshipRequestService friendshipRequestService;
    private MessageService messageService;
    private Scanner citire;

    public ConsoleUI(UtilizatorService utilizatorService, FriendshipService friendshipService,FriendshipRequestService friendshipRequestService,MessageService messageService) {
        this.utilizatorService = utilizatorService;
        this.friendshipService = friendshipService;
        this.friendshipRequestService = friendshipRequestService;
        this.messageService=messageService;
        citire = new Scanner(System.in);
    }
    private void displayMenu(){
        System.out.println();
        System.out.println("1 - VIEW USERS");
        System.out.println("2 - ADD USER");
        System.out.println("3 - DELETE USER");
        System.out.println("4 - VIEW FRIENDSHIPS");
        System.out.println("5 - ADD FRIENDSHIP");
        System.out.println("6 - DELETE FRIENDSHIP");
        System.out.println("7 - VIEW NUMBER OF COMMUNITIES");
        System.out.println("8 - VIEW ALL FRIENDS OF USER");
        System.out.println("9 - VIEW ALL FRIENDS OF USER BY TIME");
        System.out.println("10 - SEND A FRIENDSHIP REQUEST");
        System.out.println("11 - MANAGE FRIENDSHIP REQUEST");
        System.out.println("12 - CREATE A CONVERSATION");
        System.out.println("13 - RESPOND TO A MESSAGE");
        System.out.println("14 - VIEW A CONVERSATION");

        System.out.println("20 - VIEW THE MENU");
        System.out.println("0 - QUIT");
        System.out.println();
    }
    /*
     * UI method for adding an user
     */
    private void addUser() {
        try{
            System.out.print("Introduceti numele utilizatorului: ");
            String nume = citire.nextLine();
            System.out.print("Prenumele utilizatorului: ");
            String prenume = citire.nextLine();
            System.out.print("Introduceti parola:");
            String password = citire.nextLine();
            Utilizator verif = utilizatorService.addUtilizator(prenume,nume,password);
            if(verif!=null)
                System.out.println("ID deja existent!");
        }catch (ValidException ve)
        {
            System.out.println(ve.getMessage());
        }
        catch(IllegalArgumentException iae)
        {
            iae.printStackTrace();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /*
     * UI method for deleting an user
     */
    private void deleteUser() {
        try {
            System.out.print("Introduceti id-ul utilizatorului:");
            Long id = Long.valueOf(citire.nextLine());
            friendshipService.deleteUsersFriendships(id);
            friendshipRequestService.deleteUsersRequests(id);
            if(utilizatorService.deleteUser(id)==null)
                System.out.println("Nu exista nici un utilizator cu acest ID!");
        } catch (ValidException  ve) {
            System.out.println(ve.getMessage());
        }
        catch( IllegalArgumentException iae)
        {
            iae.printStackTrace();
        }
    }

    /*
     * UI method for creating a friendship between two users
     */
    private void addPrietenie(){
        try{
            System.out.print("Introduceti id-ul primului utilizator: ");
            Long id1 = Long.valueOf(citire.nextLine());
            System.out.print("Introduceti id-ul celui de al doilea utilizator: ");
            Long id2 = Long.valueOf(citire.nextLine());
            Prietenie noua = new Prietenie(id1,id2);
            noua.setId(new Tuple<Long,Long>(id1,id2));
            if(friendshipService.addPrietenie(noua)!=null)
                System.out.println("Cei doi utilizatori sunt deja prieteni");
        }catch (ValidException ve)
        {
            System.out.println(ve.getMessage());
        }
        catch (NumberFormatException ne)
        {
            ne.printStackTrace();
        }
        catch (FriendshipException fe)
        {
            System.out.println(fe.getMessage());
        }
    }

    private void deleteFriendship()
    {
        try {
            System.out.println("Intoroduceti id-urile celor 2 prieteni:");
            System.out.print("Id 1: ");
            Long id1 = Long.valueOf(citire.nextLine());
            System.out.print("Id 2: ");
            Long id2 = Long.valueOf(citire.nextLine());
            if(friendshipService.deleteFriendship(id1,id2)==null)
                System.out.println("Prietenia nu exista!");
        }
        catch ( IllegalArgumentException ie)
        {
            ie.printStackTrace();
        }
        catch (ValidException ve)
        {
            System.out.println(ve.getMessage());
        }
        catch (FriendshipException fe)
        {
            System.out.println(fe.getMessage());
        }
    }
    private void nrComunitati(){
        System.out.println("Numarul de comunitati este: "+ friendshipService.nrComunitati());
    }

    private void viewFriendsOf() {

        try{
            System.out.print("Introduceti id-ul utilizatorului:");
            Long id=Long.valueOf(citire.nextLine());
            Utilizator user=utilizatorService.getOne(id);
            if(user==null)
            {
                System.out.println("Utilizatorul nu exista!");
                return;
            }
            System.out.println("Prietenii lui sunt:");
            friendshipService.getFriendshipsOf(id).stream()
                    .map(x->{
                        if(x.getIdU1().equals(id))
                        {
                            Utilizator friend  = utilizatorService.getOne(x.getIdU2());
                            return friend.getLastName()+"|"+friend.getFirstName()+"|"+x.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        }
                        else {
                            Utilizator friend  = utilizatorService.getOne(x.getIdU1());
                            return friend.getLastName()+"|"+friend.getFirstName()+"|"+x.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        }
                    })
                    .forEach(System.out::println);
        }catch ( IllegalArgumentException ie) {
            ie.printStackTrace();
        }
        catch (ValidException ve){
            System.out.println(ve.getMessage());
        }
    }

    private void viewFriendsByDate()
    {
        try{
            System.out.print("Introduceti id-ul utilizatorului:");
            Long id=Long.valueOf(citire.nextLine());
            if(utilizatorService.getOne(id)==null)
            {
                System.out.println("Utilizatorul nu exista!");
                return;
            }
            List<Prietenie>friendships = friendshipService.getFriendshipsOf(id);
            if (friendships.size()==0) {
                System.out.println("Utilizatorul nu are prieteni");
                return;
            }
            Integer luna =0;
            while (luna<1||luna>12)
            {
                System.out.print("Introduceti luna:");
                luna = Integer.valueOf(citire.nextLine());
                if(luna<1||luna>12)
                    System.out.println("Luna invalida! Trebuie sa fie intre 1-12");
            }
            Integer an=0;
            while (an<2000||an>2050)
            {
                System.out.print("Introduceti anul:");
                an = Integer.valueOf(citire.nextLine());
                    if(an<2000||an>2050)
                        System.out.println("An invalid!");
            }
            Integer finalAn = an;
            Integer finalLuna = luna;
            friendships.stream()
                   .filter(x->x.getDate().getMonthValue() == finalLuna && x.getDate().getYear() == finalAn)
                   .forEach(x->{
                       Utilizator user=null;
                       if(x.getIdU1().equals(id))
                           user=utilizatorService.getOne(x.getIdU2());
                       else
                           user=utilizatorService.getOne(x.getIdU1());
                       System.out.println(user.getLastName()+"|"+user.getFirstName()+"|"+x.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                   });
        }
        catch (IllegalArgumentException iae)
        {
            System.out.println(iae.getMessage());
        }
        catch (ValidException ve)
        {
            System.out.println(ve.getMessage());
        }

    }
    private void sendFriendshipRequest() {
        try {
            System.out.print("Id sender:");
            Long sender = Long.valueOf(citire.nextLine());
            System.out.print("Id receiver:");
            Long receiver = Long.valueOf(citire.nextLine());
            if (utilizatorService.getOne(sender) == null) {
                System.out.println("The sender doesnt exist!");
                return;
            }
            if (utilizatorService.getOne(receiver) == null) {
                System.out.println("The receiver doesnt exist!");
                return;
            }
            friendshipRequestService.addFriendshipRequest(sender, receiver);
        }catch(IllegalArgumentException ise) {
            System.out.println(ise.getMessage());
        }
        catch (ValidException ve){
            System.out.println(ve.getMessage());
        }
    }
    private void manageFriendshipRequest() {
        try{
            System.out.print("Id-ul userului:");
            Long receiver=Long.valueOf(citire.nextLine());
            List<FriendshipRequest>requests=friendshipRequestService.getFriendshipRequests(receiver);
            requests.stream().filter(x-> x.getStatus().equals("pending"))
                    .forEach(x-> {
                Utilizator user = utilizatorService.getOne(x.getSender());
                System.out.println(user.getLastName()+" "+user.getFirstName()+" v-a cerut prietenia. | status: " + x.getStatus() +" | request id:"+x.getId());
            });
            while(true) {
                System.out.print("Do you want to manage any request? [y/n]: ");
                String raspuns = citire.nextLine();
                if (raspuns.equals("n"))
                    return;
                else if (raspuns.equals("y")) {
                    System.out.print("Request id:");
                    Long requestId = Long.valueOf(citire.nextLine());
                    System.out.print("Accept/Reject [a/r]: ");
                    String manage = citire.nextLine();
                    if (manage.equals("a")) {
                        Prietenie pr = friendshipRequestService.manageFriendshipRequest(requestId, 1);
                        friendshipService.addPrietenie(pr);
                    } else
                        friendshipRequestService.manageFriendshipRequest(requestId, 2);
                }
            }
        }
        catch (IllegalArgumentException iae){
            System.out.println(iae.getMessage());
        }
        catch (ValidException ve) {
            System.out.println(ve.getMessage());
        }
        catch (FriendshipException fe){
            System.out.println(fe.getMessage());
        }
    }

    private void sendMessage() {
        try {
            System.out.print("Introduceti id-ul utilizatorului care trimite mesajul:");
            Long idSender = Long.valueOf(citire.nextLine());
            Utilizator user = utilizatorService.getOne(idSender);
            if (user == null) {
                System.out.println("Utilizatorul nu exista!");
                return;
            }
            System.out.println("Utilizatorul poate trimite mesajul urmatorilor oameni:");
            user.getFriends().forEach(x -> {
                System.out.println(x.getFirstName() + " " + x.getLastName() + "| ID:" + x.getId());
            });
            ArrayList<Long> to = new ArrayList<Long>();
            while (true) {
                System.out.print("Id prieten:");
                Long prietenId = Long.valueOf(citire.nextLine());
                to.add(prietenId);
                System.out.print("Doriti sa mai adaugati prieteni?[y,n]:");
                String optiune = citire.nextLine();
                if (!optiune.equals("y"))
                    break;
            }
            System.out.print("Introduceti numele conversatiei:");
            String conversationName = citire.nextLine();
            System.out.print("Introduceti mesajul:");
            String text = citire.nextLine();
            Message message = new Message(idSender, to, text,conversationName);
            messageService.trimiteMesaj(message);
        }
        catch (IllegalArgumentException iae){
            System.out.println(iae.getMessage());
        }
        catch (ValidException ve){
            System.out.println(ve.getMessage());
        }
    }
    private void answerMessage(){
        try {
            System.out.print("Introduceti id-ul utilizatorului care doriti sa raspunda:");
            Long idUser = Long.valueOf(citire.nextLine());
            Utilizator user = utilizatorService.getOne(idUser);
            if (user == null) {
                System.out.println("Utilizatorul nu exista!");
                return;
            }
            List<Message>mesajeUtil=messageService.findUserMessages(idUser);
            System.out.println(user.getFullName()+ " are urmatoarele mesaje");
            mesajeUtil.forEach(x->{
                System.out.println("From: "+ utilizatorService.getOne(x.getFrom()).getFullName() +" :"+ x.getText()+ " |id mesaj:"+ x.getId());
            });
            System.out.print("Doriti sa raspundeti vreunui mesaj?[y/n]:");
            String optiune = citire.nextLine();
            if (!optiune.equals("y"))
                return;
            System.out.print("Introducti id-ul mesajului:");
            Long msgId=Long.valueOf(citire.nextLine());
            Message initialText=messageService.getOne(msgId);
            if(initialText==null)
            {
                System.out.println("Id ul acesta nu exista!");
                return;
            }
            System.out.print("Introduceti raspunsul:");
            String raspuns=citire.nextLine();
            List<Long>to=new ArrayList<Long>();
            to.add(initialText.getFrom());
            for (Long aLong : initialText.getTo()) {
                if(!aLong.equals(idUser))
                    to.add(aLong);
            }
            Message reply=new Message(idUser,to,raspuns,msgId);
            messageService.trimiteMesaj(reply);

        }
        catch (IllegalArgumentException iae){
            System.out.println(iae.getMessage());
        }
        catch (ValidException ve){
            System.out.println(ve.getMessage());
        }
    }
    private void viewConversation(){
        System.out.print("Introduceti id-ul conversatiei:");
        Long id=Long.valueOf(citire.nextLine());
        ArrayList<Message>conversatie = new ArrayList<Message>();
        messageService.getAll().forEach(x->{
            if(x.getConversationId().equals(id))
                conversatie.add(x);
        });
        System.out.println("*******");
        System.out.print("Conversatia are loc intre: ");
        final String[] to = {""};
        conversatie.get(0).getTo().forEach(receiver->{
            to[0] = to[0].concat(", "+utilizatorService.getOne(receiver).getFullName());
        });
        System.out.print(utilizatorService.getOne(conversatie.get(0).getFrom()).getFullName()+ to[0]);
        System.out.println("\n");

        conversatie.forEach(mesaj->{
            System.out.println(utilizatorService.getOne(mesaj.getFrom()).getFullName()+":-" + mesaj.getText() );
        });
        System.out.println("*******");
    }


    public void run() {
        displayMenu();
        while (true) {
            try {
                System.out.print("optiune: ");
                int varianta = Integer.valueOf(citire.nextLine());
                switch (varianta) {
                    case 0:
                        System.exit(0);
                    case 1:
                        utilizatorService.getAll().forEach(System.out::println);
                        break;
                    case 2:
                        addUser();
                        break;
                    case 3:
                        deleteUser();
                        break;
                    case 4:
                        friendshipService.getAll().forEach(System.out::println);
                        break;
                    case 5:
                        addPrietenie();
                        break;
                    case 6:
                        deleteFriendship();
                        break;
                    case 7:
                        nrComunitati();
                        break;
                    case 8:
                        viewFriendsOf();
                        break;
                    case 9:
                        viewFriendsByDate();
                        break;
                    case 10:
                        sendFriendshipRequest();
                        break;
                    case 11:
                        manageFriendshipRequest();
                        break;
                    case 12:
                        sendMessage();
                        break;
                    case 13:
                        answerMessage();
                        break;
                    case 14:
                        viewConversation();
                        break;
                    case 20:
                        displayMenu();
                        break;
                    default:
                        System.out.println("Ati introdus o optiune gresita");
                        break;
                }
            } catch (IllegalArgumentException ie) {
                System.out.println("Argumentul introdus nu este o optiune!");
            }

        }
    }




}
