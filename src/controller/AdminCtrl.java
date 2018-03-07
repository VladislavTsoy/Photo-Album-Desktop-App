package controller;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import model.*;

/**
 * Admin controller
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class AdminCtrl extends Controller {

	@FXML MenuItem logoutItem;
	@FXML MenuItem exitItem;
	@FXML private ListView<String> usersView;
	@FXML private Button createUser, deleteUser;
	private ObservableList<String> usernames;
	
	@FXML
	public void initialize() {
		Model model = getModel();
	    ObservableList<User> users = model.getAllUsers();
	    usernames = FXCollections.observableArrayList();
	    for(User u : users) {
	    	usernames.add(u.getUsername());
	    }
	    usersView.setItems(usernames);
	}
	
	/**
	 * Opens the window.
	 */
	public void open() {
		adminStage.setScene(adminScene);
	    adminStage.setTitle("Admin");
	    adminStage.setResizable(false);
	    adminStage.show();
	    adminStage.setOnCloseRequest(e -> e.consume());
	}
	
	/**
	 * Create new user.
	 */
	@FXML
	public void create(ActionEvent event) {
		Dialog<String> dialog = new Dialog<String>();
		dialog.setTitle("Create New User");
		
		Label label = new Label("Username:");
		TextField tf = new TextField();
		GridPane grid = new GridPane();
		grid.add(label, 1, 1);
		grid.add(tf, 1, 2);
		dialog.getDialogPane().setContent(grid);
		
		ButtonType btn = new ButtonType("Create", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(btn);
		
		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType param) {
				if(param == btn) {
					if(tf.getText().trim().isEmpty()) {
						   Alert error = new Alert(AlertType.INFORMATION);
						   error.setTitle("Error");
						   error.setHeaderText("Error");
						   String content = "Please enter a username.";
						   error.setContentText(content);
						   error.showAndWait();
						   return null;
					}
				}
				return tf.getText().trim();
			}
		});
		
		Optional<String> result = dialog.showAndWait();
		if(getModel().getUser(result.get()) == null) {
			getModel().addUser(result.get());
			usernames.add(result.get());
			usersView.refresh();
		} else {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Username taken.";
			error.setContentText(content);
			error.showAndWait();
		}
	}
	
	/**
	 * Delete selected user.
	 */
	@FXML
	public void delete(ActionEvent event) {
		int selectIndex = usersView.getSelectionModel().getSelectedIndex();
		
		if(selectIndex < 0) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Please select a user to delete.";
			error.setContentText(content);
			error.showAndWait();
		}
		
		String selected = usersView.getSelectionModel().getSelectedItem();
		User toDelete = getModel().getUser(selected);
		if(toDelete.getUsername().equalsIgnoreCase("admin")) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Admin cannot be deleted.";
			error.setContentText(content);
			error.showAndWait();
			return;
		}
		getModel().getAllUsers().remove(toDelete);
		usernames.remove(selectIndex);
		usersView.refresh();
	}
	
	/**
	 * Admin log off
	 */
	@FXML
	public void logout(ActionEvent event) {
		adminStage.close();
		logout();
	} 
	
	@FXML
	public void exit(ActionEvent event) {
		quitApp();
	}

	
	
	
}
