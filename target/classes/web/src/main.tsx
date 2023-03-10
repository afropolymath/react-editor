import { createRoot } from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { install } from "resize-observer";
import { App } from "./App";

import "./css/main.css";

if (!window.ResizeObserver) install();

const container = document.getElementById("app");

const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
  },
]);

if (container) {
  const root = createRoot(container);
  root.render(<RouterProvider router={router} />);
}
