from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude
from mavsdk.offboard import (OffboardError, PositionNedYaw)

from main_python.roller_coaster import roller_coaster
from main_python.spiral_ascend import spiral_ascend
from main_python.zigzag_flight import zigzag_flight
import asyncio


async def run():
    """ Does Off-board control using position NED coordinates. """
    drone = System()
    await drone.connect() # system_address="udp://:14540"
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
    await drone.action.arm()
    await drone.offboard.set_position_ned(PositionNedYaw(0, 0, 0, 0))
    await drone.offboard.start()
    await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 0, -30, 0),
                                                   VelocityNedYaw(0, 0, 1, 0))
    await asyncio.sleep(10)
    # await spiral_ascend(drone, 100, 7, 1, 8)
    # await zigzag_flight(drone, 10, 10, 6)
    print("-- Begin to do the roller coaster")
    await roller_coaster(drone, duration=20, rotation_speed=60, altitude=10)
    print("-- roller coaster")
    await asyncio.sleep(10)
    await drone.offboard.stop()
    await drone.action.land()

    # Make sure all lines have only 4 spaces indented


# Execute the run()
asyncio.run(run())
