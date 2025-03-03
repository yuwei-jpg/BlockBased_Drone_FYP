
import asyncio
import csv
from mavsdk import System
from mavsdk.action import OrbitYawBehavior
from mavsdk.offboard import VelocityNedYaw,VelocityBodyYawspeed,Attitude,AccelerationNed
from mavsdk.offboard import (OffboardError, PositionNedYaw)

from main_python.csv_generated import generate_s_shape_trajectory_csv
from main_python.read_csv import execute_trajectory_other


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

        # 这里是生成的代码
    await drone.action.arm()
    await drone.offboard.set_position_ned(PositionNedYaw(0,0,0,0))
    await drone.offboard.start()
    await drone.offboard.set_position_ned(PositionNedYaw(0,0,-10,90))
    await asyncio.sleep(10)
    generate_s_shape_trajectory_csv()
    await execute_trajectory_other("s_shape_trajectory.csv", drone)
    await drone.offboard.stop()
    await drone.action.land()
      # 确保所有行都只有 4 个空格缩进

# 执行 run 函数
asyncio.run(run())
