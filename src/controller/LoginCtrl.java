package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.*;

/**
 * Login controller
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class LoginCtrl extends Controller {
	
	@FXML TextField usernameFld;
	@FXML Button loginBtn;
	
	/**
	 * Sets the stage.
	 */
	public void open() {
		loginStage.setScene(loginScene);
		loginStage.setTitle("Login");
		loginStage.setResizable(false);
		loginStage.show();
		
		loginStage.setOnCloseRequest(event -> {
				quitApp();
	      });
	}

	/**
	 * Logs into the appropriate subsystem.
	 * 
	 * @param event
	 */
	@FXML
	public void login(ActionEvent event) {
		String username = usernameFld.getText().trim();
		if(username.isEmpty()) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String msg = "Please enter a valid username.";
			error.setContentText(msg);
			error.showAndWait();
			return;
		} 
		
		User user = getModel().getUser(username);
		if(user == null) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String msg = "User does not exist.";
			error.setContentText(msg);
			error.showAndWait();
			return;
		} else if(user.getUsername().equalsIgnoreCase("admin")) {
			// go to admin window
			getModel().setCurrentUser("admin");
			usernameFld.clear();
			loginStage.close();
			adminCtrl.open();
			adminStage.show();
		} else {
			// go to user's subsystem
			getModel().setCurrentUser(user.getUsername());
			usernameFld.clear();
			loginStage.close();
			userCtrl.initialize();
			userCtrl.open();
			userStage.show();
		}
	}
	
}
