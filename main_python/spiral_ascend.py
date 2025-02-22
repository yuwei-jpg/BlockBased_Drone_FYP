import math
from mavsdk.offboard import VelocityBodyYawspeed
from mavsdk.offboard import PositionNedYaw
import asyncio


async def spiral_ascend(drone, total_time_steps, radius, speed, angle_speed):
    """
       Achieve spiral upward flight.

       Parameters：
           drone: drone object
           total_time_steps: The total number of time steps during flight
           radius: Spiral radius
           speed: Rising speed
           angle_speed: Angle of rotation per second
       """
    for t in range(total_time_steps):
        # Calculate the current position and rotation
        z_position = t * speed  # Raised Z-axis position
        yaw_angle = angle_speed * t  # Rotation Angle

        # Calculate the horizontal position and move along a circular trajectory
        north_position = radius * math.cos(math.radians(yaw_angle))  # X direction
        east_position = radius * math.sin(math.radians(yaw_angle))  # Y direction

        # Update Location
        await drone.offboard.set_position_ned(PositionNedYaw(north_position, east_position, -z_position, yaw_angle))
        await asyncio.sleep(0.05)

        # Update Speed
        await drone.offboard.set_velocity_body(VelocityBodyYawspeed(0, 0, speed, angle_speed))

        await asyncio.sleep(0.01)
