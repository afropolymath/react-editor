package com.afropolymath.react.editor.ui;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.afropolymath.react.editor.App;
import com.afropolymath.react.editor.TabController;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class TabManager {
    private HashMap<String, EditorOpenTab> openTabFileMap;

    public TabManager() {
        openTabFileMap = new HashMap<>();
    }

    public TabController addOpenTab(TabPane tabPane, File selectedFile) throws IOException {
        ObservableList<Tab> openTabs = tabPane.getTabs();
        String filePath = selectedFile.getAbsolutePath();

        EditorOpenTab newOpenTab = new EditorOpenTab(openTabs.size(), filePath, null);
        EditorOpenTab previousOpenTab = openTabFileMap.putIfAbsent(filePath, newOpenTab);

        if (previousOpenTab != null) {
            tabPane.getSelectionModel().select(openTabs.get(previousOpenTab.getTabIndex()));
            return previousOpenTab.getTabController();
        }

        FXMLLoader fxmlLoader = new FXMLLoader();
        Tab newFileTab = fxmlLoader.load(App.class.getResource("/fxml/editor-tab.fxml").openStream());
        TabController tabController = fxmlLoader.getController();

        newOpenTab.setTabController(tabController);

        newFileTab.setText(selectedFile.getName());
        newFileTab.setOnClosed(event -> closeOpenTab(filePath));
        openTabs.add(newFileTab);
        tabPane.getSelectionModel().select(newFileTab);

        return tabController;
    }

    public void closeOpenTab(String filePath) {
        EditorOpenTab deletedTab = openTabFileMap.remove(filePath);
        if (deletedTab != null) {
            openTabFileMap.forEach((key, openTab) -> {
                if (openTab.getTabIndex() > deletedTab.getTabIndex()) {
                    openTab.setTabIndex(openTab.getTabIndex() - 1);
                }
            });
        }
    }

    public EditorOpenTab getTabByIndex(Integer index) {
        for (Map.Entry<String, EditorOpenTab> entry : openTabFileMap.entrySet()) {
            EditorOpenTab tab = entry.getValue();
            if (tab.getTabIndex().equals(index)) {
                return tab;
            }
        }
        return null;
    }

    public EditorOpenTab getSelectedTab(TabPane tabPane) {
        Integer selectedTabIndex = tabPane.getSelectionModel().getSelectedIndex();
        return getTabByIndex(selectedTabIndex);
    }
}
