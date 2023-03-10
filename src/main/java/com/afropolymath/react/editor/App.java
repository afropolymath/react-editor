package com.afropolymath.react.editor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private Javalin app;
    protected static final Logger logger = LoggerFactory.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        this.startServer();

        scene = new Scene(loadFXML("primary"));
        scene.getStylesheets().add(App.class.getResource("/css/main.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("React Editor");
        stage.setMaximized(true);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    private void startServer() throws IOException {
        try {
            logger.info("Building Monaco...");
            buildMonaco(true);
            logger.info("Starting server...");
            this.app = Javalin.create(config -> {
                config.staticFiles.add(staticFiles -> {
                    staticFiles.hostedPath = "/"; // change to host files on a subpath, like '/assets'
                    staticFiles.directory = "/web/dist";
                });
            }).start(9000);
        } catch (URISyntaxException e) {
            logger.error("Unable to build Monaco");
        } catch (InterruptedException e) {
            logger.error("Problem running the build commands");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        this.app.stop();
        super.stop(); // To change body of generated methods, choose Tools | Templates.
    }

    private void buildMonaco(Boolean doBuild) throws URISyntaxException, IOException, InterruptedException {
        if (doBuild) {
            File webResourcesFolder = new File(App.class.getResource("/web").toURI());
            new ProcessBuilder("yarn").inheritIO().directory(webResourcesFolder).start().waitFor();
            new ProcessBuilder("yarn", "build").inheritIO().directory(webResourcesFolder).start().waitFor();
        }
    }

}
