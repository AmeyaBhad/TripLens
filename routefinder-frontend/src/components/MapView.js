import {
  MapContainer,
  TileLayer,
  Polyline,
  Marker,
  Popup
} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";

// ✅ SAME COLOR ICON FOR BOTH MARKERS
const markerIcon = new L.Icon({
  iconUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png",
  shadowUrl:
    "https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png",
  iconSize: [25, 41],
  iconAnchor: [12, 41]
});

// ⏱ Convert seconds → readable time
const formatTime = (seconds) => {
  const hrs = Math.floor(seconds / 3600);
  const mins = Math.floor((seconds % 3600) / 60);
  return hrs > 0 ? `${hrs} hr ${mins} min` : `${mins} min`;
};

function MapView({ routeData }) {
  if (!routeData) {
    return (
      <MapContainer
        center={[20.5937, 78.9629]}
        zoom={5}
        style={{ height: "80vh", marginTop: "10px" }}
      >
        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
      </MapContainer>
    );
  }

  const routes = routeData.features;

  // 📍 Start & End
  const start = routes[0].geometry.coordinates[0];
  const end =
    routes[0].geometry.coordinates[
      routes[0].geometry.coordinates.length - 1
    ];

  return (
    <MapContainer
      center={[start[1], start[0]]}
      zoom={6}
      style={{ height: "80vh", marginTop: "10px" }}
    >
      <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />

      {/* ✅ SAME COLOR MARKERS */}
      <Marker position={[start[1], start[0]]} icon={markerIcon}>
        <Popup>Start</Popup>
      </Marker>

      <Marker position={[end[1], end[0]]} icon={markerIcon}>
        <Popup>Destination</Popup>
      </Marker>

      {/* 🛣 ALTERNATIVE ROUTES */}
      {routes.map((route, index) => {
        const coordinates = route.geometry.coordinates.map(
          ([lng, lat]) => [lat, lng]
        );

        const distanceKm =
          (route.properties.summary.distance / 1000).toFixed(2);
        const timeReadable = formatTime(
          route.properties.summary.duration
        );

        return (
          <Polyline
            key={index}
            positions={coordinates}
            pathOptions={{
              color: index === 0 ? "blue" : "gray",
              weight: index === 0 ? 6 : 4,
              opacity: 0.7
            }}
          >
            {/* 🧠 HOVER INFO */}
            <Popup>
              <b>Route {index + 1}</b>
              <br />
              Distance: {distanceKm} km
              <br />
              Time: {timeReadable}
            </Popup>
          </Polyline>
        );
      })}
    </MapContainer>
  );
}

export default MapView;
