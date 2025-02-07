from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude
from mavsdk.offboard import (OffboardError, PositionNedYaw)
from main_python.spiral_ascend import spiral_ascend
import asyncio


async def run():
    """ Does Off-board control using position NED coordinates. """
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

    # Generated code
    await drone.action.arm()
    await drone.offboard.set_position_ned(PositionNedYaw(0, 0, 0, 0))
    await drone.offboard.start()
    await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 0, -2, 0),
                                                   VelocityNedYaw(0, 0, 0.5, 0))
    await asyncio.sleep(10)
    await spiral_ascend(drone, 1000, 7, 1, 8)

    # Make sure all lines have only 4 spaces indented


# Execute the run()
asyncio.run(run())
