import { useState } from "react";
import MapView from "./components/MapView";

function App() {
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [routeData, setRouteData] = useState(null); // ✅ FIX IS HERE
  const [error, setError] = useState("");

  const fetchRoute = async () => {
    try {
      setError("");
      setRouteData(null);

      if (!from || !to) {
        setError("Please enter both source and destination");
        return;
      }

      const response = await fetch(
        `http://localhost:8080/api/route?from=${from}&to=${to}`
      );

      if (!response.ok) {
        throw new Error("Route not found");
      }

      const data = await response.json();
      setRouteData(data); // ✅ now defined
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ padding: "10px" }}>
      <h2>Route Finder</h2>

      <input
        placeholder="From city"
        value={from}
        onChange={(e) => setFrom(e.target.value)}
      />

      <input
        placeholder="To city"
        value={to}
        onChange={(e) => setTo(e.target.value)}
        style={{ marginLeft: "10px" }}
      />

      <button onClick={fetchRoute} style={{ marginLeft: "10px" }}>
        Find Route
      </button>

      {error && <p style={{ color: "red" }}>{error}</p>}

      <MapView routeData={routeData} />
    </div>
  );
}

export default App;
