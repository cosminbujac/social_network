package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controllers.LoginViewController;
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
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;
import java.sql.SQLException;


public class MainApp extends Application {
    UtilizatorService userService;
    static FriendshipService friendshipService;
    static FriendshipRequestService friendshipRequestService;
    static MessageService messageService;
    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        String dbUsername = ApplicationContext.getPROPERTIES().getProperty("data.socialnewtwork.databaseUsername");
        String dbPassword = ApplicationContext.getPROPERTIES().getProperty("data.socialnewtwork.databasePassword");
        String databaseUrl = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.usersDatabase");
        Repository<Long, Utilizator> databaseUserRepo = null;
        try {
            databaseUserRepo = new UserDatabaseRepository(new UtilizatorValidator(), databaseUrl, dbUsername, dbPassword, "users");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }
        userService = new UtilizatorService(databaseUserRepo);

        Repository<Tuple<Long, Long>, Prietenie> friendshipDbRepo = null;
        try {
            friendshipDbRepo = new FriendshipDatabaseRepository(new PrietenieValidator(), databaseUrl, dbUsername, dbPassword, "friendships");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }
        friendshipService = new FriendshipService(friendshipDbRepo, databaseUserRepo);

        FriendshipRequestDBRepo friendshipRequestRepo = null;
        try {
            friendshipRequestRepo = new FriendshipRequestDBRepo(new FriendshipRequestValidator(), databaseUrl, dbUsername, dbPassword, "friendship_request");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.exit(0);
        }
        friendshipRequestService = new FriendshipRequestService(friendshipDbRepo, friendshipRequestRepo);

        Repository<Long, Message> messageDbRepo = null;
        try {
            messageDbRepo = new MessageDBRepo(new MessageValidator(), databaseUrl, dbUsername, dbPassword, "mesaje");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        messageService = new MessageService(messageDbRepo);
        initView(primaryStage);
        //primaryStage.setWidth(800);
        primaryStage.show();

    }

    private void initView(Stage primaryStage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/views/loginView.fxml"));
        AnchorPane loginLayout = loginLoader.load();
        primaryStage.setScene(new Scene(loginLayout));

        LoginViewController loginViewController = loginLoader.getController();
        loginViewController.setService(userService,primaryStage);


    }
    public static FriendshipService getFriendshipService(){
        return friendshipService;
    }

    public static FriendshipRequestService getFriendshipRequestService(){ return friendshipRequestService; }

    public static MessageService getMessageService(){ return messageService; }
}
