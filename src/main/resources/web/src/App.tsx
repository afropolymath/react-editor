import Editor from "./components/Editor";
import ErrorBoundary from "./ErrorBoundary";

export function App() {
  return (
    <ErrorBoundary>
      <Editor />
    </ErrorBoundary>
  );
}
