package socialnetwork;

import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipRequestValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.FriendshipRequestDBRepo;
import socialnetwork.repository.database.MessageDBRepo;
import socialnetwork.repository.database.UserDatabaseRepository;
import socialnetwork.repository.file.PrietenieFile;
import socialnetwork.repository.file.UtilizatorFile;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;
import socialnetwork.ui.ConsoleUI;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        //consoleMain();
        MainApp.main(args);

    }

    private static void consoleMain() {
        String fileNameUtilizator = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.users");
        String dbUsername = ApplicationContext.getPROPERTIES().getProperty("data.socialnewtwork.databaseUsername");
        String dbPassword = ApplicationContext.getPROPERTIES().getProperty("data.socialnewtwork.databasePassword");
//        Repository<Long,Utilizator> userFileRepository = new UtilizatorFile(fileNameUtilizator, new UtilizatorValidator());
//        UtilizatorService servUtil = new UtilizatorService(userFileRepository);
//        String fileNamePrietenie=ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendship");
//        Repository<Tuple<Long,Long>, Prietenie> firendshipFileRepo = new PrietenieFile(fileNamePrietenie, new PrietenieValidator());
//        FriendshipService friendshipService = new FriendshipService(firendshipFileRepo,userFileRepository);
//        ConsoleUI consoleUI= new ConsoleUI(servUtil,friendshipService);
//        consoleUI.run();
        String databaseUrl = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.usersDatabase");
        Repository<Long, Utilizator> databaseUserRepo = null;
        try {
            databaseUserRepo = new UserDatabaseRepository(new UtilizatorValidator(), databaseUrl, dbUsername, dbPassword, "users");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }
        UtilizatorService servUtil = new UtilizatorService(databaseUserRepo);
        String fileNamePrietenie = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.friendship");
        // Repository<Tuple<Long,Long>, Prietenie> firendshipFileRepo = new PrietenieFile(fileNamePrietenie, new PrietenieValidator());
        Repository<Tuple<Long, Long>, Prietenie> friendshipDbRepo = null;
        try {
            friendshipDbRepo = new FriendshipDatabaseRepository(new PrietenieValidator(), databaseUrl, dbUsername, dbPassword, "friendships");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }
        FriendshipRequestDBRepo friendshipRequestRepo = null;
        try {
            friendshipRequestRepo = new FriendshipRequestDBRepo(new FriendshipRequestValidator(), databaseUrl, dbUsername, dbPassword, "friendship_request");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }
        Repository<Long, Message> messageDbRepo = null;
        try {
            messageDbRepo = new MessageDBRepo(new MessageValidator(), databaseUrl, dbUsername, dbPassword, "mesaje");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        MessageService messageService = new MessageService(messageDbRepo);
        FriendshipService friendshipService = new FriendshipService(friendshipDbRepo, databaseUserRepo);
        FriendshipRequestService friendshipRequestService = new FriendshipRequestService(friendshipDbRepo, friendshipRequestRepo);
        ConsoleUI consoleUI = new ConsoleUI(servUtil, friendshipService, friendshipRequestService, messageService);
        consoleUI.run();
    }
}


