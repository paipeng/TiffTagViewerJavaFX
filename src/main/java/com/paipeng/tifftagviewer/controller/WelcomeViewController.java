package com.paipeng.tifftagviewer.controller;

import com.paipeng.tifftagviewer.utils.CommonUtils;
import com.sun.tools.javac.util.List;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeViewController implements Initializable {
    public static Logger logger = Logger.getLogger(WelcomeViewController.class);
    private static Stage stage;
    private static final String FXML_FILE = "/fxml/WelcomeViewController.fxml";
    private static ResourceBundle resources;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        waitSleep();
    }


    public static void start() {
        try {
            resources = ResourceBundle.getBundle("bundles.languages", CommonUtils.getCurrentLanguageLocale());
            Parent root = FXMLLoader.load(WelcomeViewController.class.getResource(FXML_FILE), resources);

            Scene scene = new Scene(root);
            stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setResizable(true);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void waitSleep() {
        AsynchronSleepTask<Integer> task = new AsynchronSleepTask<>();

        task.setOnSucceeded(wse -> {
                    stage.close();
                    TiffTagViewerViewController.start();
                }
        );

        Thread t = new Thread(task, "wait-sleep-thread");
        t.setDaemon(true);
        t.start();
    }

    // The Task implementation
    private static class AsynchronSleepTask<E> extends Task<List<E>> {


        private AsynchronSleepTask() {
            updateTitle("AsynchronSleepTask");
        }

        @Override
        protected List<E> call() throws Exception {
            Thread.sleep(2000);


            return null;
        }

        @Override
        protected void running() {
            System.out.println("Sorting task is running...");
        }

        @Override
        protected void succeeded() {
            System.out.println("Sorting task successful.");
        }

    }
}
