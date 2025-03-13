import asyncio
import csv
from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude, AccelerationNed
from mavsdk.offboard import (OffboardError, PositionNedYaw)

from main_python.csv_generated import generate_s_shape_trajectory_csv
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

    # 这里是生成的代码

    speed = float(3)
    await drone.action.arm()
    await drone.action.set_takeoff_altitude(10)
    await drone.action.takeoff()
    await asyncio.sleep(10)
    await drone.action.set_current_speed(speed)
    await drone.action.goto_location(180, 90, 10, 0)
    await asyncio.sleep(10)
    await drone.action.set_return_to_launch_altitude(10)
    await drone.action.return_to_launch()
    await asyncio.sleep(15)
    await drone.action.land()

    position_task.cancel()

    # 确保所有行都只有 4 个空格缩进


# 执行 run 函数
asyncio.run(run())

#
# await drone.action.arm()
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 0, 0, 0),
#                                                VelocityNedYaw(0, 0, 0, 0))
# await drone.offboard.start()
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 0, -5, 0),
#                                                VelocityNedYaw(1.5, 1.5, 0, 0))
# await asyncio.sleep(5)
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(10, 10, -5, 0),
#                                                VelocityNedYaw(0.76, 0.76, 0, 0))
# await asyncio.sleep(0.2)
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(10, 10, -5, 0),
#                                                VelocityNedYaw(-0.76, 0.76, 0, 0))
# await asyncio.sleep(5)
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 20, -5, 0),
#                                                VelocityNedYaw(-0.76, 0.76, 0, 0))
# await asyncio.sleep(0.2)
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 20, -5, 0),
#                                                VelocityNedYaw(0.76, 0.76, 0, 0))
# await asyncio.sleep(5)
# await drone.offboard.set_position_velocity_ned(PositionNedYaw(10, 30, -5, 0),
#                                                VelocityNedYaw(1.52, 1.52, 0, 0))
# await asyncio.sleep(5)
# await drone.offboard.stop()
# await drone.action.land()
