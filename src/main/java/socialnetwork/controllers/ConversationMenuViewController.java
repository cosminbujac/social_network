package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import socialnetwork.MainApp;
import socialnetwork.domain.FriendshipRequestDTO;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConversationMenuViewController {
    Stage stage;
    Utilizator user;
    UtilizatorService userService;
    MessageService messageService;
    @FXML
    ListView<Tuple<Long,String>> ConversationView;
    ObservableList<Tuple<Long,String>> obsConversation = FXCollections.observableArrayList();

    public void setService(Utilizator user, Stage stage, UtilizatorService userService) {
        this.user=user;
        this.stage=stage;
        this.userService=userService;
        this.messageService= MainApp.getMessageService();
        loadConversations();
    }
    @FXML
    private void initialize() {
        ConversationView.setItems(obsConversation);
    }
    public void toHomepage(){
        FXMLLoader homepageLoader = new FXMLLoader();
        homepageLoader.setLocation(getClass().getResource("/views/homepageView.fxml"));
        AnchorPane homepageLayout = null;
        try {
            homepageLayout = homepageLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        HomepageViewController homepageViewController = homepageLoader.getController();
        homepageViewController.setService(user,stage, userService);
        stage.setScene(new Scene(homepageLayout));
    }

    private void loadConversations(){
        ConversationView.getItems().clear();
        if(user==null)
            return;
        List<Tuple<Long,String>> conversations =  messageService.getAllConvsOf(user.getId());
        obsConversation.addAll(conversations);
        ConversationView.setItems(obsConversation);
    }

    public void continueConversation(){
        Tuple<Long,String> conversation=ConversationView.getSelectionModel().getSelectedItem();
        if(conversation==null) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Conversation", "No selection was found");
            return;
        }
        FXMLLoader contiueConversationLoader = new FXMLLoader();
        contiueConversationLoader.setLocation(getClass().getResource("/views/continueConversationView.fxml"));
        AnchorPane conversationLayout = null;
        try {
            conversationLayout = contiueConversationLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        Stage stage = new Stage();
        stage.setTitle("Conversation");
        stage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(conversationLayout);
        stage.setScene(scene);
        stage.show();
        ContinueConversationViewController viewController = contiueConversationLoader.getController();
        viewController.setService(user,stage, userService,messageService,conversation.getLeft());
    }

    public void newConv(){
        FXMLLoader newConversationLoader = new FXMLLoader();
        newConversationLoader.setLocation(getClass().getResource("/views/newConversationView.fxml"));
        AnchorPane conversationLayout = null;
        try {
            conversationLayout = newConversationLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        stage.setScene(new Scene(conversationLayout));
        NewConversationViewController viewController = newConversationLoader.getController();
        viewController.setService(user,stage, userService,messageService);
    }
}

