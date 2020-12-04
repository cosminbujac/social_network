package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.FriendshipRequestDTO;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.UtilizatorService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddFriendViewController {
    FriendshipRequestService friendshipRequestService;
    UtilizatorService userService;
    Utilizator user;
    Stage stage;
    ObservableList<Utilizator> obsStrangers = FXCollections.observableArrayList();
    ObservableList<FriendshipRequestDTO> obsSent = FXCollections.observableArrayList();

    @FXML
    TextField SearchPerson;
    @FXML
    Button SendFriendRequest;
    @FXML
    TableView<Utilizator> addFriendsTable;
    @FXML
    TableColumn<Utilizator,String> FirstNameColumn;
    @FXML
    TableColumn<Utilizator,String> LastNameColumn;
    @FXML
    ListView<FriendshipRequestDTO> SentFriendReqListview;

    @FXML
    private void initialize() {
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator,String>("lastName"));
        addFriendsTable.setItems(obsStrangers);
        SentFriendReqListview.setItems(obsSent);
    }

    public void setService(Utilizator user, Stage stage, UtilizatorService userService,FriendshipRequestService friendshipRequestService) {
        this.user=user;
        this.stage=stage;
        this.userService = userService;
        this.friendshipRequestService = friendshipRequestService;
        loadSuggestions();
        loadSentFriendRequests();
    }

    public void loadSuggestions(){
        obsStrangers.setAll(getStrangers());
    }

    private List<Utilizator> getStrangers(){
        if(user==null)
            return null;
        List<Utilizator> strangers=new ArrayList<Utilizator>();
        userService.getAll().forEach(strangers::add);
        friendshipRequestService.getFriendshipRequestsInvolving(user.getId()).forEach(x->{
            if(!x.getStatus().equals("rejected")){
                if (x.getReceiver().equals(user.getId()))
                    strangers.remove(userService.getOne(x.getSender()));
                else
                    strangers.remove(userService.getOne(x.getReceiver()));
            }
        });
        strangers.remove(user);
        return strangers;
    }

    public void sendFriendRequest(){
        Utilizator stranger=addFriendsTable.getSelectionModel().getSelectedItem();
        if(stranger==null) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Unfriend", "No selection was found");
            return;
        }
        friendshipRequestService.addFriendshipRequest(user.getId(),stranger.getId());
        MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Friend request", "Friend request sent!");
        loadSentFriendRequests();
        loadSuggestions();
    }

    public void findSomeone(){
        obsStrangers.setAll(getStrangers().stream().
                filter(x->x.getLastName().startsWith(SearchPerson.getText())||x.getFirstName().startsWith(SearchPerson.getText()))
                .collect(Collectors.toList()));
    }

    public void loadSentFriendRequests(){
        SentFriendReqListview.getItems().clear();
        if(user==null)
            return;
        List<FriendshipRequestDTO>requests = new ArrayList<FriendshipRequestDTO>();
        friendshipRequestService.getSentRequests(user.getId()).forEach(x->{
            if(x.getStatus().equals("pending"))
                requests.add(new FriendshipRequestDTO(x, userService.getOne(x.getReceiver())));
        });
        obsSent.setAll(requests);
    }

    public void cancelFriendRequest(){
        FriendshipRequestDTO friendshipReq=SentFriendReqListview.getSelectionModel().getSelectedItem();
        if(friendshipReq==null) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Requests", "No selection was found");
            return;
        }
        try{
            if(friendshipRequestService.deleteRequest(friendshipReq.getFriendshipRequest().getId())!=null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Request", "Request cancelled!");
                loadSuggestions();
                loadSentFriendRequests();
        }catch (Exception e){
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
    }

}
