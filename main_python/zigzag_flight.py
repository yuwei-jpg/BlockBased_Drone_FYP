import math
import time

from mavsdk.offboard import PositionNedYaw, VelocityBodyYawspeed
import asyncio


async def zigzag_flight(drone, time_interval, amplitude, speed):
    """
    Achieve Z-shaped flight.

    Parameters：
        drone: drone object
        time_interval: The total number of time steps during the flight
        amplitude: Swing amplitude on the X axis
        speed: flying speed
    """
    # Define initial positions
    z_position = 3  # Constant altitude
    yaw_angle = 0  # Start with a heading of 0 (facing East)

    # First move: horizontally to the left (negative X direction)
    for t in range(time_interval):
        x_position = 0  # Move left for the first swing
        y_position = amplitude * (t % 2)  # Stay at 0 in Y-axis initially

        # Update position and speed
        await drone.offboard.set_position_ned(PositionNedYaw(x_position, y_position, 0, yaw_angle))
        await asyncio.sleep(0.1)
        await drone.offboard.set_velocity_body(VelocityBodyYawspeed(speed, 0, 0, yaw_angle))
        await asyncio.sleep(0.02)

    # After moving left, change heading to 45 degrees (northeast direction)
    yaw_angle = 45  # Set heading to 45 degrees (northeast direction)

    # Second move: towards the northeast at 45 degrees (still moving left)
    for t in range(time_interval):
        x_position = -amplitude * (t % 2)  # Move left for the second swing
        y_position = amplitude * (t % 2)  # Now move diagonally with the Y component

        # Update position and speed with the new heading
        await drone.offboard.set_position_ned(PositionNedYaw(x_position, y_position, z_position, yaw_angle))
        await asyncio.sleep(0.08)
        await drone.offboard.set_velocity_body(VelocityBodyYawspeed(speed, 0, 0, yaw_angle))
        await asyncio.sleep(0.02)

    yaw_angle = 0
    for t in range(time_interval):
        x_position = 0  # Move left for the first swing
        y_position = amplitude * (t % 2)  # Stay at 0 in Y-axis initially

        # Update position and speed
        await drone.offboard.set_position_ned(PositionNedYaw(x_position, y_position, 0, yaw_angle))
        await asyncio.sleep(0.1)
        await drone.offboard.set_velocity_body(VelocityBodyYawspeed(speed, 0, 0, yaw_angle))
        await asyncio.sleep(0.02)
