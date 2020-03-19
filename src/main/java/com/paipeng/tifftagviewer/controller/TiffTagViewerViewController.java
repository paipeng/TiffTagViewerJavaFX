package com.paipeng.tifftagviewer.controller;

import com.paipeng.tifftagviewer.utils.CommonUtils;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class TiffTagViewerViewController implements Initializable {
    public static Logger logger = Logger.getLogger(WelcomeViewController.class);
    private static Stage stage;
    private static final String FXML_FILE = "/fxml/TiffTagViewerViewController.fxml";
    private  static ResourceBundle resources;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    public static void start() {
        try {
            resources = ResourceBundle.getBundle("bundles.languages", CommonUtils.getCurrentLanguageLocale());
            Parent root = FXMLLoader.load(TiffTagViewerViewController.class.getResource(FXML_FILE),resources);

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
}
