package controller;

import java.io.ObjectOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.*;

/**
 * Parent class for all controllers.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class Controller {

	public static Model model;
	
	public static Stage loginStage;
	public static Scene loginScene;
	public static LoginCtrl loginCtrl;
	public static Stage adminStage;
	public static Scene adminScene;
	public static AdminCtrl adminCtrl;
	public static Stage userStage;
	public static Scene userScene;
	public static UserCtrl userCtrl;
	public static Stage albumStage;
	public static Scene albumScene;
	public static Scene photoScene;
	public static AlbumCtrl albumCtrl;
	
	private final static String fileName = "/data/Photos.dat";
	
	/**
	 * Gets current model for the application, or creates a new model object if it is being opened on a new machine.
	 * 
	 * @return Model
	 */
	public static Model getModel() {
		// Upon program start.
		if(model == null) {
			try {
				FileInputStream inputFile = new FileInputStream(fileName);
				ObjectInputStream inputObj = new ObjectInputStream(inputFile);
				model = (Model)inputObj.readObject();
				inputFile.close();
				inputObj.close();
			} catch(IOException | ClassNotFoundException e) {
				model = null;
			}
		}
		
		// Opening application for the first time on user's machine.
		if(model == null) {
			model = new Model();
			
			model.addUser("admin");
			model.addUser("stock");
			
			model.setCurrentUser("stock");
			User stocky = model.getCurrentUser();
			
			stocky.addAlbum("Christmas");
			stocky.setCurrentAlbum(stocky.getAlbum("Christmas"));
			
			stocky.getCurrentAlbum().addPhoto(new File("data/stock/christmas1.jpg"));
			stocky.getCurrentAlbum().addPhoto(new File("data/stock/christmas2.jpg"));
			stocky.getCurrentAlbum().addPhoto(new File("data/stock/christmas3.jpg"));
			
			stocky.addAlbum("Belgium");
			stocky.setCurrentAlbum(stocky.getAlbum("Belgium"));
			
			stocky.getCurrentAlbum().addPhoto(new File("data/stock/belgium1.jpg"));
			stocky.getCurrentAlbum().addPhoto(new File("data/stock/belgium2.jpg"));
			
		}
		
		return model;
	}
	
	/**
	 * Sets the stages and scenes for the program.
	 * 
	 * @param stage 	Primary stage upon startup
	 * @throws Exception
	 */
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(getClass().getResource("/view/Login.fxml"));
	    Parent root = loader.load();
	    loginCtrl = loader.getController();
	    loginStage = stage;
	    loginScene = new Scene(root);
	    
		loader = new FXMLLoader();   
		loader.setLocation(getClass().getResource("/view/AdminHome.fxml"));
	    root = loader.load();
	    adminCtrl = loader.getController();
	    adminStage = new Stage();
	    adminScene = new Scene(root);
	    
		loader = new FXMLLoader(); 
		loader.setLocation(getClass().getResource("/view/UserHome.fxml"));
	    root = loader.load();
	    userCtrl = loader.getController();
	    userStage = new Stage();
	    userScene = new Scene(root);
	    
		loader = new FXMLLoader();   
		loader.setLocation(getClass().getResource("/view/AlbumHome.fxml"));
	    root = loader.load();
	    albumCtrl = loader.getController();
	    albumStage = new Stage();
	    albumScene = new Scene(root);
	    
	    loginCtrl.open();
	} 
	
	/**
	 * Logs user out and returns to login screen.
	 */
	public void logout() {
	    LoginCtrl login = new LoginCtrl();
	    login.open();
	}
	
	/**
	 * Exits the program.
	 */
	public void quitApp() {
		try {
			FileOutputStream outputFile = new FileOutputStream(fileName);
			ObjectOutputStream outputObj = new ObjectOutputStream(outputFile);
			outputObj.writeObject(model);
			outputFile.close();
			outputObj.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		Platform.exit();
		System.exit(0);
	}
}
