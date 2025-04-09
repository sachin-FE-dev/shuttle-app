require("dotenv").config();
const express = require("express");
const bcrypt = require("bcryptjs");
const jwt = require("jsonwebtoken");
const { DynamoDBDocumentClient, PutCommand, GetCommand, ScanCommand } = require("@aws-sdk/lib-dynamodb");
const { DynamoDBClient } = require("@aws-sdk/client-dynamodb");
const { v4: uuidv4 } = require("uuid");
const auth = require("./auth.js");

const app = express();
app.use(express.json());
const dynamoClient = new DynamoDBClient({ region: process.env.AWS_REGION });
const docClient = DynamoDBDocumentClient.from(dynamoClient);
const USERS_TABLE = process.env.USERS_TABLE;
const SHUTTLE_TABLE = process.env.LIVE_SHUTTLE_STOP_TABLE;

// Generate JWT Token
function generateToken(user) {
    return jwt.sign(
        { id: user.id, email: user.email, role: user.role },
        process.env.JWT_SECRET,
        { expiresIn: process.env.JWT_EXPIRES_IN }
    );
}

// Register API (Add User)
app.post("/register", async (req, res) => {
    const { name, email, password, role, routeId, routeColor } = req.body;

    // Validate role
    if (!["driver", "location_manager", "passenger"].includes(role)) {
        return res.status(400).json({ error: "Invalid role" });
    }

    if (role === "driver" && (!routeId || !routeColor)) {
        return res.status(400).json({ error: "Driver must have a routeId and routeColor" });
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    const user = {
        id: Date.now() * 1000 + Math.floor(Math.random() * 1000),
        name,
        email,
        password: hashedPassword,
        role
    };
    // Only include route details if the user is a driver
    if (role === "driver") {
        user.routeId = routeId;
        user.routeColor = routeColor;
    }
    try {
        await docClient.send(new PutCommand({
            TableName: USERS_TABLE,
            Item: user
        }));
        res.json({ message: "User registered successfully" });
    } catch (error) {
        console.error("DynamoDB Error:", error);
        res.status(500).json({ error: "Error saving user" });
    }
});

// Login API 
app.post("/login", async (req, res) => {
    const { email, password } = req.body;

    try {
        // Scan the table to find user by email
        const { Items } = await docClient.send(new ScanCommand({
            TableName: USERS_TABLE,
            FilterExpression: "email = :email",
            ExpressionAttributeValues: {
                ":email": email
            }
        }));

        if (!Items || Items.length === 0) {
            return res.status(401).json({ error: "User not found" });
        }

        const user = Items[0]; // Get first matching user

        // Compare hashed password
        const validPassword = await bcrypt.compare(password, user.password);
        if (!validPassword) {
            return res.status(401).json({ error: "Invalid credentials" });
        }

        // Generate token
        const token = generateToken(user);
        res.json({
            token, role: user.role, ...(user.role === "driver" && {
                routeId: user.routeId,
                routeColor: user.routeColor
            })
        });

    } catch (error) {
        console.error("DynamoDB Login Error:", error);
        res.status(500).json({ error: "Error logging in" });
    }
});

// Get Shuttle Stops API
app.get("/shuttle-stops", async (req, res) => {
    try {
        const { Items } = await docClient.send(new ScanCommand({ TableName: SHUTTLE_TABLE }));

        res.json({ success: true, data: Items });
    } catch (error) {
        console.error("DynamoDB Fetch Error:", error);
        res.status(500).json({ error: "Error fetching shuttle stops" });
    }
});

// Add Shuttle Route to DynamoDB
app.post("/shuttle-stops", async (req, res) => {
    const { id, name, color, stops } = req.body;

    if (!id || !name || !color || !Array.isArray(stops)) {
        return res.status(400).json({ error: "Missing or invalid route data" });
    }

    const newRoute = {
        id,
        name,
        color,
        stops
    };

    try {
        await docClient.send(new PutCommand({
            TableName: SHUTTLE_TABLE,
            Item: newRoute
        }));

        res.json({ message: "Shuttle route added successfully", route: newRoute });
    } catch (error) {
        console.error("Error adding shuttle route:", error);
        res.status(500).json({ error: "Could not add shuttle route" });
    }
});


// Start the server
app.listen(3000, () => console.log("Server running on http://localhost:3000"));
