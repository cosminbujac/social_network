package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.MainApp;
import socialnetwork.config.ApplicationContext;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipRequestValidator;
import socialnetwork.domain.validators.PrietenieValidator;
import socialnetwork.domain.validators.UtilizatorValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.FriendshipDatabaseRepository;
import socialnetwork.repository.database.FriendshipRequestDBRepo;
import socialnetwork.repository.database.UserDatabaseRepository;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HomepageViewController {
    String dbUsername = ApplicationContext.getPROPERTIES().getProperty("data.socialnewtwork.databaseUsername");
    String dbPassword = ApplicationContext.getPROPERTIES().getProperty("data.socialnewtwork.databasePassword");
    String databaseUrl = ApplicationContext.getPROPERTIES().getProperty("data.socialnetwork.usersDatabase");

    UtilizatorService userService;
    FriendshipRequestService friendshipRequestService;
    FriendshipService friendshipService;
    Stage stage;
    Utilizator user;

    @FXML
    Button Quit;
    @FXML
    Button LogOff;
    @FXML
    Button AddNewFriend;
    @FXML
    Button StartConversation;
    @FXML
    Button ContinueConversation;
    @FXML
    Label Greeting;
    @FXML
    ListView<Utilizator> FriendsListView;
    @FXML
    Button Unfriend;
    @FXML
    ListView<FriendshipRequestDTO> FriendshipRequestListView;


    public void setService(Utilizator user, Stage stage,UtilizatorService userService) {
        this.userService = userService;
        this.friendshipService = MainApp.getFriendshipService();
        this.friendshipRequestService = MainApp.getFriendshipRequestService();
        this.stage = stage;
        this.user = user;
        Greeting.setText("Have a great day, "+user.getFirstName()+ "!");
        loadFriends();
    }
    @FXML
    private void initialize() {

    }

    public void quit()
    {
        System.exit(0);
    }

    public void logOff(){
        FXMLLoader loginLoader = new FXMLLoader();
        loginLoader.setLocation(getClass().getResource("/views/loginView.fxml"));
        AnchorPane loginLayout = null;
        try {
            loginLayout = loginLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        stage.setScene(new Scene(loginLayout));
        LoginViewController loginViewController = loginLoader.getController();
        loginViewController.setService(userService,stage);
    }

    public void loadFriends(){
        FriendsListView.getItems().clear();
        if(user==null)
            return;
        List<Utilizator> friends = user.getFriends();
        ObservableList<Utilizator> names = FXCollections.observableArrayList(friends);
        FriendsListView.setItems(names);
    }

    public void loadFriendshipRequests(){
        FriendshipRequestListView.getItems().clear();
        if(user==null)
            return;
        List<FriendshipRequestDTO>requests = new ArrayList<FriendshipRequestDTO>();
        friendshipRequestService.getFriendshipRequests(user.getId()).forEach(x->{
            if(x.getStatus().equals("pending"))
                requests.add(new FriendshipRequestDTO(x,userService.getOne(x.getSender())));
        });
        ObservableList<FriendshipRequestDTO> friendRequests =FXCollections.observableArrayList(requests);
        FriendshipRequestListView.setItems(friendRequests);
    }

    private void manageFriendRequest(Integer option){
        FriendshipRequestDTO friendshipReq=FriendshipRequestListView.getSelectionModel().getSelectedItem();
        if(friendshipReq==null)
        {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friendship Request", "No selection was found");
            return;
        }
        try {
            Prietenie newFriendship = friendshipRequestService.manageFriendshipRequest(friendshipReq.getFriendshipRequest().getId(),option);
            if (newFriendship!=null){
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friendship", "You got a new friend!!");
                newFriendship.setId(new Tuple<Long,Long>(this.user.getId(),friendshipReq.getSender().getId()));
                friendshipService.addPrietenie(newFriendship);

            loadFriends();
            } else {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friendship Request", "Friendship rejected!");
            }
        }catch(Exception e){
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
        loadFriendshipRequests();
    }

    public void acceptFriendRequest(){
        manageFriendRequest(1);
    }
    public void rejectFriendRequest(){
        manageFriendRequest(0);
    }


    public void unfriend(){
        Utilizator friend=FriendsListView.getSelectionModel().getSelectedItem();
        if(friend==null)
        {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Unfriend", "No selection was found");
            return;
        }
        if(friendshipService.deleteFriendship(user.getId(),friend.getId())!=null) {
            friendshipRequestService.deleteRequest(user.getId(),friend.getId());
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Unfriend", "You are not friends anymore");
        }
        else
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Unfriend", "Unfriend failed");
        loadFriends();
    }

    public void addNewFriend(){
        FXMLLoader addNewFriendLoader = new FXMLLoader();
        addNewFriendLoader.setLocation(getClass().getResource("/views/addFriendView.fxml"));
        AnchorPane addNewFriendLayout = null;
        try {
            addNewFriendLayout = addNewFriendLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        Stage addfriendStage = new Stage();
        addfriendStage.setTitle("Add friend");
        addfriendStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(addNewFriendLayout);
        addfriendStage.setScene(scene);
        addfriendStage.show();
        AddFriendViewController addFriendViewController = addNewFriendLoader.getController();
        addFriendViewController.setService(user,stage, userService,friendshipRequestService);
    }

    public void goToConversations(){
        FXMLLoader conversationMenuLoader = new FXMLLoader();
        conversationMenuLoader.setLocation(getClass().getResource("/views/conversationMenuView.fxml"));
        AnchorPane conversationMenuLayout = null;
        try {
            conversationMenuLayout =  conversationMenuLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        stage.setScene(new Scene(conversationMenuLayout));
        ConversationMenuViewController conversationMenuViewController =conversationMenuLoader.getController();
        conversationMenuViewController.setService(user,stage, userService);


    }
}
