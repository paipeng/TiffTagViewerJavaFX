package com.paipeng.tifftagviewer.controller;

import com.paipeng.tifftagviewer.model.TiffTag;
import com.paipeng.tifftagviewer.utils.CommonUtils;
import com.paipeng.tifftagviewer.utils.TiffUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TiffTagViewerViewController implements Initializable {
    public static Logger logger = Logger.getLogger(WelcomeViewController.class);
    private static Stage stage;
    private static final String FXML_FILE = "/fxml/TiffTagViewerViewController.fxml";
    private static ResourceBundle resources;

    @FXML
    private TableView tiffTagTableView;

    @FXML
    private Button selectTiffButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initTiffTagTableView();
    }


    public static void start() {
        try {
            resources = ResourceBundle.getBundle("bundles.languages", CommonUtils.getCurrentLanguageLocale());
            Parent root = FXMLLoader.load(TiffTagViewerViewController.class.getResource(FXML_FILE), resources);

            Scene scene = new Scene(root);
            stage = new Stage();
            stage.setTitle(resources.getString("appTitle"));
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(true);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void addImageButtonClicked(MouseEvent mouseEvent) throws IOException {
        logger.info("addImageButtonClicked");
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter(resources.getString("selectTiff"), "tif", "tiff");
        fileChooser.setSelectedExtensionFilter(extensionFilter);

        File selectedFile = fileChooser.showOpenDialog(((Node) mouseEvent.getTarget()).getScene().getWindow());
        if (selectedFile != null) {
            logger.info("selected file: " + selectedFile.getAbsolutePath());
            updateTiffTagTableView(selectedFile.getAbsolutePath());
        }
    }

    private void initTiffTagTableView() {
        try {
            TableColumn tableColumn = (TableColumn) tiffTagTableView.getColumns().get(0);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>("tagName"));

            tableColumn = (TableColumn) tiffTagTableView.getColumns().get(1);
            tableColumn.setCellValueFactory(new PropertyValueFactory<>("tagValue"));
        } catch (NullPointerException e) {
            logger.error(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            logger.error(e.getMessage());
        }
    }

    private void cleanTiffTagTableView() {
        tiffTagTableView.getItems().clear();
    }

    private void updateTiffTagTableView(String imagePath) throws IOException {
        cleanTiffTagTableView();

        TiffUtils.updateTableView(tiffTagTableView.getItems(), TiffUtils.readTiffTag(imagePath));

//        TiffTag tiffTag = new TiffTag("test", "123");
//        tiffTagTableView.getItems().add(tiffTag);

    }
}
