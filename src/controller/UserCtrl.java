package controller;

import java.io.File;
import java.util.Optional;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import model.*;

/**
 * User controller
 * 
 * @author Jane Chang
 * @author Vladislav Tsoy
 */
public class UserCtrl extends Controller {
	
	@FXML private DatePicker startDate, endDate;
	@FXML private TextField typeText, valueText;
	@FXML private ListView<Tag> tagView;
	@FXML private Button dateSearch, tagAdd, tagDelete, tagSearch;
	@FXML private TableView<Album> albumsTable;
	@FXML private TableColumn<Album, String> colName;
	@FXML private TableColumn<Album, Integer> colSize;
	@FXML private TableColumn<Album, String> colStart;
	@FXML private TableColumn<Album, String> colEnd;
	private ObservableList<Tag> tagsForSearch = FXCollections.observableArrayList();
	private ObservableList<Album> albumList;
	
	/**
	 * Initializes the scene.
	 */
	@FXML
	public void initialize() {
		Model model = getModel();
		User user = model.getCurrentUser();
		albumList = user.getAlbumList();
		
		// setting up ListView for tags
		tagView.setCellFactory(new Callback<ListView<Tag>, ListCell<Tag>>() {
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
		tagView.setItems(tagsForSearch);
		
		// setting up TableView for albums
		colName.setCellValueFactory(p -> p.getValue().getAlbumName());
		colSize.setCellValueFactory(p -> p.getValue().getSize().asObject());
		colStart.setCellValueFactory(p -> p.getValue().getStartDate());
		colEnd.setCellValueFactory(p -> p.getValue().getEndDate());
		
		albumsTable.getItems().clear();
		albumsTable.getItems().addAll(albumList);
		albumsTable.refresh();
		
		albumsTable.setRowFactory(t -> {
			TableRow<Album> row = new TableRow<Album>();
			getModel().getCurrentUser().setCurrentAlbum(row.getItem());
			
			// right clicking on album
			ContextMenu menu = new ContextMenu();
			MenuItem renameA = new MenuItem("Rename");
			renameA.setOnAction(e -> {
				// rename album
				Dialog<String> dialog = new Dialog<String>();
				dialog.setTitle("Rename Album");
				
				Label label = new Label("Album Name:");
				TextField tf = new TextField();
				tf.setText(row.getItem().getAlbumName().get());
				
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
								   String content = "Album name cannot be empty.";
								   error.setContentText(content);
								   error.showAndWait();
								   return null;
							}
						}
						return tf.getText().trim();
					}
				});
				
				Optional<String> result = dialog.showAndWait();
				row.getItem().setAlbumName(result.get());
				albumsTable.refresh();
			});
			MenuItem deleteA = new MenuItem("Delete");
			deleteA.setOnAction(e -> {
				// delete album
				albumsTable.getItems().remove(row.getItem());
				user.getAlbumList().remove(row.getItem());
				albumsTable.refresh();
			});
			menu.getItems().add(renameA);
			menu.getItems().add(deleteA);
            row.contextMenuProperty().bind(Bindings.when(row.emptyProperty()).then((ContextMenu)null).otherwise(menu));
			
			// double clicking on album to open
			row.setOnMouseClicked(event -> {
				if(!row.isEmpty() && event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
					user.setCurrentAlbum(row.getItem());
					albumCtrl.open();
				}
			});

			// row.setOnContextMenuRequested(e -> menu.show(row, e.getScreenX(), e.getScreenY()));
			
			return row;
		});
	}
	
	/**
	 * Sets the stage.
	 */
	public void open() {
		userStage.setScene(userScene);
		userStage.setTitle("Hello " + getModel().getCurrentUser().getUsername() + "!");
		userStage.setResizable(false);
		userStage.setOnCloseRequest(e -> e.consume());
		
	} 
	
	/**
	 * Adds a tag to the list for searching photos.
	 * If the tag already exists, does not add.
	 * 
	 * @param event
	 */
	@FXML
	public void addToList(ActionEvent event) {
		if(typeText.getText().trim().isEmpty() || valueText.getText().trim().isEmpty()) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Please fill both fields.";
			error.setContentText(content);
			error.showAndWait();
			return;
		}
		
		// if the tag already exists, return
		for(Tag t : tagsForSearch) {
			if(t.getTagType().equalsIgnoreCase(typeText.getText().trim())) {
				if(t.getTagValue().equalsIgnoreCase(valueText.getText().trim())) {
					return;
				}
			}
		}
		
		Tag tag = new Tag(typeText.getText(), valueText.getText());
		tagsForSearch.add(tag);
		// tagView.refresh();
		return;
	}
	

	/**
	 * Logout
	 * 
	 * @param event
	 */
	@FXML
	public void logout(ActionEvent event) {
		userStage.close();
		albumStage.close();
		logout();
	}
	
	/**
	 * Quit app
	 * 
	 * @param event
	 */
	@FXML
	public void exit(ActionEvent event) {
		quitApp();
	}
	
	/**
	 * Create new album
	 * 
	 * @param event
	 */
	@FXML
	public void createNew(ActionEvent event) {
		Dialog<String> dialog = new Dialog<String>();
		dialog.setTitle("Create New Album");
		
		Label label = new Label("Album Name:");
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
						   String content = "Album name cannot be empty.";
						   error.setContentText(content);
						   error.showAndWait();
						   return null;
					}
				}
				return tf.getText().trim();
			}
		});
		
		Optional<String> result = dialog.showAndWait();
		Album another = new Album(result.get());
		getModel().getCurrentUser().addAlbum(result.get());
		albumsTable.getItems().add(another);
	}
	
	
	/**
	 * Deletes a tag from the search list.
	 * 
	 * @param event
	 */
	@FXML
	public void deleteFromList(ActionEvent event) {
		Tag item = tagView.getSelectionModel().getSelectedItem();
		int index = tagView.getSelectionModel().getSelectedIndex();
		
		if(index < 0) {
			Alert error = new Alert(AlertType.INFORMATION);
			error.setTitle("Error");
			error.setHeaderText("Error");
			String content = "Please select a tag to delete.";
			error.setContentText(content);
			error.showAndWait();
			return;
		}
		
		tagsForSearch.remove(item);
		return;

	}
	
	@FXML
	public void searchList(ActionEvent event) {
		Album searchSave = null;
		if(getModel().getCurrentUser().getAlbum("Search Results").equals(null)) {
			searchSave = new Album("Search Results");
		}
		
		User user = getModel().getCurrentUser();
		for(int i = 0; i < user.getAlbumList().size(); i++) {
			Album album = user.getAlbumList().get(i);
			for(int j = 0; j < album.getPhotos().size(); j++) {
				Photo photo = album.getPhotos().get(j);
				for(int k = 0; k < photo.getTags().size(); k++) {
					Tag tag = photo.getTags().get(k);
					for(int l = 0; l < tagsForSearch.size(); l++) {
						if(tag.getTagType().equalsIgnoreCase(tagsForSearch.get(l).getTagType())) {
							if(tag.getTagValue().equalsIgnoreCase(tagsForSearch.get(l).getTagValue())) {
								searchSave.addPhoto(new File(photo.getPath()));
							}
						}
					}
				}
			}
		}
		
		if(searchSave.getSize().get() == 0) {
			return;
		}
		
		user.addAlbum("Search Results");
		albumsTable.getItems().add(searchSave);
		return;
	}
	
}
