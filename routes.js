const authMiddleware = require("./auth");

app.get("/protected-route", authMiddleware, (req, res) => {
    res.json({ message: `Hello, ${req.user.role}!`, userId: req.user.id });
});