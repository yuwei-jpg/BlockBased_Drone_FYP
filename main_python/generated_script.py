import asyncio
import csv
import math

from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude, AccelerationNed
from mavsdk.offboard import (OffboardError, PositionNedYaw)
from csv_generated import generate_l_shape_trajectory_csv
from csv_generated import generate_z_trajectory_csv
from csv_generated import generate_circle_trajectory_csv
from csv_generated import generate_spiral_ascend_trajectory_csv
from csv_generated import generate_triangle_trajectory_csv
from csv_generated import generate_roller_coaster_trajectory_csv
from csv_generated import generate_s_shape_trajectory_csv
from csv_generated import generate_square_trajectory_csv
from read_csv import execute_trajectory_other
from read_csv import execute_trajectory
from move_relatively import move_relative


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
    await drone.action.arm()
    await drone.offboard.set_position_ned(PositionNedYaw(0, 0, 0, 0))
    await drone.offboard.start()
    generate_roller_coaster_trajectory_csv()
    await execute_trajectory("roller_coaster_trajectory.csv", 0, 0.1, drone)
    await drone.offboard.stop()
    await drone.action.land()
    # Make sure all lines have only 4 spaces indented


# execute run() function
asyncio.run(run())
