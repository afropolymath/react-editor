import React, { PropsWithChildren } from "react";

type ErrorBoundaryState = {
  hasError: boolean;
  error: any;
};

export default class ErrorBoundary extends React.Component<
  PropsWithChildren<{}>
> {
  public state: ErrorBoundaryState;

  constructor(props: {}) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error: any) {
    return { hasError: true, error };
  }

  render() {
    if (this.state.hasError) {
      return (
        <>
          <h1>Something went wrong</h1>
          <div>{String(this.state.error)}</div>
        </>
      );
    }

    return this.props.children;
  }
}
