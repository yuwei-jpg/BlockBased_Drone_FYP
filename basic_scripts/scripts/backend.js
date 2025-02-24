const express = require("express");
const { runServer, stopServer } = require("./server-controller");
const app = express();
const port = 4000;

app.get("/runServer", (req, res) => {
    runServer();
    res.send("Server started.");
});

app.get("/stopServer", (req, res) => {
    stopServer();
    res.send("Server stopped.");
});

app.listen(port, () => {
    console.log(`Control server running on http://localhost:${port}`);
});
