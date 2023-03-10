package com.afropolymath.react.editor.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.afropolymath.react.editor.bridge.FileWrangler;

public class ReactPreviewEngine {
    private File webResourcesFolder;
    private File baseProjectPath;
    protected static final Logger logger = LoggerFactory.getLogger(ReactPreviewEngine.class);

    public ReactPreviewEngine(File webResourcesFolder, File baseProjectPath) {
        this.webResourcesFolder = webResourcesFolder;
        this.baseProjectPath = baseProjectPath;
    }

    private StringSubstitutor getStringSubstitutor(String filePath) {
        // Necessary parameters as it relates to the current file and project
        String baseName = FilenameUtils.getBaseName(filePath);
        String folderHash = DigestUtils.sha256Hex(baseName);
        String relativeComponentPath = baseProjectPath.toURI().relativize(new File(filePath).toURI()).getPath();

        // TODO: Replace this logic with better templating logic
        // Create Hashmap to hold fields to be replaced
        HashMap<String, String> templateParameters = new HashMap<>();
        templateParameters.put("relativeComponentPath",
                relativeComponentPath.substring(0, relativeComponentPath.lastIndexOf('.')));
        templateParameters.put("componentName", baseName);
        templateParameters.put("projectName", baseProjectPath.getName());
        templateParameters.put("folderHash", folderHash.substring(0, 12));

        // We are using the StringSubstitutor as our "templating engine"
        return new StringSubstitutor(templateParameters);
    }

    public void generateAndWriteReactPreviewTemplates(StringSubstitutor sub) throws IOException {
        String webResourcesFolderPath = webResourcesFolder.getAbsolutePath();

        // Previews for each component will be output in this /previews folder.
        // Subfolder is the hashed component name
        Path previewFileOutputPath = Paths
                .get(FilenameUtils.concat(webResourcesFolderPath, sub.replace("previews/${folderHash}")));

        // Template replacement string
        String importLineTemplate = "import ${componentName} from '${projectName}/${relativeComponentPath}';\n";
        String componentInjectionSearch = "<Preview></Preview>";
        String componentInjectionTemplate = "<Preview><${componentName} /></Preview>";

        // We obtain the file paths for the template files we will be replacing
        String reactTemplateFilePath = FilenameUtils.concat(webResourcesFolderPath, "templates/preview.react.tsx");
        String htmlTemplateFilePath = FilenameUtils.concat(webResourcesFolderPath, "templates/base.engine.html");

        // We read the contents of the template file
        String reactTemplateFile = FileWrangler.readFileContentsFromPath(reactTemplateFilePath);

        // We process the template file making the necessary replacements and inclusions
        String processedTemplateFile = new StringBuilder(sub.replace(importLineTemplate))
                .append(reactTemplateFile.replace(componentInjectionSearch, sub.replace(componentInjectionTemplate)))
                .toString();

        // Create the output folder for the previews if it doesn't already exist
        if (!Files.exists(previewFileOutputPath)) {
            Files.createDirectories(previewFileOutputPath);
        }

        // Create the paths for the templates to be written
        String previewAbsoluteBasePath = previewFileOutputPath.toAbsolutePath().toString();
        String previewReactPath = FilenameUtils.concat(previewAbsoluteBasePath, "preview.react.tsx");
        String previewHTMLPath = FilenameUtils.concat(previewAbsoluteBasePath, "index.html");
        String TSConfigFilePath = FilenameUtils.concat(previewAbsoluteBasePath, "tsconfig.json");

        // Write the templates to the respective files
        FileWrangler.writeContentsToFilePath(previewReactPath, processedTemplateFile);
        FileWrangler.writeContentsToFilePath(previewHTMLPath,
                FileWrangler.readFileContentsFromPath(htmlTemplateFilePath));
        FileWrangler.writeContentsToFilePath(TSConfigFilePath,
                FileWrangler.readFileContentsFromPath(
                        FilenameUtils.concat(baseProjectPath.getAbsolutePath(), "tsconfig.json")));
    }

    public String generatePreview(String filePath) throws IOException, InterruptedException {
        StringSubstitutor sub = getStringSubstitutor(filePath);
        generateAndWriteReactPreviewTemplates(sub);

        String buildOutputDirectory = sub.replace("dist/${folderHash}");
        String buildSourceFile = sub.replace("previews/${folderHash}/index.html");

        new ProcessBuilder("yarn").inheritIO().directory(webResourcesFolder).start().waitFor();
        new ProcessBuilder("yarn", "link", baseProjectPath.getName()).inheritIO().directory(webResourcesFolder)
                .start()
                .waitFor();
        new ProcessBuilder("yarn", "build", buildSourceFile, "--dist-dir", buildOutputDirectory, "--public-url",
                sub.replace("/${folderHash}")).inheritIO()
                .directory(webResourcesFolder).start().waitFor();
        return sub.replace("http://localhost:9000/${folderHash}");

    }

}
