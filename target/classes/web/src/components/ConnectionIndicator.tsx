type ConnectionIndicatorProps = {
  connected: boolean;
  lastPing: string | null;
};

export default function ConnectionIndicator({
  connected = false,
  lastPing,
}: ConnectionIndicatorProps) {
  return (
    <div
      style={{ display: "flex", alignItems: "center", padding: "0.5em 1em" }}
    >
      <div style={{ flexGrow: 1 }}>
        <i
          style={{
            display: "inline-block",
            width: "10px",
            height: "10px",
            backgroundColor: connected ? "green" : "red",
          }}
        />
        <span style={{ marginLeft: "10px" }}>
          {connected ? "Connected" : "Disconnected"}
        </span>
      </div>
      <span>Last Ping: {lastPing}</span>
    </div>
  );
}
