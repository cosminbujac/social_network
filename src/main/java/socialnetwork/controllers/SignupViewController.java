package socialnetwork.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.domain.Utilizator;
import socialnetwork.service.UtilizatorService;

import java.awt.*;
import java.io.IOException;

public class SignupViewController {
    UtilizatorService userService;
    Stage stage;

    public void setService(UtilizatorService userService, Stage stage) {
        this.userService = userService;
        this.stage = stage;
    }
    @FXML
    TextField FirstName;
    @FXML
    TextField LastName;
    @FXML
    PasswordField Password;
    @FXML
    Button LogIn;
    @FXML
    Button SignUp;

    @FXML
    private void initialize() {

    }

    public void signUp()
    {
        String firstname=FirstName.getText();
        String lastname= LastName.getText();
        String password = Password.getText();
        try {
            Utilizator user=userService.addUtilizator(firstname,lastname,password);
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Signup Success", "Congrats, you have a new account!You can go back to login now!");
        } catch (Exception e) {
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Signup Failure", e.getMessage());
        }
    }

    public void backToLogin(){
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
}
