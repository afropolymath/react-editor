package com.afropolymath.react.editor.bridge;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileWrangler {
    public static String readFileContentsFromPath(String filePath) {
        return readFileContentsFromPath(filePath, Charset.defaultCharset());
    }

    public static String readFileContentsFromPath(String filePath, Charset encoding) {
        System.out.println("Reading contents from - " + filePath);
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            return new String(encoded, encoding);
        } catch (IOException e) {
            System.err.println(e);
            System.err.println("Error reading contents from - " + filePath);
            return null;
        }
    }

    public static void writeContentsToFilePath(String filePath, String content) throws IOException {
        Files.write(Paths.get(filePath), content.getBytes());
    }
}
