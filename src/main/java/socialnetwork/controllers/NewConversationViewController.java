package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.MainApp;
import socialnetwork.domain.FriendshipRequestDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewConversationViewController {
    Stage stage;
    Utilizator user;
    UtilizatorService userService;
    MessageService messageService;
    FriendshipService friendshipService;
    ObservableList<Utilizator> obsFriends = FXCollections.observableArrayList();
    ObservableList<Utilizator> obsParticipants = FXCollections.observableArrayList();

    @FXML
    ListView<Utilizator> FriendsList;
    @FXML
    ListView<Utilizator> ParticipantsList;
    @FXML
    TextField ConversationTitle;

    public void setService(Utilizator user, Stage stage, UtilizatorService userService,MessageService messageService) {
        this.user=user;
        this.stage=stage;
        this.userService=userService;
        this.messageService= messageService;
        this.friendshipService=MainApp.getFriendshipService();
        loadFriends();

    }
    @FXML
    private void initialize() {
        FriendsList.setItems(obsFriends);
        ParticipantsList.setItems(obsParticipants);
    }
    public void loadFriends(){
        FriendsList.getItems().clear();
        if(user==null)
            return;
        List<Utilizator> friends = user.getFriends();
        friends.forEach(x->{
            if(!obsParticipants.contains(x))
                obsFriends.add(x);
        });
        FriendsList.setItems(obsFriends);
    }

    public void addParticipant(){
        Utilizator friend = FriendsList.getSelectionModel().getSelectedItem();
        if(friend==null) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend", "No selection was found");
            return;
        }
        obsParticipants.add(friend);
        ParticipantsList.setItems(obsParticipants);
        obsFriends.remove(friend);
        FriendsList.setItems(obsFriends);
    }

    public void removeParticipant(){
        Utilizator friend = ParticipantsList.getSelectionModel().getSelectedItem();
        if(friend==null) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend", "No selection was found");
            return;
        }
        obsParticipants.remove(friend);
        ParticipantsList.setItems(obsParticipants);
        obsFriends.add(friend);
        FriendsList.setItems(obsFriends);
    }

    public void createConversation(){
        String title = ConversationTitle.getText();
        if(title.equals("")){
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "New conversation", "The title cannot be empty!");
            return;
        }
        if(ParticipantsList.getItems().size()==0){
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "New conversation", "You haven't added any friends! Please do!");
            return;
        }
        try{
            List<Long> to = new ArrayList<>();
            String text = "Initiated this conversation with:";
            for (Utilizator item : ParticipantsList.getItems()) {
                to.add(item.getId());
                text=text.concat(" "+item.getFullName()+",");
            }
            Message conversationStarter = new Message(user.getId(),to,text,title);
            messageService.trimiteMesaj(conversationStarter);
            goToConvMenu();
        }
        catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

    private void goToConvMenu(){
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
