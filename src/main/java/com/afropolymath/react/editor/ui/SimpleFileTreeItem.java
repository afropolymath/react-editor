package com.afropolymath.react.editor.ui;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

/**
 * SimpleFileTreeItem
 * 
 * @see https://www.consulting-bolte.de/index.php/java-se-ee/javafx-and-e-fx-clipse/48-simple-javafx-file-system-tree-view
 */

public class SimpleFileTreeItem extends TreeItem<File> {
    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isLeaf;

    public SimpleFileTreeItem(File f) {
        super(f);
    }

    @Override
    public ObservableList<TreeItem<File>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;

            /*
             * First getChildren() call, so we actually go off and determine the
             * children of the File contained in this TreeItem.
             */
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            File f = (File) getValue();
            isLeaf = f.isFile();
        }
        return isLeaf;
    }

    /**
     * Returning a collection of type ObservableList containing TreeItems, which
     * represent all children available in handed TreeItem.
     * 
     * @param TreeItem
     *                 the root node from which children a collection of TreeItem
     *                 should be created.
     * @return an ObservableList<TreeItem<File>> containing TreeItems, which
     *         represent all children available in handed TreeItem. If the
     *         handed TreeItem is a leaf, an empty list is returned.
     */
    public ObservableList<TreeItem<File>> buildChildren(TreeItem<File> treeItem) {
        File f = treeItem.getValue();
        if (f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                ObservableList<TreeItem<File>> children = FXCollections
                        .observableArrayList();

                for (File childFile : files) {
                    children.add(new SimpleFileTreeItem(childFile));
                }

                children.sort((o1, o2) -> {
                    int firstFileNumber = o1.getValue().isDirectory() ? 0 : 1;
                    int secondFileNumber = o2.getValue().isDirectory() ? 0 : 1;
                    if (firstFileNumber == secondFileNumber) {
                        return o1.getValue().getName().compareTo(o2.getValue().getName());
                    }
                    return firstFileNumber - secondFileNumber;
                });
                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }
}