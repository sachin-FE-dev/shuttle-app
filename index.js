const WebSocket = require('ws');

const wss = new WebSocket.Server({ port: 8080, host: '0.0.0.0' });

wss.on('connection', (ws) => {
    console.log("Client connected");

    ws.send(JSON.stringify({ message: "Welcome to GPS WebSocket Server!" }));

    ws.on('message', (data) => {
        console.log("Received GPS Data:", data.toString());

        // Broadcast GPS data to all connected clients
        wss.clients.forEach(client => {
            if (client !== ws && client.readyState === WebSocket.OPEN) {
                client.send(data.toString());
            }
        });
    });

    ws.on('close', () => console.log("Client disconnected"));
});

console.log("WebSocket Server running on ws://localhost:8080");