import csv
import math

from mavsdk import System
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude, AccelerationNed
from mavsdk.offboard import (PositionNedYaw)
import asyncio
from csv_generated import generate_z_trajectory_csv, generate_roller_coaster_trajectory_csv, \
    generate_spiral_ascend_trajectory_csv, generate_circle_trajectory_csv, generate_square_trajectory_csv, \
    generate_l_shape_trajectory_csv, generate_s_shape_trajectory_csv, generate_triangle_trajectory_csv
from read_csv import execute_trajectory, execute_trajectory_other


async def run():
    """ Does Off-board control using position NED coordinates. """

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

# Execute the run()
asyncio.run(run())
