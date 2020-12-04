package socialnetwork.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import socialnetwork.MainApp;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.MessageService;
import socialnetwork.service.UtilizatorService;

import java.util.ArrayList;
import java.util.List;

public class ContinueConversationViewController {
    Stage stage;
    Utilizator user;
    UtilizatorService userService;
    MessageService messageService;
    Long conversationId;
    Message lastMessage;

    @FXML
    javafx.scene.control.TextArea TextArea;

    @FXML
    TextField UserTextArea;
    @FXML
    Button SendButton;
    @FXML
    Label ConversationName;

    @FXML
    private void initialize() {
        TextArea.setDisable(true);
        TextArea.setStyle("-fx-opacity: 1;");
    }

    public void setService(Utilizator user, Stage stage, UtilizatorService userService,MessageService messageService,Long conversationId) {
        this.user=user;
        this.stage=stage;
        this.userService=userService;
        this.messageService= messageService;
        this.conversationId=conversationId;
        loadMessages();
    }

    private void loadMessages() {
        TextArea.clear();
        List<Message> messages = messageService.getConversation(conversationId);
        messages.forEach(x->{
            TextArea.appendText(userService.getOne(x.getFrom()).getFullName() + ": -" +x.getText()+"\n");
        });
        lastMessage=messages.get(messages.size()-1);
        ConversationName.setText(messages.get(0).getConversationName());
    }

    public void sendMessage(){
        String message=UserTextArea.getText();
        if(message.equals(""))
            return;
        if(message.length()>1000)
        {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Message", "The message can have at most 1000 characters!");
        }
        try{
            List<Long> sendTO=new ArrayList<>();
            lastMessage.getTo().forEach(x->{
                if(!x.equals(user.getId()))
                    sendTO.add(x);
            });
            if(!lastMessage.getFrom().equals(user.getId()))
                sendTO.add(lastMessage.getFrom());
            messageService.trimiteMesaj(new Message(user.getId(),sendTO,message,lastMessage.getId()));
            TextArea.appendText(user.getFullName() + ": -" +message+"\n");

        }
        catch (Exception e)
        {
            MessageAlert.showErrorMessage(null,e.getMessage());
        }
        finally {
            UserTextArea.clear();
        }
    }
}
