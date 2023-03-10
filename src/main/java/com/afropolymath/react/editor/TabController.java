package com.afropolymath.react.editor;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.afropolymath.react.editor.bridge.EditorBridge;
import com.afropolymath.react.editor.bridge.EditorChangedEvent;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

/**
 *
 * @author chidieberennadi
 */
public class TabController {
    private EditorBridge editorBridge;
    private WebEngine engine;
    private String baseEditorUrl = "http://0.0.0.0:9000/";
    protected static final Logger logger = LoggerFactory.getLogger(TabController.class);

    @FXML
    private WebView tabWebView;

    @FXML
    public void initialize() {
        engine = tabWebView.getEngine();
        engine.setJavaScriptEnabled(true);

        editorBridge = new EditorBridge(null);

        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state,
                    Worker.State t1) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("bridge", editorBridge);
                // Now where ever console.log is called in your html you will get a log in Java
                // console
                engine.executeScript("console.log = function(message) { bridge.logToJava(message); }");
                engine.executeScript(
                        "console.error = function(message) { bridge.logToJava(message); }");
            }
        });

        engine.load(baseEditorUrl);
        tabWebView.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            try {

                String updatedFilePath = new URI(engine.getLocation()).getFragment();
                String updatedEditorContent = (String) engine.executeScript("window.editor.getValue()");
                tabWebView
                        .fireEvent(new EditorChangedEvent(EditorChangedEvent.EDITOR_CONTENT_CHANGED, updatedFilePath,
                                updatedEditorContent));
            } catch (URISyntaxException e) {
                logger.error("Bad URL syntax" + e);
            }
        });
    }

    public void openFilePathInTab(String filePath) {
        engine.load(baseEditorUrl + '#' + filePath);
    }

    public void initializeBridgeData(File projectFolder) {
        editorBridge.setProjectFolder(projectFolder);
    }

    public void refreshEditor() {
        engine.reload();
    }
}
