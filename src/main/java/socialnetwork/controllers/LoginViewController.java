package socialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.repository.database.FriendshipRequestDBRepo;
import socialnetwork.service.FriendshipRequestService;
import socialnetwork.service.UtilizatorService;
import javafx.scene.control.Button;

import java.io.IOException;

public class LoginViewController {
    UtilizatorService userService;
    Stage stage;
    @FXML
    PasswordField Password;
    @FXML
    TextField ID;
    @FXML
    Button LogIn;
    @FXML
    Button SignUp;

    @FXML
    private void initialize() {

    }

    public void setService(UtilizatorService userService, Stage stage) {
        this.userService = userService;
        this.stage = stage;
    }

    public void logIn() {
        try {
            String password = Password.getText();
            Long id = Long.valueOf(ID.getText());
            Utilizator user = userService.getOne(id);
            if (user==null)
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Login Failure", "The user with this id doesn't exist!");
            if (UtilizatorService.check(password, user.getPassword())) {
                toHomepage(user);
            } else {
                MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Login Failure", "Incorrect ID or password");
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Login Failure", "Incorrect ID or password");
        }
    }
    public void signUp() {
        FXMLLoader signupLoader = new FXMLLoader();
        signupLoader.setLocation(getClass().getResource("/views/signupView.fxml"));
        AnchorPane singupLayout = null;
        try {
            singupLayout = signupLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
        stage.setScene(new Scene(singupLayout));

        SignupViewController signupViewController = signupLoader.getController();
        signupViewController.setService(userService,stage);
    }

    private void toHomepage(Utilizator user){
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

}
