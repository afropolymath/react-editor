package com.afropolymath.react.editor.bridge;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EditorBridge {
    private File projectFolder;
    protected static final Logger logger = LoggerFactory.getLogger(EditorBridge.class);

    public EditorBridge(File projectFolder) {
        logger.info("Initializing the bridge...");
        setProjectFolder(projectFolder);
    }

    /**
     * Log a message from the Javascript environment in the Java logs
     * 
     * @param logMessage
     */
    public void logToJava(String logMessage) {
        logger.info(logMessage);
    }

    public String readFileContentsFromPath(String filePath) {
        return FileWrangler.readFileContentsFromPath(filePath);
    }

    public String getProjectFolderPath() {
        return projectFolder.getAbsolutePath();
    }

    public String getRelativeFilePath(String filePath) {
        return projectFolder.toURI().relativize(new File(filePath).toURI()).getPath();
    }

    public void setProjectFolder(File projectFolder) {
        this.projectFolder = projectFolder;
    }

    public String[] getFilesInDirectory(String filePath) {
        File currentFile = new File(filePath);
        File parentDirectory = currentFile.getParentFile();
        File[] listOfFiles = parentDirectory.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();
        for (File singleFile : listOfFiles) {
            fileNames.add(singleFile.getName());
        }
        return fileNames.toArray(new String[0]);
    }
}
