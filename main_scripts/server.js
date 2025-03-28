const express = require("express");
const { exec, spawn } = require("child_process");
const app = express();
const cors = require('cors');
const {join} = require("path");

const fs = require("fs");
const config = JSON.parse(fs.readFileSync("config.json", "utf8"));
config.px4_path = "./PX4-Autopilot";

app.use(cors());
app.use(express.json());

let simulatorProcess = null;
let pythonProcess = null;
let simulatorReady = false;

app.get("/", (req, res) => {
    res.send("MAVSDK Server is running! Use POST /run_MAVSDK to execute drone commands.");
});

app.get("/run_MAVSDK", (req, res) => {
    res.status(405).send("Method Not Allowed. Please use POST request to execute drone commands.");
});

app.post("/run_simulator", (req, res) => {
    if (simulatorProcess) {
        return res.status(400).json({
            status: "error",
            message: "Simulator is already running"
        });
    }

    const px4Path = config.px4_path;
// "/Users/jiyuwei/PX4-Autopilot";
    simulatorProcess = spawn('make', ['px4_sitl', 'jmavsim'], {
        cwd: px4Path,
        shell: true,
        env: process.env
    });

    simulatorProcess.stdout.on('data', (data) => {
        const output = data.toString();
        console.log(`Simulator output: ${output}`);

        if (output.includes("Ready for takeoff!")) {
            simulatorReady = true;
            res.json({
                status: "success",
                message: "Simulator is ready for takeoff"
            });
        }
    });

    simulatorProcess.stderr.on('data', (data) => {
        console.error(`Simulator error: ${data}`);
    });

    simulatorProcess.on('error', (error) => {
        console.error(`Failed to start simulator: ${error}`);
        simulatorProcess = null;
        simulatorReady = false;
        res.status(500).json({
            status: "error",
            message: "Failed to start simulator"
        });
    });

    setTimeout(() => {
        if (!simulatorReady) {
            res.status(500).json({
                status: "error",
                message: "Simulator startup timeout"
            });
        }
    }, 30000);
});

app.get("/simulator_status", (req, res) => {
    res.json({
        status: simulatorReady ? "ready" : "not_ready"
    });
});

app.get("/simulator_view", (req, res) => {
       // Returns the simulator's HTML page or redirects to the simulator's URL
    res.sendFile(join(__dirname, 'path/to/simulator.html'));
});

app.post("/run_MAVSDK", async (req, res) => {
    try {
        console.log("Received request body:", req.body);
        const { code } = req.body;

        const fs = require('fs');
        const path = require('path');
        const scriptPath = path.join(__dirname,'../main_python', 'generated_script.py');
        const formattedCode = code.split('\n').map(line => '    ' + line).join('\n');


        const fullScript = `
import asyncio
import csv
from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw,VelocityBodyYawspeed,Attitude,AccelerationNed
from mavsdk.offboard import (OffboardError, PositionNedYaw)

async def run():
    """ Does Offboard control using position NED coordinates. """
    mode_descriptions = {
        0: "On the ground",
        10: "Initial climbing state",
        20: "Initial holding after climb",
        30: "Moving to start point",
        40: "Holding at start point",
        50: "Moving to maneuvering start point",
        60: "Holding at maneuver start point",
        70: "Maneuvering (trajectory)",
        80: "Holding at the end of the trajectory coordinate",
        90: "Returning to home coordinate",
        100: "Landing"
    }
    drone = System()
    await drone.connect(system_address="udp://:14540")

    print("Waiting for drone to connect...")
    async for state in drone.core.connection_state():
        if state.is_connected:
            print(f"-- Connected to drone!")
            break

    print("Waiting for drone to have a global position estimate...")
    async for health in drone.telemetry.health():
        if health.is_global_position_ok and health.is_home_position_ok:
            print("-- Global position estimate OK")
            break

        # Here is generated code
${formattedCode}  # Make sure all lines have only 4 spaces indented

# execute run() function
asyncio.run(run())
`;

        fs.writeFileSync(scriptPath, fullScript);

        const { exec } = require('child_process');
        exec(`python ${scriptPath}`, (error, stdout, stderr) => {
            if (error) {
                console.error(`Error: ${error.message}`);
                return res.status(500).send("Error running script.");
            }
            if (stderr) {
                console.error(`Stderr: ${stderr}`);
            }
            console.log(`Stdout: ${stdout}`);
            res.send("Code executed successfully!");
        });

    } catch (error) {
        console.error('MAVSDK execution error:', error);
        res.status(500).json({
            status: "error",
            message: error.message || "Code execution failed"
        });
    }
});

app.post("/cleanup", (req, res) => {
    if (simulatorProcess) {
        simulatorProcess.kill();
        simulatorProcess = null;
    }
    if (mavsdkProcess) {
        mavsdkProcess.kill();
        mavsdkProcess = null;
    }
    res.json({ status: "success", message: "Cleanup completed" });
});

app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({
        status: "error",
        message: "Internal server error"
    });
});

app.post("/start_server", (req, res) => {
    res.json({ status: "success", message: "Server is running" });
});

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server running on http://localhost:${PORT}`);
});

process.on('SIGTERM', () => {
    if (simulatorProcess) simulatorProcess.kill();
    if (mavsdkProcess) mavsdkProcess.kill();
    process.exit(0);
});
