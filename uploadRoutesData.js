import fetch from "node-fetch";
import fs from "fs";

// Your ORS API key
const apiKey = "5b3ce3597851110001cf6248ab5ee321b6344abf909912f1a40b041f";

// Stops in [lat, lon] but ORS expects [lon, lat]
const stops =  [
  [32.8998, -97.0403], // DFW Airport
  [32.9250, -97.0080], // Las Colinas Blvd
  [32.9340, -97.0025], // Mandalay Canal
  [32.9400, -96.9925], // North Lake College
  [32.9550, -96.9750], // Irving Convention Center
  [32.9650, -96.9650], // Toyota Music Factory
  [32.9750, -96.9550], // MacArthur Blvd & Royal Lane
  [32.8998, -97.0403]  // Back to DFW Airport
];

function sleep(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

async function getRoute(start, end) {
  const url = "https://api.openrouteservice.org/v2/directions/driving-car/geojson";

  const body = {
    coordinates: [[start[1], start[0]], [end[1], end[0]]]
  };

  const response = await fetch(url, {
    method: "POST",
    headers: {
      "Authorization": apiKey,
      "Content-Type": "application/json"
    },
    body: JSON.stringify(body)
  });

  const data = await response.json();

  if (!data || !data.features || !data.features[0]) {
    console.error("❌ Invalid route response for:", start, "→", end);
    return [];
  }

  return data.features[0].geometry.coordinates.map(([lon, lat]) => [lat, lon]);
}

(async () => {
  let fullRoute = [];

  for (let i = 0; i < stops.length - 1; i++) {
    console.log(`🔄 Routing: Stop ${i} → ${i + 1}`);
    const segment = await getRoute(stops[i], stops[i + 1]);
    if (i > 0 && segment.length > 0) segment.shift(); // avoid duplicate point
    fullRoute.push(...segment);
    // await sleep(1000); // prevent rate limiting
  }

  console.log("✅ Total route points:", fullRoute.length);

  // Save output
  fs.writeFileSync("realistic_purple_route.json", JSON.stringify(fullRoute, null, 2));
  console.log("📁 Saved to realistic_green_route.json");
})();