package com.afropolymath.react.editor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.afropolymath.react.editor.bridge.EditorChangedEvent;
import com.afropolymath.react.editor.ui.ReactPreviewEngine;
import com.afropolymath.react.editor.ui.SimpleFileTreeItem;
import com.afropolymath.react.editor.ui.TabManager;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class PrimaryController implements Initializable {
    private static final Boolean REACT_PREVIEW_ENABLED = false;

    private TabManager tabManager;
    private String devDocsUrl = "https://devdocs.io/";
    protected static final Logger logger = LoggerFactory.getLogger(PrimaryController.class);

    @FXML
    private TabPane mainTabArea;

    @FXML
    private TreeView<File> workspaceTreeView;

    @FXML
    private ComboBox<Integer> fontSizeSelector;

    @FXML
    private WebView devDocsWebView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine devDocsWebViewEngine = devDocsWebView.getEngine();
        devDocsWebViewEngine.setJavaScriptEnabled(true);
        devDocsWebViewEngine.load(devDocsUrl);

        fontSizeSelector.getItems().addAll(11, 12, 13, 14, 15, 16, 17);

        tabManager = new TabManager();
        initializeWorkspaceTreeView();

        if (Boolean.TRUE.equals(REACT_PREVIEW_ENABLED)) {
            initializeEditorTabPreviewListener(devDocsWebViewEngine);
        }
    }

    @FXML
    private void openFileInNewTab(File selectedFile) throws IOException {
        TabController newTabController = tabManager.addOpenTab(mainTabArea, selectedFile);
        newTabController.initializeBridgeData(workspaceTreeView.getRoot().getValue());
        newTabController.openFilePathInTab(selectedFile.getPath());
    }

    @FXML
    private void openFolderInCurrentWorkspace() {
        Stage primaryStage = (Stage) mainTabArea.getScene().getWindow();
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        workspaceTreeView.setRoot(new SimpleFileTreeItem(selectedDirectory.getAbsoluteFile()));
    }

    @FXML
    private void updateFontSize() {
        tabManager.getSelectedTab(mainTabArea).getTabController().refreshEditor();
    }

    /**
     * Initializes the workspace tree including formatting the tree item text as
     * well as adding in
     * click event listeners on tree items etc.
     */
    private void initializeWorkspaceTreeView() {
        workspaceTreeView.setCellFactory(tv -> new TreeCell<File>() {
            @Override
            public void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        workspaceTreeView.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<TreeItem<File>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<File>> observable,
                            TreeItem<File> oldValue, TreeItem<File> newValue) {
                        if (newValue != null) {
                            File selectedFile = newValue.getValue();
                            try {
                                if (selectedFile.isFile())
                                    openFileInNewTab(selectedFile);
                            } catch (IOException e) {
                                logger.error("Unable to open file " + selectedFile.getPath());
                            }
                        }

                    }
                });
    }

    /**
     * Initializes the React Preview engine which will generate and refresh the
     * preview of the currently
     * edited component in the preview tab
     * 
     * @param previewTabWebEngine Pointer to the preview tab web engine
     */
    private void initializeEditorTabPreviewListener(WebEngine previewTabWebEngine) {
        try {
            File webResourcesFolder = new File(App.class.getResource("/web").toURI());
            mainTabArea.addEventFilter(EditorChangedEvent.EDITOR_CONTENT_CHANGED, event -> {
                try {
                    String previewUrl = new ReactPreviewEngine(webResourcesFolder,
                            workspaceTreeView.getRoot().getValue()).generatePreview(event.getPath());
                    previewTabWebEngine.load(previewUrl);
                } catch (IOException e) {
                    logger.error("Problem generating the preview");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    logger.error("Error while executing one of the preview build commands");
                    e.printStackTrace();
                }
            });
        } catch (URISyntaxException e) {
            logger.error("Was unable to load the web resources folder");
            e.printStackTrace();
        }
    }
}
