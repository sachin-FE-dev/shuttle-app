const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });
wss.on('connection', (ws) => {
    console.log(":white_check_mark: Client connected");
    // Send a welcome message
    ws.send(JSON.stringify({ message: "Welcome to GPS WebSocket Server!" }));
    ws.on('message', (data) => {
        console.log(":round_pushpin: Received GPS Data:", data.toString());
        // Broadcast GPS data to all connected clients
        wss.clients.forEach(client => {
            if (client !== ws && client.readyState === WebSocket.OPEN) {
                client.send(data.toString());
            }
        });
    });
    ws.on('close', () => console.log(":red_circle: Client disconnected"));
});
console.log(":rocket: WebSocket Server running on ws://localhost:8080");
