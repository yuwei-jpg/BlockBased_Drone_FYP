import math

from mavsdk import System
from mavsdk.offboard import VelocityNedYaw, VelocityBodyYawspeed, Attitude, AccelerationNed
from mavsdk.offboard import (PositionNedYaw)

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
    # await drone.offboard.set_position_velocity_ned(PositionNedYaw(0, 0, -10, 0),
    #                                                VelocityNedYaw(0, 0, 1, 0))
    await drone.offboard.set_position_ned(PositionNedYaw(0, 0, -10, 0))
    await asyncio.sleep(10)
    # await spiral_ascend(drone, 100, 7, 1, 8)
    # await zigzag_flight(drone, 10, 10, 6)
    print("-- Begin to do the roller coaster")
    # await roller_coaster(drone, duration=20, rotation_speed=60, altitude=10)
    # 阶段1: 垂直爬升（向上加速）
    await drone.offboard.set_attitude(Attitude(0, 30, 0, 0))
    await drone.offboard.set_position_velocity_acceleration_ned(
        PositionNedYaw(0, 0, -10, 0),  # 目标高度10米
        VelocityNedYaw(0, 0, -2, 0),  # 向上速度2m/s
        AccelerationNed(0, 0, -1)  # 向上加速1m/s²
    )
    await asyncio.sleep(5)  # 持续5秒

    # 阶段2: 俯冲（向前俯冲+姿态下压）
    await drone.offboard.set_attitude(Attitude(0, -60, 0, 0))  # 俯仰30度
    await drone.offboard.set_velocity_body(VelocityBodyYawspeed(5, 0, 3, 0))  # 前向5m/s，向下3m/s
    await asyncio.sleep(3)
    print("-- roller coaster")

    await asyncio.sleep(10)
    await drone.offboard.stop()
    await drone.action.land()

    # Make sure all lines have only 4 spaces


# Execute the run()
asyncio.run(run())
