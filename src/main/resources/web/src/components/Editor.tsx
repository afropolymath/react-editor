import { useState } from "react";
import { useLocation } from "react-router-dom";
import { editor } from "monaco-editor";
import MonacoEditor, { Monaco } from "@monaco-editor/react";

const INITIAL_VALUE = "// type your code...";

/* Declare or import type detailing methods provided by the Java Bridge Interface */
type EditorBridge = {
  getProjectFolderPath: () => string;
  getFilesInDirectory: (filePath: string) => string[];
  readFileContentsFromPath: (filePath: string) => string;
  getRelativeFilePath: (filePath: string) => string;
};

/* Declare bridge variable injected by JavaFX */
declare const window: Window & {
  bridge?: EditorBridge;
  editor?: editor.IStandaloneCodeEditor;
  monaco?: Monaco;
};

const bridge = window.bridge;

export default function Editor() {
  const [editorOptions] = useState<editor.IStandaloneEditorConstructionOptions>(
    {
      fontSize: 14,
      smoothScrolling: true,
      mouseWheelScrollSensitivity: 0.05,
      suggest: {
        showFiles: true,
      },
    }
  );
  const [editorContent, setEditorContent] = useState(INITIAL_VALUE);
  const location = useLocation();
  const filePath = location.hash.substring(1);

  return (
    <div className="editor-container">
      <MonacoEditor
        height="100%"
        defaultLanguage="typescript"
        defaultValue={editorContent}
        value={editorContent}
        options={editorOptions}
        onMount={(standaloneEditor, monaco) => {
          const projectFolder = bridge!.getProjectFolderPath();
          const fileContents = bridge!.readFileContentsFromPath(filePath);
          setEditorContent(fileContents);

          window.monaco = monaco;
          window.editor = standaloneEditor;

          monaco.languages.typescript.typescriptDefaults.setCompilerOptions({
            jsx: monaco.languages.typescript.JsxEmit.React,
            target: monaco.languages.typescript.ScriptTarget.ES2015,
            module: monaco.languages.typescript.ModuleKind.CommonJS,
            moduleResolution:
              monaco.languages.typescript.ModuleResolutionKind.NodeJs,
            experimentalDecorators: true,
            emitDecoratorMetadata: true,
            baseUrl: projectFolder,
          });

          // Configure the editor to use the language service
          monaco.languages.typescript.typescriptDefaults.setEagerModelSync(
            true
          );

          console.log("Configuring completions");

          monaco.languages.registerCompletionItemProvider("typescript", {
            triggerCharacters: [".", "/", "'"],
            provideCompletionItems(model, position, context, token) {
              const { lineNumber } = position;
              var word = model.getWordUntilPosition(position);
              const fileNames = bridge!.getFilesInDirectory(filePath);
              const suggestions = fileNames.map((fileName) => ({
                label: fileName,
                kind: window.monaco!.languages.CompletionItemKind.File,
                insertText: fileName,
                range: {
                  startLineNumber: lineNumber,
                  endLineNumber: lineNumber,
                  startColumn: word.startColumn,
                  endColumn: word.endColumn,
                },
              }));
              return { suggestions };
            },
          });

          console.log(
            `Creating model using contents from ::: ${fileContents.substring(
              0,
              40
            )}...`
          );
          // const relativeFilePath = bridge!.getRelativeFilePath(filePath);
          // standaloneEditor.setModel(
          //   editor.createModel(
          //     fileContents,
          //     undefined,
          //     monaco.Uri.file(`/${relativeFilePath}`)
          //   )
          // );

          // monaco?.languages.typescript.javascriptDefaults.setCompilerOptions(
          //   {
          //     allowNonTsExtensions: true,
          //     allowJs: true,
          //     target: 99,
          //     paths: {
          //       "@/*": [projectFolder],
          //     },
          //     baseUrl: projectFolder,
          //   }
          // );
        }}
      />
    </div>
  );
}
