import asyncio
import csv
from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude, AccelerationNed
from mavsdk.offboard import (OffboardError, PositionNedYaw)

from main_python.csv_generated import generate_s_shape_trajectory_csv
from main_python.move_relatively import move_relative
from main_python.read_csv import execute_trajectory_other
from movements.spiral_ascend_shape.sa_path import log_position_velocity


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

    position_task = asyncio.create_task(log_position_velocity(drone))

    # Here is the generated code
    speed = float(3)
    await drone.action.arm()
    await drone.action.set_takeoff_altitude(10)
    await drone.action.takeoff()
    await asyncio.sleep(10)
    await drone.action.set_current_speed(speed)
    await move_relative(drone, 5, 10, 5)
    await drone.action.set_return_to_launch_altitude(10)
    await drone.action.return_to_launch()
    await asyncio.sleep(15)
    await drone.action.land()

    position_task.cancel()

    # Make sure all lines have only 4 spaces indented


# Execute run() function
asyncio.run(run())
