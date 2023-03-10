import { PropsWithChildren } from "react";
import { createRoot } from "react-dom/client";
import { install } from "resize-observer";

if (!window.ResizeObserver) install();

const container = document.getElementById("app");

function Preview({ children }: PropsWithChildren) {
  return <>{children}</>;
}

if (container) {
  const root = createRoot(container);
  root.render(<Preview></Preview>);
}
