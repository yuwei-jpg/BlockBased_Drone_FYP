import math

from mavsdk import System
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude, AccelerationNed
from mavsdk.offboard import (PositionNedYaw)
import csv
import asyncio


def get_current_waypoint(waypoints, time):
    return next((wp for wp in waypoints if time <= wp[0]), None)


async def run():
    """ Does Off-board control using position NED coordinates. """
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
    await drone.connect()  # system_address="udp://:14540"
    # await drone.connect(system_address="tcp://127.0.0.1:5760")
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

    # Generated code

    # Make sure all lines have only 4 spaces
    print("-- Disarming")
    await drone.action.disarm()


# Execute the run()
asyncio.run(run())
