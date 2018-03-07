package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.*;

/**
 * Album controller.
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class AlbumCtrl extends Controller {

	// note: if you want the application to be more efficient and scalable,
	// should save thumbnail images to disk after creation.
	// worry about that later/last/never.
	
	@FXML private StackPane stackpane;
	@FXML private TilePane albumOverview;
	@FXML private AnchorPane albumZoom;
	@FXML private Pagination pages;
	@FXML private ListView<Tag> listOfTags;
	@FXML private Button addBtn, backBtn, addTagBtn, deleteTagBtn;
	@FXML private Text capText, dtText;
	private ObservableList<Tag> theseTags;
	
	/**
	 * Initializes the scene.
	 */
	@FXML
	public void initialize() {
		userStage.setScene(albumScene);
		userStage.setTitle(getModel().getCurrentUser().getCurrentAlbum().getAlbumName().get());
		userStage.setResizable(false);
		userStage.setOnCloseRequest(e -> e.consume());
	}
	
	/**
	 * Sets the stage.
	 */
	public void open() {
		userStage.setScene(albumScene);
		userStage.setTitle(getModel().getCurrentUser().getCurrentAlbum().getAlbumName().get());
		userStage.setResizable(false);
		
		// album overview setup
		albumOverview.toFront();
		albumOverview.getChildren().clear();
		albumZoom.setVisible(false);
		albumOverview.setPadding(new Insets(10, 10, 10, 10));
		albumOverview.setPrefTileWidth(125);
		albumOverview.setPrefTileHeight(125);
		albumOverview.setHgap(20);
		albumOverview.setVgap(15);

		Model model = getModel();
		Album album = model.getCurrentUser().getCurrentAlbum();
		if(album.getPhotos().size() > 0) {
			album.setCurrentPhoto(album.getPhotos().get(0));
		}
		ObservableList<Photo> allPhotos = album.getPhotos();
		// theseTags = model.getCurrentUser().getCurrentAlbum().getCurrentPhoto().getTags();
		
		// tag ListView setup
		listOfTags.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
			@Override
			public ListCell<Tag> call(ListView<Tag> param) {
				ListCell<Tag> cell = new ListCell<Tag>() {
					@Override
					protected void updateItem(Tag current, boolean boo) {
						if(current != null) {
							setText(current.getTagType() + " : " + current.getTagValue());
						} else {
							setText(null);
						}
					}
				};
				return cell;
			}
		});
		
		listOfTags.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> showInfo());
		
		// don't know why the VBoxes don't display in the correct order but we don't have to worry about that urgently
		for(Photo p : allPhotos) {
			VBox vbox = new VBox();
			
			ImageView imgView = new ImageView(p.getThumbnail());
			if(p.getThumbnail().getWidth() > p.getThumbnail().getHeight()) {
				imgView.setFitWidth(125);
			} else {
				imgView.setFitHeight(100);
			}
			
			Label label = new Label(p.getCaption());
			
			vbox.getChildren().add(imgView);
			vbox.getChildren().add(label);
			vbox.setPrefWidth(200);
			vbox.setPrefHeight(150);
			vbox.setAlignment(Pos.CENTER);
			albumOverview.getChildren().add(albumOverview.getChildren().size(), vbox);
			p.setVB(vbox);
			/*
			if(album.getCurrentPhoto().equals(p)) {
				vbox.setStyle("-fx-background-color: lightblue;");
			}
			*/
			
			vbox.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					// display selection
					album.getCurrentPhoto().getVB().setStyle("");
					vbox.setStyle("-fx-background-color: lightblue;");
					album.setCurrentPhoto(p);
					
					// display info
					showInfo();
					
					// double clicked on the thumbnail
					if(e.getClickCount() == 2 && e.getButton().equals(MouseButton.PRIMARY)) {
						zoom(p);
					}
					// right clicked on the thumbnail
					else if(e.getClickCount() == 1 && e.getButton().equals(MouseButton.SECONDARY)) {
						openContext(vbox, p);
					}
				}
			});
			
			
		}
		
		// pagination setup
		albumZoom.setPadding(new Insets(25,25,25,25));
		pages.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer param) {
				return createPage(param);
			}
		});
		pages.setPageCount(album.getSize().get());
	}

	/**
	 * Creates each page within pagination view.
	 * 
	 * @param param	Index of photo
	 * @return		The page
	 */
	public static VBox createPage(Integer param) {
		Model model = getModel();
		User user = model.getCurrentUser();
		Album album = user.getCurrentAlbum();
		
		ImageView imgView = new ImageView();
		int index = param.intValue();
		File file = new File(album.getPhotos().get(index).getPath());
		try {
			Image img = new Image(new FileInputStream(file), 400, 375, true, true);
			imgView.setImage(img);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		
		VBox vbox = new VBox();
		vbox.getChildren().add(imgView);
		vbox.setAlignment(Pos.CENTER);
		return vbox;
	}
	
	/**
	 * When a selected photo is double-clicked, switches to pagination view.
	 * 
	 * @param photo	The selected photo
	 */
	public void zoom(Photo photo) {
		albumZoom.toFront();
		albumOverview.setVisible(false);
		pages.setCurrentPageIndex(photo.getIndex());
		albumZoom.setVisible(true);
		addBtn.setDisable(true);
	}
	
	/**
	 * When a photo is right-clicked, context menu shows.
	 * 
	 * @param photo	The selected photo
	 */
	public void openContext(VBox vbox, Photo photo) {
		Model model = getModel();
		User user = model.getCurrentUser();
		Album album = user.getCurrentAlbum();
		
		ContextMenu menu = new ContextMenu();
		
		// recaption
		MenuItem recaption = new MenuItem("Recaption");
		recaption.setOnAction(event -> {
			Dialog<String> dialog = new Dialog<String>();
			dialog.setTitle("Photo Caption");
			
			Label label = new Label("Caption:");
			TextField tf = new TextField();
			tf.setText(photo.getCaption());
			
			GridPane grid = new GridPane();
			grid.add(label, 1, 1);
			grid.add(tf, 1, 2);
			dialog.getDialogPane().setContent(grid);
			
			ButtonType btn = new ButtonType("OK", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().add(btn);
			
			dialog.setResultConverter(new Callback<ButtonType, String>() {
				@Override
				public String call(ButtonType param) {
					String newCap = "";
					if(param == btn) {
						newCap = tf.getText().trim();
					}
					return newCap;
				}
			});
			
			Optional<String> result = dialog.showAndWait();
			photo.setCaption(result.get());
			Label newCap = new Label(result.get());
			vbox.getChildren().remove(1);
			vbox.getChildren().add(newCap);
			showInfo();
		});
		
		// copy to...
		Menu copyTo = new Menu("Copy to...");
		for(int i = 0; i < user.getAlbumList().size(); i++) {
			Album forCopy = user.getAlbumList().get(i);
			String name = forCopy.getAlbumName().get();
			if(!name.equalsIgnoreCase(album.getAlbumName().get())) {
				MenuItem copy = new MenuItem(name);
				copyTo.getItems().add(copy);
				
				copy.setOnAction(e -> {
					forCopy.addPhoto(new File(photo.getPath())).setCaption(photo.getCaption());;
				});
			}
		}
		
		// move to...
		Menu moveTo = new Menu("Move to...");
		for(int i = 0; i < user.getAlbumList().size(); i++) {
			Album forMove = user.getAlbumList().get(i);
			String name = forMove.getAlbumName().get();
			if(!name.equalsIgnoreCase(album.getAlbumName().get())) {
				MenuItem move = new MenuItem(name);
				moveTo.getItems().add(move);
				
				move.setOnAction(e -> {
					forMove.getPhotos().add(photo);
					albumOverview.getChildren().remove(photo.getVB());
					ObservableList<Photo> allPhotos = album.getPhotos();
					for(int j = 0; j < allPhotos.size(); j++) {
						if(allPhotos.get(j).equals(photo)) {
							allPhotos.remove(j);
						}
					}
				});
			}
		}
		
		// delete
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(event -> {
			albumOverview.getChildren().remove(photo.getVB());
			album.deletePhoto(photo);
		});
		
		menu.getItems().add(recaption);
		menu.getItems().add(copyTo);
		menu.getItems().add(moveTo);
		menu.getItems().add(delete);
		vbox.setOnContextMenuRequested(e -> menu.show(vbox, e.getScreenX(), e.getScreenY()));
	}
	
	/**
	 * Add a photo into the album.
	 * 
	 * @param event	On button clicked
	 */
	@FXML
	public void addPhoto(ActionEvent event) {
		User user = getModel().getCurrentUser();
		Album album = user.getCurrentAlbum();

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"));
		File file = fileChooser.showOpenDialog(userStage);
		
		album.addPhoto(file);
		open();
	}
	
	/**
	 * Go back to album overview from pagination, or back to user's home from album overview.
	 * 
	 * @param event	On button clicked
	 */
	@FXML
	public void goBack(ActionEvent event) {
		if(!albumOverview.isVisible()) {
			addBtn.setDisable(false);
			albumOverview.toFront();
			albumOverview.setVisible(true);
			albumZoom.setVisible(false);
		} else {
			clearInfo();
			userCtrl.initialize();
			userCtrl.open();
		}
		
	}
	
	/**
	 * Add a tag to the selected photo.
	 * 
	 * @param event
	 */
	@FXML
	public void addTag(ActionEvent event) {
		Dialog<String> dialog = new Dialog<String>();
		dialog.setTitle("Add a Tag");
		
		Label typeLabel = new Label("Tag Type:");
		TextField typeTF = new TextField();
		Label valueLabel = new Label("Tag Value:");
		TextField valueTF = new TextField();
		
		GridPane grid = new GridPane();
		grid.add(typeLabel, 1, 1);
		grid.add(typeTF, 2, 1);
		grid.add(valueLabel, 1, 2);
		grid.add(valueTF, 2, 2);
		dialog.getDialogPane().setContent(grid);
		
		ButtonType btn = new ButtonType("Add", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().add(btn);
		
		dialog.setResultConverter(new Callback<ButtonType, String>() {
			@Override
			public String call(ButtonType param) {
				if(param == btn) {
					if(typeTF.getText().trim().isEmpty() || valueTF.getText().trim().isEmpty()) {
						   Alert error = new Alert(AlertType.INFORMATION);
						   error.setTitle("Error");
						   error.setHeaderText("Error");
						   String content = "Please fill in both fields.";
						   error.setContentText(content);
						   error.showAndWait();
						   return null;
					}
				}
				return typeTF.getText().trim() + "," + valueTF.getText().trim();
			}
		});
		
		Optional<String> result = dialog.showAndWait();
		if(result.equals(null)) { return; }
		if(getModel().getCurrentUser().getCurrentAlbum().getCurrentPhoto().tagFound(result.get())) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Tag already exists.";
			error.setContentText(content);
			error.showAndWait();
			return;
		}
		String[] a = result.get().split(",");
		Tag tag = new Tag(a[0], a[1]);
		getModel().getCurrentUser().getCurrentAlbum().getCurrentPhoto().getTags().add(tag);
		// listOfTags.refresh();
		return;
		
	}
	
	/**
	 * Delete a tag from the selected photo.
	 * 
	 * @param event
	 */
	@FXML
	public void deleteTag(ActionEvent event) {
		Tag item = listOfTags.getSelectionModel().getSelectedItem();
		int index = listOfTags.getSelectionModel().getSelectedIndex();
		
		if(index < 0) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Please select a tag to delete.";
			error.setContentText(content);
			error.showAndWait();
			return;
		}
		
		ObservableList<Tag> current = getModel().getCurrentUser().getCurrentAlbum().getCurrentPhoto().getTags();
		current.remove(item);
		// showInfo();
		return;
	}
	
	/**
	 * Displays the selected photo's information on the lefthand side of the window.
	 */
	public void showInfo() {
		Photo p = getModel().getCurrentUser().getCurrentAlbum().getCurrentPhoto();
		capText.setText(p.getCaption());
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy\nHH:mm:ss");
		dtText.setText(new SimpleStringProperty(df.format(p.getDate())).get());
		
		ObservableList<Tag> current = p.getTags();
		listOfTags.setItems(current);
	}
	
	/**
	 * Clears the information on the lefthand side.
	 */
	public void clearInfo() {
		capText.setText("");
		dtText.setText("");
		listOfTags.setItems(null);
	}
	
	/**
	 * User log off
	 */
	@FXML
	public void logout(ActionEvent event) {
		userStage.close();
		logout();
	} 
	
	/**
	 * Quit app
	 * @param event
	 */
	@FXML
	public void exit(ActionEvent event) {
		quitApp();
	}
}
