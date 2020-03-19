package com.paipeng.tifftagviewer;

import com.paipeng.tifftagviewer.controller.WelcomeViewController;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.stage.Stage;

public class TiffTagViewerApplication extends Application {

    public static HostServices hostServices;
    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = getHostServices();
        WelcomeViewController.start();
    }
}
