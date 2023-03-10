package com.afropolymath.react.editor.ui;

import com.afropolymath.react.editor.TabController;

public class EditorOpenTab {
    private Integer tabIndex;
    private TabController tabController;
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public TabController getTabController() {
        return tabController;
    }

    public void setTabController(TabController tabController) {
        this.tabController = tabController;
    }

    public Integer getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(Integer tabIndex) {
        this.tabIndex = tabIndex;
    }

    public EditorOpenTab(Integer index, String filePath, TabController controller) {
        this.tabController = controller;
        this.tabIndex = index;
        this.filePath = filePath;
    }
}